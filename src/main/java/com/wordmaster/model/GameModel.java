package com.wordmaster.model;

import com.wordmaster.model.algorithm.Algorithm;
import com.wordmaster.model.algorithm.Vocabulary;
import com.wordmaster.model.exception.ModelException;
import com.wordmaster.model.exception.ModelInitializeException;
import com.wordmaster.model.exception.UnsupportedModelOperationException;
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

/**
 * Represents one game object. Takes care about it state
 * and provides game manipulation interface. Has separate
 * threads to notify subscribers about events and to perform
 * game operations like move, undo or redo. Can b
 */
@XmlRootElement(name="game")
@XmlType(propOrder = {
        "playerList",
        "currentMove",
        "gameField",
        "moves"})
@XmlAccessorType(XmlAccessType.NONE)
public class GameModel {
    private static final Logger logger = LoggerFactory.getLogger(GameModel.class);
    private List<ModelAware> modelListeners = Collections.synchronizedList(new LinkedList<>());

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
    private NotificationThread notificationThread;

    private ModelOperation currentOperation;
    private enum ModelOperationType {
        MAKE_MOVE, GENERATE_MOVE, UNDO, REDO
    }

    private Move suggestion;
    private boolean isReplay;

    // for the jaxb
    public GameModel() {

    }

    /**
     * Creates new game model from the specified player list, words vocabulary and
     * game field base word.
     *
     * @param players       list of players
     * @param vocabulary    vocabulary
     * @param baseWord      the base word
     */
    public GameModel(List<Player> players, Vocabulary vocabulary, String baseWord) {
        playerList.addAll(players);
        playerList.forEach((Player player) -> {
            if (player.isComputer()) {
                addModelListener((ComputerPlayer)player);
            }
        });
        try {
            gameField = new GameField(baseWord);
        } catch (IllegalArgumentException e) {
            throw new ModelInitializeException("Illegal GameField initialization", e);
        }
        algorithm = new Algorithm(gameField, vocabulary);
        scheduler = new ModelScheduler(this);
        notificationThread = new NotificationThread();
        notificationThread.start();
        scheduler.runModelThread();
    }

