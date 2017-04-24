package com.wordmaster.model;

public class Player {
    public static final int MAX_NAME_LENGTH = 10;
    protected String name;
    protected int score;
    protected int gameTime;
    protected int moveTime;

    public Player(String name, int gameTime, int moveTime) {
        this.name = name;
    }
}
