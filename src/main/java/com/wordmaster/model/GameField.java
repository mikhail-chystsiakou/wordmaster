package com.wordmaster.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameField {
    private static final Logger logger = LoggerFactory.getLogger(GameField.class);
    public final static int FIELD_WIDTH = 5;
    public final static int FIELD_HEIGHT = 5;
    private char[][] field = new char[FIELD_HEIGHT][FIELD_WIDTH];

    public GameField(String word) {
        System.out.println("Game filed initialized with word " + word);
        if (word.length() != FIELD_WIDTH) {
            logger.error("Invalid size of GameField base word {}", word);
            throw new IllegalArgumentException("Illegal word size");
        }
        for (int i = 0; i < FIELD_WIDTH; i++) {
            field[FIELD_HEIGHT/2][i] = word.charAt(i);
        }
    }

    public class FieldCell {
        int x;
        int y;

        public FieldCell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public char getValue() {
            return field[x][y];
        }

        public FieldCell getLeft() {
            if (x > 0) {
                return new FieldCell(x-1, y);
            } else return null;
        }
        // ...
    }
    public class GameFieldWord {
        private FieldCell[] word;
    }
}