    /**
     * Thread that performs all model operations.
     */
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
            emitMoveEvent();
        }
    }

    /**
     * Publish move operation to the model thread.
     *
     * @param letter        set letter
     * @param letterCell    set cell
     * @param resultWord    result word
     * @throws ModelStateException  if another operation in progress
     */
    public void makeMove(char letter,
                            GameField.Cell letterCell,
                            GameField.Word resultWord) throws ModelStateException {
        if(isReplay) {
            throw new ModelStateException("Model was loaded in replay mode, cannot make move", null);
        }
        Move move = new Move();
        move.setNewCellValue(letter);
        move.setCell(letterCell);
        move.setResultWord(resultWord);

        currentOperation = new ModelOperation(ModelOperationType.MAKE_MOVE);
        currentOperation.setMove(move);

        scheduler.startOperation();
    }

    /**
     * Publish move generation operation to the model thread.
     *
     * @throws ModelStateException  if another operation in progress
     */
    public void generateMove() throws ModelStateException {
        if(isReplay) {
            throw new ModelStateException("Model was loaded in replay mode, cannot make move", null);
        }
        Move move = new Move();
        currentOperation = new ModelOperation(ModelOperationType.GENERATE_MOVE);
        currentOperation.setMove(move);

        scheduler.startOperation();
    }

    /**
     * Publish undo operation to the model thread. If replay, redo 1 time,
     * if not reply, redo 2 times.
     *
     * @throws ModelStateException if another operation in progress
     */
    public void undo() throws ModelStateException {
        currentOperation = new ModelOperation(ModelOperationType.UNDO);
        if(isReplay) {
            currentOperation.setUndoRedoTimes(1);
        }

        scheduler.startOperation();
    }

    /**
     * Public redo operation to model thread. If replay, redo 1 time,
     * if not reply, redo 2 times.
     *
     * @throws ModelStateException if another operation in progress
     */
    public void redo() throws ModelStateException {
        currentOperation = new ModelOperation(ModelOperationType.REDO);
        if(isReplay) {
            currentOperation.setUndoRedoTimes(1);
        }

        scheduler.startOperation();
    }

    /**
     * Saves model state to the file. Block model changes until complete.
     * Need to call startGame() after loading.
     *
     * @param file path to save
     */
    public void save(File file) {
        scheduler.freeze();
        try {
            JAXBContext context =
                    JAXBContext.newInstance( this.getClass(), Player.class,
                            ComputerPlayer.class, GameField.class, Move.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.marshal(this, file);
        } catch (JAXBException e) {
            logger.warn("Cannot marshal model to file {}", file.getName(), e);
            throw new ModelException("Cannot marshal model to file" + file.getName(), e);
        }
        scheduler.unfreeze();
    }

    /**
     * Loads and initializes game from file. Need to call startGame()
     * after loading.
     *
     * @param file      file with save
     * @param vocabulary    vocabulary to use
     * @param isReplay  is loaded model is replay
     * @return  loaded game model
     */
    public static GameModel load(File file, Vocabulary vocabulary, boolean isReplay) {
        try {
            JAXBContext context = JAXBContext.newInstance( GameModel.class, Player.class,
                    ComputerPlayer.class, GameField.class, Move.class);
            Unmarshaller um = context.createUnmarshaller();
            GameModel loadedModel = (GameModel)um.unmarshal(file);
            loadedModel.algorithm = new Algorithm(loadedModel.gameField, vocabulary);
            loadedModel.scheduler = new ModelScheduler(loadedModel);

            loadedModel.isReplay = isReplay;
            if (isReplay) {
                loadedModel.currentMove = 0;    // implicit for the replay case
                loadedModel.playerList.forEach(Player::clearWords);
                loadedModel.gameField.clear();
            }
            loadedModel.playerList.forEach((Player player) ->  {
                if (player.isComputer()) {
                    loadedModel.addModelListener((ComputerPlayer)player);
                }
            });
            loadedModel.notificationThread = new NotificationThread();

            loadedModel.notificationThread.start();
            loadedModel.scheduler.runModelThread();
            return loadedModel;
        } catch (JAXBException e) {
            logger.warn("Cannot marshal model to file {}", file.getName(), e);
            throw new ModelException("Cannot marshal model to file" + file.getName(), e);
        }
    }

    /**
     * Prohibits all changes in model until resume()
     */
    public void pause() {
        scheduler.freeze();
    }

    /**
     * Allows model changes.
     */
    public void resume() {
        scheduler.unfreeze();
    }

    /**
     * Starts model game logic and internal threads.
     */
    public void startGame() {
        if (playerList.get(currentPlayer).isComputer()) {
            notificationThread.addTask(() -> {
                ((ComputerPlayer)playerList.get(currentPlayer)).onMove(this);
            });
        } else {
            analyzePosition();
        }
    }

    /**
     * Finishes game with the win of second player. Logic can change
     * in future releases.
     */
    public void surrender() {
        if (playerList.size() == 2) {
            winners.add(playerList.get(getPreviousPlayer()));
            emitFinishEvent();
            destroy();
        } else {
            // hook for the later development
        }
    }

    /**
     * Game field getter.
     *
     * @return model game field
     */
    public GameField getGameField() {
        return gameField;
    }

    /**
     * Allows to subscribe on model events.
     *
     * @param listener listener to subscribe
     */
    public void addModelListener(ModelAware listener) {
        modelListeners.add(listener);
    }

    /**
     * Allows subscribes to go out from subscribe list.
     *
     * @param listener listener to unsubscribe
     */
    public void removeModelListener(ModelAware listener) {
        notificationThread.addTask(() -> {
            modelListeners.remove(listener);
        });
    }

    /**
     * Returns the previous moving player number.
     *
     * @return number of player
     */
    private int getPreviousPlayer() {
        if (currentPlayer == 0) {
            return playerList.size()-1;
        } else return currentPlayer-1;
    }

    /**
     * Returns the current player object.
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        return playerList.get(currentPlayer);
    }

    /**
     * Returns the next moving player.
     *
     * @return number of player
     */
    private int getNextPlayer() {
        if (currentPlayer == playerList.size()-1) {
            return 0;
        } else return currentPlayer+1;
    }

    /**
     * Getter for the players list
     *
     * @return player list
     */
    public List<Player> getPlayers() {
        return playerList;
    }

    /**
     * Getter for the winners list
     *
     * @return winners list
     */
    public List<Player> getWinners() {return winners;}

    /**
     * Getter for the current move
     *
     * @return current move number
     */
    public int getCurrentMove() {
        return currentMove;
    }

    /**
     * Getter for replay property
     *
     * @return  true if model was started in
     *          replay mode, false otherwise
     */
    public boolean isReplay() {
        return isReplay;
    }

    /**
     * Checks if model can perform undo operation.
     *
     * @return true if model can perform undo
     */
    public boolean canUndo() {
        if (isReplay) return currentMove > 0;
        return currentMove > 1;
    }

    /**
     * Checks if model can perform redo operation.
     *
     * @return true if model can redo
     */
    public boolean canRedo() {
        if (isReplay) return (moves.size() - currentMove) > 0;
        return (moves.size() - currentMove) > 1;
    }

    /**
     * Rises kill flags on model threads. Model can not be
     * used after calling this method
     */
    public void destroy() {
        // send kill message to model
        notificationThread.raiseDeath();
        scheduler.raiseDeath();
    }

    /**
     * Calculates winner list
     */
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

    /**
     * Checks if there is any legal move. If not, finishes the game.
     */
    private void analyzePosition() {
        List<String> allPlayerWords = new LinkedList<>();
        playerList.forEach((Player p) -> {
            allPlayerWords.addAll(p.getWords());
        });
        allPlayerWords.add(gameField.getStartWord());

        algorithm.predictMove((Move m) -> {
            if (m == null) {
                // game ends;
                detectWinners();
                emitFinishEvent();
                destroy();
            } else {
                suggestion = m;
                logger.debug("There is at least one move: {}",
                        m.getResultWordAsString(gameField));
            }
        }, allPlayerWords);
    }

    /**
     * Notifies subscribers about game move.
     */
    private void emitMoveEvent() {
        notificationThread.addTask(() -> {
            pause();
            modelListeners.forEach((ModelAware ma) -> {
                ma.onMove(this);
            });
            resume();
        });
    }

    /**
     * Notifies subscribers about game ends.
     */
    private void emitFinishEvent() {
        notificationThread.addTask(() -> {
            pause();
            modelListeners.forEach((ModelAware ma) -> {
                ma.onFinish(this);
            });
            resume();
        });
    }

    /**
     * Notifies subscribers about invalid move.
     */
    private void emitInvalidMoveEvent(int type) {
        notificationThread.addTask(() -> {
            pause();
            modelListeners.forEach((ModelAware ma) -> ma.onInvalidMove(this, type));
            resume();
        });
    }

    /**
     * The execution unit, that can be performed on model. Only one
     * model operation can be run at single moment. Before applying
     * model changes, checks for model sleep.
     *
     * @author Mike
     * @version 1.0
     */
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

        /**
         * Detect wich operation to perform and performs it
         */
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
                    throw new UnsupportedModelOperationException("Unsupported model operation type", null);
                }
            }
        }

        /**
         * Represents move operation. Move will be validated before apply
         */
        void makeMove() {
            if (!algorithm.validateMove(move)) {
                emitInvalidMoveEvent(Move.INVALID_WORD);
                return;
            }
            boolean alreadyExists = false;
            String moveWord = move.getResultWordAsString(gameField);
            for (Player player : playerList) {
                if (player.getWords().contains(moveWord)) alreadyExists = true;
            }
            if (gameField.getStartWord().equals(moveWord)) alreadyExists = true;
            if (alreadyExists) {
                emitInvalidMoveEvent(Move.ALREADY_USED);
                return;
            }
            applyMove(move);
        }

        /**
         * Represents move generation operation.
         */
        void generateMove() {
            List<String> allPlayerWords = new LinkedList<>();
            playerList.forEach((Player p) -> {
                allPlayerWords.addAll(p.getWords());
            });
            allPlayerWords.add(gameField.getStartWord());
            List<Move> generatedMoves = algorithm.generateWithout(allPlayerWords);

            if (generatedMoves == null || generatedMoves.size() == 0) {
                detectWinners();
                emitFinishEvent();
                destroy();
                return;
            }
            if (playerList.get(currentPlayer).isComputer()) {
                move = ((ComputerPlayer)playerList.get(currentPlayer)).selectMove(generatedMoves);
            } else {
                move = generatedMoves.get(0);
            }
            applyMove(move);
        }

        /**
         * Applies move. Deletes all information about possible redo operations.
         * @param move move to apply
         */
        private void applyMove(Move move) {
            scheduler.applyOperation();
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
            if (!playerList.get(currentPlayer).isComputer()) {
                analyzePosition();
            }
        }

        /**
         * Represents undo operation.
         */
        private void undo() {
            if (currentMove < undoRedoTimes) {
                throw new ModelStateException("No moves to undo", null);
            }
            scheduler.applyOperation();
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
        }

        /**
         * Represents redo operation.
         */
        private void redo() {
            if (currentMove + undoRedoTimes > moves.size()) {
                throw new ModelStateException("No moves to redo", null);
            }

            scheduler.applyOperation();
            for (int i = 0; i < undoRedoTimes; i++) {
                Move moveToRedo = moves.get(currentMove);

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
    }
}
