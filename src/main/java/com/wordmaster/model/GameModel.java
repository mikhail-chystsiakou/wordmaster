package com.wordmaster.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class GameModel {
    private static final Logger logger = LoggerFactory.getLogger(GameModel.class);
    private List<Player> playerList = new LinkedList<>();
    private GameField gameField;
    private Player currentPlayer;
    private static GameModel instance;

    private GameModel(List<Player> players, String baseWord) {
        playerList.addAll(players);
        gameField = new GameField(baseWord);
    }

    public static void initialize (List<Player> players, String baseWord) {
        instance = new GameModel(players, baseWord);
    }

    public static GameModel getInstance() {
        if (instance == null) {
            logger.error("Trying to get uninitialized model instance");
            throw new IllegalStateException("Model is not initialized");
        }
        return instance;
    }
    public static boolean isInitialized() {
        return (instance == null);
    }
}
