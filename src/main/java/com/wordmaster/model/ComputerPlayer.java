package com.wordmaster.model;

public class ComputerPlayer extends Player {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    private Difficulty difficulty;
    private int delay;

    public ComputerPlayer(String name, int gameTime, int moveTime, Difficulty difficulty, int delay) {
        super(name, gameTime, moveTime);
        this.difficulty = difficulty;
        this.delay = delay;
    }

    public boolean isComputer() {
        return true;
    }
}
