package com.wordmaster.model;

import java.util.LinkedList;
import java.util.List;

public class Player {
    public static final int MAX_NAME_LENGTH = 10;
    public static final int MIN_NAME_LENGTH = 2;
    protected String name;
    protected int score;
    protected int gameTime;
    protected int moveTime;
    protected List<String> words = new LinkedList<>();


    public Player(String name, int gameTime, int moveTime) {
        this.name = name;
    }

    void addWord(String newWord) {
        score += newWord.length();
        words.add(newWord);
    }

    public String getName() {
        return name;
    }

    public List<String> getWords() {
        return words;
    }

    public String getLastWord() {
        return (words.size() > 0) ? words.get(words.size()-1) : null;
    }

    public int getScore() {
        return score;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Player) {
            Player obj2compare = (Player) obj;
            return obj2compare.name.equals(name);
        } else return false;
    }
}
