package com.wordmaster.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import java.util.*;

@XmlType
public class GameField {
    private static final Logger logger = LoggerFactory.getLogger(GameField.class);
    public final static int FIELD_WIDTH = 5;
    public final static int FIELD_HEIGHT = 5;
    public final static int MIN_START_WORD_SIZE = 3;
    public final static int MAX_START_WORD_SIZE = 5;
    public final static char EMPTY_CELL_VALUE = ' ';
    @XmlElement
    private char[][] field = new char[FIELD_HEIGHT][FIELD_WIDTH];
    @XmlAttribute
    private String startWord;

    // for the jaxb
    public GameField() {

    }

    GameField(String word) {
        if (word.length() > FIELD_WIDTH || word.length() == 0) {
            logger.error("Invalid size of GameField base word {}", word);
            throw new IllegalArgumentException("Illegal word size");
        }
        this.startWord = word;
        clear();
    }

    public List<Cell> getAvailableCells() {
        List<Cell> availableCells = new LinkedList<>();
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_HEIGHT; x++) {
                Cell cell = getCell(x, y);
                if (cell.isEmpty() && !cell.isStandalone()) availableCells.add(cell);
            }
        }
        return availableCells;
    }

    void clear() {
        int wordStartPosition = (FIELD_WIDTH-startWord.length())/2;
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_HEIGHT; x++) {
                field[y][x] = EMPTY_CELL_VALUE;
            }
        }
        for (int i = 0; i < startWord.length(); i++) {
            field[FIELD_HEIGHT/2][wordStartPosition+i] = startWord.charAt(i);
        }
    }

    public Cell getCell(int x, int y) {
        return new Cell(x, y);
    }

    public Word getWord(int[][] arr) {
        Word word = new Word();
        for (int[] cell : arr) {
            word.pushLetter(getCell(cell[0], cell[1]));
        }
        return word;
    }

    public String getStartWord() {
        return startWord;
    }

    public class Cell {
        int x;
        int y;

        private Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean isEmpty() {
            return getValue() == EMPTY_CELL_VALUE;
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

        public List<Cell> getNearCells() {
            LinkedList<Cell> nearCells = new LinkedList<>();
            if (getLeft() != null) nearCells.add(getLeft());
            if (getRight() != null) nearCells.add(getRight());
            if (getTop() != null) nearCells.add(getTop());
            if (getBottom() != null) nearCells.add(getBottom());
            return nearCells;
        }

        public boolean isStandalone() {
            Cell left = getLeft();
            Cell right = getRight();
            Cell top = getTop();
            Cell bottom = getBottom();
            boolean leftStandalone = (left == null || left.getValue() == EMPTY_CELL_VALUE);
            boolean rightStandalone = (right == null || right.getValue() == EMPTY_CELL_VALUE);
            boolean topStandalone = (top == null || top.getValue() == EMPTY_CELL_VALUE);
            boolean bottomStandalone = (bottom == null || bottom.getValue() == EMPTY_CELL_VALUE);
            return (leftStandalone && rightStandalone && topStandalone && bottomStandalone);
        }

        public int[] toArray() {
            int[] arr = new int[2];
            arr[0] = x;
            arr[1] = y;
            return arr;
        }

        @Override
        public String toString() {
            return "cell: {"+x+", "+y+"}";
        }
    }

    public static class Word {
        private List<Cell> word = Collections.synchronizedList(new LinkedList<>());

        public void pushLetter(Cell letter) {
            word.add(letter);
        }

        public boolean isEmpty() {
            return word.isEmpty();
        }
        public Cell popLetter() {
            if (word.size() > 0) {
                Cell cellToReturn = word.get(word.size()-1);
                word.remove(word.size()-1);
                return cellToReturn;
            }
            return null;
        }

        public boolean contains(Cell cell) {
            return word.contains(cell);
        }

        public Cell getEmptyCell() {
            for (Cell c : word) {
                if (c.getValue() == EMPTY_CELL_VALUE) return c;
            }
            return null;
        }

        public Cell getLast() {
            return word.get(word.size()-1);
        }

        public Word copy() {
            Word clone = new Word();
            clone.word = Collections.synchronizedList(new LinkedList<>(word));
            return clone;
        }

        public String fillGap(char c) {
            StringBuilder sb = new StringBuilder();
            word.forEach((Cell cell) -> {
                if (cell.getValue() == EMPTY_CELL_VALUE) {
                    sb.append(c);
                } else {
                    sb.append(cell.getValue());
                }
            });
            return sb.toString();
        }

        public int[][] toArray() {
            int[][] arr = new int[word.size()][];
            for (int i = 0; i < word.size(); i++) {
                arr[i] = word.get(i).toArray();
            }
            return arr;
        }

        public void reverse() {
            Collections.reverse(word);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Cell cell : word) {
                sb.append(cell.getValue());
            }
            return sb.toString();
        }
    }
}
