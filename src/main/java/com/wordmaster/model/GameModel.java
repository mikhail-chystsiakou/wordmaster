package com.wordmaster.model;

import com.wordmaster.model.algorithm.Vocabulary;
import com.wordmaster.model.exception.ModelInitializeException;
import com.wordmaster.model.exception.ModelOperationException;
import com.wordmaster.model.exception.ModelStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

// annotate with jaxb.
// need:
// 1) list of commands
// 2) players (scores and names)
// 3) field
public class GameModel {
    private static final Logger logger = LoggerFactory.getLogger(GameModel.class);
    private List<ModelAware> modelListeners = new LinkedList<>();

    private List<Move> moves = new LinkedList<>();
    private int currentMove;

    private List<Player> playerList = new LinkedList<>();
    private int currentPlayer;

    private GameField gameField;
    private Vocabulary vocabulary;
    private ModelScheduler scheduler;

    private ModelOperation currentOperation;
    private enum ModelOperationType {
        MAKE_MOVE, GENERATE_MOVE, UNDO, REDO
    }


    public GameModel(List<Player> players, Vocabulary vocabulary, String baseWord) {
        playerList.addAll(players);
        this.vocabulary = vocabulary;
        try {
            gameField = new GameField(baseWord);
        } catch (IllegalArgumentException e) {
            throw new ModelInitializeException("Illegal GameField initialization", e);
        }
        scheduler = new ModelScheduler(this);
        scheduler.runModelThread();
    }

    void modelThread() {
        logger.trace("model thread begin");
        while (true) {
            if (scheduler.checkDeath()) return;
            scheduler.waitForMove();
            logger.trace("model got operation");
            currentOperation.performOperation();
            scheduler.endOperation();
        }
    }

    public void makeMove(char letter,
                            GameField.Cell letterCell,
                            GameField.Word resultWord) throws ModelStateException {
        scheduler.checkOperationInProgressAndLock();
        logger.trace("make move begin");
        Move move = new Move();
        move.setNewCellValue(letter);
        move.setCell(letterCell);
        move.setResultWord(resultWord);

        currentOperation = new ModelOperation(ModelOperationType.MAKE_MOVE);
        currentOperation.setMove(move);
        scheduler.startOperation();
        logger.trace("make move end");
    }

    public void generateMove(GameField.Cell cell) throws ModelStateException {
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
        scheduler.startOperation();
    }

    public void redo() throws ModelStateException {
        scheduler.checkOperationInProgressAndLock();

        currentOperation = new ModelOperation(ModelOperationType.REDO);
        scheduler.startOperation();
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
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

    public Player getCurrentPlayer() {
        return playerList.get(currentPlayer);
    }

    public Player getPreviousPlayer() {
        if (currentPlayer == 0) {
            return playerList.get(playerList.size()-1);
        } else return playerList.get(currentPlayer-1);
    }

    public List<Player> getPlayers() {
        return playerList;
    }

    public int getCurrentMove() {
        return currentMove;
    }

    public void destroy() {
        // send kill message to model
        scheduler.destroyModelThread();
    }

    private void emitMoveEvent() {
        new Thread(()-> {
            modelListeners.forEach(ModelAware::onMove);
        }).start();
    }

    private void emitFinishEvent() {
        new Thread(()-> {
            modelListeners.forEach(ModelAware::onMove);
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

        ModelOperation(ModelOperationType type) {
            this.type = type;
        }

        void setMove(Move move) {
            this.move = move;
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
            // check move
            scheduler.checkAndSleep();
            applyMove(move);
        }
        void generateMove() {
            // generate move
            // move = ...
            scheduler.checkAndSleep();
            applyMove(move);
        }

        private void applyMove(Move move) {
            //IF NOT REDO delete all commands behind current
            moves.removeAll(moves.subList(currentMove, moves.size()));

            // set cell to the game field
            move.getCell().setValue(move.getNewCellValue());

            // add this to model command list
            moves.add(move);

            // increase model current move
            currentMove++;

            // set word to player
            playerList.get(currentPlayer).addWord(move.getResultWord().toString());

            // switch to the next player
            if (currentPlayer == playerList.size()-1) {
                currentPlayer = 0;
            } else {
                currentPlayer++;
            }
            emitMoveEvent();
        }

        private void undo() {
            scheduler.checkAndSleep();

            //cell.setValue(prevCellValue);
            // unset cell changes
            //model.currentMove--;
            // decrease model current move
        }

        private void redo() {
            scheduler.checkAndSleep();
        }
    }
}
