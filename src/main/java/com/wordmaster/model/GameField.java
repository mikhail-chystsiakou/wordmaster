package com.wordmaster.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class GameField {
    private static final Logger logger = LoggerFactory.getLogger(GameField.class);
    public final static int FIELD_WIDTH = 5;
    public final static int FIELD_HEIGHT = 5;
    public final static int MIN_START_WORD_SIZE = 3;
    private char[][] field = new char[FIELD_HEIGHT][FIELD_WIDTH];
    private Word lastWord;

    GameField(String word) {
        System.out.println("Game filed initialized with word " + word);
        if (word.length() > FIELD_WIDTH || word.length() == 0) {
            logger.error("Invalid size of GameField base word {}", word);
            throw new IllegalArgumentException("Illegal word size");
        }
        int wordStartPosition = (FIELD_WIDTH-word.length())/2;
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_HEIGHT; x++) {
                field[y][x] = ' ';
            }
        }
        for (int i = 0; i < word.length(); i++) {
            field[FIELD_HEIGHT/2][wordStartPosition+i] = word.charAt(i);
        }
    }

    public Cell getCell(int x, int y) {
        return new Cell(x, y);
    }

    public class Cell {
        int x;
        int y;

        private Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public char getValue() {
            return field[y][x];
        }

        void setValue(char value) {
            field[y][x] = value;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isNear(Cell cell) {
            if (cell == null) return false;
            boolean xNear = Math.abs(cell.getX() - x) < 2;
            boolean yNear = Math.abs(cell.getY() - y) < 2;
            return xNear && yNear && (x == cell.getX() || y == cell.getY());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof Cell) {
                Cell obj2compare = (Cell) obj;
                return (x == obj2compare.getX() && y == obj2compare.getY());
            } else return false;
        }

        public Cell getLeft() {
            if (x > 0) {
                return new Cell(x-1, y);
            } else return null;
        }
        public Cell getRight() {
            if (x < FIELD_WIDTH - 1) {
                return new Cell(x+1, y);
            } else return null;
        }

        public Cell getTop() {
            if (y > 0) {
                return new Cell(x, y-1);
            } else return null;
        }

        public Cell getBottom() {
            if (y < FIELD_HEIGHT - 1) {
                return new Cell(x, y+1);
            } else return null;
        }

        public boolean isStandalone() {
            Cell left = getLeft();
            Cell right = getRight();
            Cell top = getTop();
            Cell bottom = getBottom();
            boolean leftStandalone = (left == null || left.getValue() == ' ');
            boolean rightStandalone = (right == null || right.getValue() == ' ');
            boolean topStandalone = (top == null || top.getValue() == ' ');
            boolean bottomStandalone = (bottom == null || bottom.getValue() == ' ');
            return (leftStandalone && rightStandalone && topStandalone && bottomStandalone);
        }
    }
    public static class Word {
        private LinkedList<Cell> word = new LinkedList<>();

        public void pushLetter(Cell letter) {
            word.offerLast(letter);
        }

        public boolean isEmpty() {
            return word.isEmpty();
        }
        public Cell popLetter() {
            return word.pollLast();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Cell cell : word) {
                sb.append(cell.getValue());
            }
            return sb.toString();
        }
        public boolean contains(Cell cell) {
            return word.contains(cell);
        }
    }
}
