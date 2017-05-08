package com.wordmaster.model;

import com.wordmaster.model.algorithm.Algorithm;
import com.wordmaster.model.algorithm.Vocabulary;
import com.wordmaster.model.exception.ModelException;
import com.wordmaster.model.exception.ModelInitializeException;
import com.wordmaster.model.exception.ModelOperationException;
import com.wordmaster.model.exception.ModelStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name="game")
@XmlType(propOrder = {
        "playerList",
        "currentMove",
        "gameField",
        "moves"})
@XmlAccessorType(XmlAccessType.NONE)
public class GameModel {
    private static final Logger logger = LoggerFactory.getLogger(GameModel.class);
    private List<ModelAware> modelListeners = new LinkedList<>();

    @XmlElement(name="move")
    @XmlElementWrapper(name="moves")
    private List<Move> moves = Collections.synchronizedList(new LinkedList<>());

    @XmlElement
    private int currentMove;

    @XmlElement(name="player")
    @XmlElementWrapper(name="players")
    private List<Player> playerList = Collections.synchronizedList(new LinkedList<>());
    private int currentPlayer;
    private List<Player> winners = Collections.synchronizedList(new LinkedList<>());

    @XmlElement
    private GameField gameField;
    private Algorithm algorithm;
    private ModelScheduler scheduler;

    private ModelOperation currentOperation;
    private enum ModelOperationType {
        MAKE_MOVE, GENERATE_MOVE, UNDO, REDO
    }

    private Move suggestion;
    private boolean isReplay;

    // for the jaxb
    public GameModel() {

    }

    public GameModel(List<Player> players, Vocabulary vocabulary, String baseWord) {
        playerList.addAll(players);
        try {
            gameField = new GameField(baseWord);
        } catch (IllegalArgumentException e) {
            throw new ModelInitializeException("Illegal GameField initialization", e);
        }
        algorithm = new Algorithm(gameField, vocabulary);
        scheduler = new ModelScheduler(this);
        scheduler.runModelThread();
    }

    void modelThread() {
        logger.trace("Model thread started");
        while (true) {
            if (scheduler.checkDeath()) {
                logger.trace("Model thread death");
                return;
            }
            scheduler.waitForMove();
            if (currentOperation != null) {
                currentOperation.performOperation();
                currentOperation = null;
            }
            scheduler.endOperation();
        }
    }

    public void makeMove(char letter,
                            GameField.Cell letterCell,
                            GameField.Word resultWord) throws ModelStateException {
        if(isReplay) {
            throw new ModelStateException("Model was loaded in replay mode, cannot make move", null);
        }
        scheduler.checkOperationInProgressAndLock();
        Move move = new Move();
        move.setNewCellValue(letter);
        move.setCell(letterCell);
        move.setResultWord(resultWord);

        currentOperation = new ModelOperation(ModelOperationType.MAKE_MOVE);
        currentOperation.setMove(move);
        scheduler.startOperation();
    }

    public void generateMove(GameField.Cell cell) throws ModelStateException {
        if(isReplay) {
            throw new ModelStateException("Model was loaded in replay mode, cannot make move", null);
        }

        scheduler.checkOperationInProgressAndLock();

        Move move = new Move();
        move.setCell(cell);

        currentOperation = new ModelOperation(ModelOperationType.GENERATE_MOVE);
        currentOperation.setMove(move);
        scheduler.startOperation();
    }

    public void undo() throws ModelStateException {
        scheduler.checkOperationInProgressAndLock();

        currentOperation = new ModelOperation(ModelOperationType.UNDO);
        if(isReplay) {
            currentOperation.setUndoRedoTimes(1);
        }
        scheduler.startOperation();
    }

    public void redo() throws ModelStateException {
        scheduler.checkOperationInProgressAndLock();

        currentOperation = new ModelOperation(ModelOperationType.REDO);
        if(isReplay) {
            currentOperation.setUndoRedoTimes(1);
        }
        scheduler.startOperation();
    }

    public void save(File file) {
        scheduler.raiseSleep();
        try {
            JAXBContext context =
                    JAXBContext.newInstance( this.getClass(), Player.class,
                                                GameField.class, Move.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.marshal(this, file);
        } catch (JAXBException e) {
            logger.warn("Cannot marshal model to file {}", file.getName(), e);
            throw new ModelException("Cannot marshal model to file" + file.getName(), e);
        }
        scheduler.awake();
    }

    public static GameModel load(File file, Vocabulary vocabulary, boolean isReplay) {
        try {
            JAXBContext context = JAXBContext.newInstance( GameModel.class, Player.class,
                                                            GameField.class, Move.class);
            Unmarshaller um = context.createUnmarshaller();
            GameModel loadedModel = (GameModel)um.unmarshal(file);
            loadedModel.algorithm = new Algorithm(loadedModel.gameField, vocabulary);
            loadedModel.scheduler = new ModelScheduler(loadedModel);
            loadedModel.scheduler.runModelThread();
            loadedModel.isReplay = isReplay;
            if (isReplay) {
                loadedModel.currentMove = 0;    // implicit for the replay case
                loadedModel.playerList.forEach(Player::clearWords);
            }
            return loadedModel;
        } catch (JAXBException e) {
            logger.warn("Cannot marshal model to file {}", file.getName(), e);
            throw new ModelException("Cannot marshal model to file" + file.getName(), e);
        }
    }

    public void pause() {
        scheduler.raiseSleep();
    }

    public void resume() {
        scheduler.awake();
    }

    public void surrender() {
        if (playerList.size() == 2) {
            winners.add(playerList.get(getPreviousPlayer()));
            scheduler.raiseDeath();
            emitFinishEvent();
        } else {
            // hook for the later development
        }
    }

    public GameField getGameField() {
        return gameField;
    }

    public void addModelListener(ModelAware listener) {
        modelListeners.add(listener);
    }

    public void removeModelListener(ModelAware listener) {
        modelListeners.remove(listener);
    }

    private int getPreviousPlayer() {
        if (currentPlayer == 0) {
            return playerList.size()-1;
        } else return currentPlayer-1;
    }
    public Player getCurrentPlayer() {
        return playerList.get(currentPlayer);
    }
    private int getNextPlayer() {
        if (currentPlayer == playerList.size()-1) {
            return 0;
        } else return currentPlayer+1;
    }

    public List<Player> getPlayers() {
        return playerList;
    }
    public List<Player> getWinners() {return winners;}

    public int getCurrentMove() {
        return currentMove;
    }

    public boolean isReplay() {
        return isReplay;
    }

    public boolean canUndo() {
        if (isReplay) return currentMove > 0;
        return currentMove > 1;
    }
    public boolean canRedo() {
        if (isReplay) return (moves.size() - currentMove) > 0;
        return (moves.size() - currentMove) > 1;
    }

    public void destroy() {
        // send kill message to model
        scheduler.raiseDeath();
    }

    private void detectWinners() {
        winners.add(playerList.get(0));
        for (Player player : playerList.subList(1, playerList.size())) {
            if (player.getScore() < winners.get(0).getScore()) continue;

            if (player.getScore() > winners.get(0).getScore()) {
                winners.clear();
            }
            winners.add(player);
        }
    }

    private void emitMoveEvent() {
        new Thread(()-> {
            modelListeners.forEach(ModelAware::onMove);
        }).start();
    }

    private void emitFinishEvent() {
        new Thread(()-> {
            modelListeners.forEach(ModelAware::onFinish);
        }).start();
    }

    private void emitInvalidMoveEvent() {
        new Thread(()-> {
            modelListeners.forEach(ModelAware::onInvalidMove);
        }).start();
    }

    private class ModelOperation {
        private Move move;
        private ModelOperationType type;
        private int undoRedoTimes = 2;

        ModelOperation(ModelOperationType type) {
            this.type = type;
        }

        void setMove(Move move) {
            this.move = move;
        }

        void setUndoRedoTimes(int undoRedoTimes) {
            this.undoRedoTimes = undoRedoTimes;
        }

        void performOperation() {
            switch (type) {
                case MAKE_MOVE: {
                    makeMove();
                    break;
                }
                case GENERATE_MOVE: {
                    generateMove();
                    break;
                }
                case UNDO: {
                    undo();
                    break;
                }
                case REDO: {
                    redo();
                    break;
                }
                default: {
                    logger.error("Unsupported model operation type");
                    throw new ModelOperationException("Unsupported model operation type", null);
                }
            }
        }

        void makeMove() {
            if (!algorithm.validateMove(move)) {
                emitInvalidMoveEvent();
                return;
            }
            scheduler.checkAndSleep();
            applyMove(move);
        }

        void generateMove() {
            move = algorithm.generateMove();
            if (move == null) {
                detectWinners();
                scheduler.raiseDeath();
                emitFinishEvent();
                return;
            }
            scheduler.checkAndSleep();
            applyMove(move);
        }

        private void applyMove(Move move) {
            //IF NOT REDO delete all commands behind current
            moves.removeAll(new LinkedList<>(moves.subList(currentMove, moves.size())));

            // set cell to the game field
            move.getCell(gameField).setValue(move.getNewCellValue());

            moves.add(move);

            // increase model current move
            currentMove++;

            // set word to player
            playerList.get(currentPlayer).addWord(move.getResultWord(gameField).toString());

            // switch to the next player
            if (currentPlayer == playerList.size()-1) {
                currentPlayer = 0;
            } else {
                currentPlayer++;
            }
            suggestion = null;
            emitMoveEvent();

            if (!playerList.get(currentPlayer).isComputer()) {
                // if human, run analyze in background
                algorithm.predictMove((Move m) -> {
                    if (m == null) {
                        // game ends;
                        detectWinners();
                        scheduler.raiseDeath();
                        emitFinishEvent();
                    } else {
                        suggestion = m;
                    }
                });
            }
        }

        private void undo() {
            scheduler.checkAndSleep();
            if (currentMove < undoRedoTimes) {
                throw new ModelStateException("No moves to undo", null);
            }
            for (int i = 0; i < undoRedoTimes; i++) {
                Move previousMove = moves.get(currentMove - 1);
                if (isReplay) {
                    playerList.get(getPreviousPlayer()).removeLastWord();
                } else {
                    playerList.get(currentPlayer).removeLastWord();
                }
                previousMove.getCell(gameField).setValue(previousMove.getPrevCellValue());
                currentMove--;
                currentPlayer = getPreviousPlayer();
            }
            emitMoveEvent();
        }

        private void redo() {
            scheduler.checkAndSleep();
            int j = 0;
            if (currentMove + undoRedoTimes - 1 < moves.size()) {
                for (int i = 0; i < undoRedoTimes; i++) {
                    Move moveToRedo = moves.get(currentMove);
                    logger.trace("word {} from the {} move", moveToRedo.getResultWord(gameField), currentMove);

                    // set cell to the game field
                    moveToRedo.getCell(gameField).setValue(moveToRedo.getNewCellValue());

                    // set word to player
                    if (isReplay) {
                        playerList.get(currentPlayer).addWord(moveToRedo.getResultWord(gameField).toString());
                    } else {
                        playerList.get(getNextPlayer()).addWord(moveToRedo.getResultWord(gameField).toString());
                    }
                    // switch to the next player
                    currentPlayer = getNextPlayer();

                    // increase model current move
                    currentMove++;
                }
            }
            emitMoveEvent();
        }
    }
}
