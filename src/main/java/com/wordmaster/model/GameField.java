package com.wordmaster.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlType;
import java.util.*;

/**
 * Represents game field. Internally stores cells in char array
 * and has pretty useful cell and word wrappers.
 */
@XmlType
public class GameField {
    private static final Logger logger = LoggerFactory.getLogger(GameField.class);
    public final static int FIELD_WIDTH = 7;
    public final static int FIELD_HEIGHT = 7;
    public final static int MIN_START_WORD_SIZE = 3;
    public final static int MAX_START_WORD_SIZE = 7;
    public final static char EMPTY_CELL_VALUE = ' ';
    @XmlElement
    private char[][] field = new char[FIELD_HEIGHT][FIELD_WIDTH];
    @XmlAttribute
    private String startWord;

    // for the jaxb
    public GameField() {

    }

    /**
     * Initializes game field with start word.
     *
     * @param word start word
     */
    GameField(String word) {
        if (word == null || word.length() > FIELD_WIDTH || word.length() == 0) {
            logger.error("Invalid size of GameField base word {}", word);
            throw new IllegalArgumentException("Illegal word size");
        }
        this.startWord = word;
        clear();
    }

    /**
     * Returns all cells, that can be used in move. The cell must be
     * free be near to not-empty cell.
     *
     * @return list of available cells
     */
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

    /**
     * Fills game field with default empty cell values and
     * restores start word
     */
    void clear() {
        int wordStartPosition = (FIELD_WIDTH-startWord.length())/2;
        for (int y = 0; y < FIELD_HEIGHT; y++) {
            for (int x = 0; x < FIELD_HEIGHT; x++) {
                field[y][x] = EMPTY_CELL_VALUE;
            }
        }
        for (int i = 0; i < startWord.length(); i++) {
            field[(FIELD_HEIGHT-1)/2][wordStartPosition+i] = startWord.charAt(i);
        }
    }

    /**
     * Creates the cell wrapper object. Top left corner
     * is the count start point.
     *
     * @param x x position of cell
     * @param y y position of cell
     * @return cell wrapper object
     */
    public Cell getCell(int x, int y) {
        return new Cell(x, y);
    }

    /**
     * Creates the word wrapper object.
     *
     * @param arr array of row game cells that are part of word
     * @return word wrapper object
     */
    public Word getWord(int[][] arr) {
        Word word = new Word();
        for (int[] cell : arr) {
            word.pushLetter(getCell(cell[0], cell[1]));
        }
        return word;
    }

    /**
     * Getter for start word.
     *
     * @return start word
     */
    public String getStartWord() {
        return startWord;
    }

    /**
     * Wrapper class for the game field cell.
     */
    public class Cell {
        int x;
        int y;

        private Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Checks if cell is empty.
         *
         * @return true if cell if empty, false otherwise
         */
        public boolean isEmpty() {
            return getValue() == EMPTY_CELL_VALUE;
        }

        /**
         * Returns the value of game field in cell position.
         *
         * @return cell value
         */
        public char getValue() {
            return field[y][x];
        }

        /**
         * Set the value of game field in cell position.
         *
         * @param value field value
         */
        void setValue(char value) {
            field[y][x] = value;
        }

        /**
         * Cell x position getter.
         *
         * @return cell x position
         */
        public int getX() {
            return x;
        }

        /**
         * Cell y position getter
         *
         * @return cell y position
         */
        public int getY() {
            return y;
        }

        /**
         * Checks if cell is near. Each no-corner cell has 4
         * near cells, on top, bot, left and right.
         *
         * @param cell cell to check
         * @return true if cell is near, false otherwise
         */
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

        /**
         * Returns the cell on left.
         *
         * @return cell on the left or null for the left-corner cell
         */
        public Cell getLeft() {
            if (x > 0) {
                return new Cell(x-1, y);
            } else return null;
        }

        /**
         * Returns the cell on right.
         *
         * @return cell on the right or null for the right-corner cell
         */
        public Cell getRight() {
            if (x < FIELD_WIDTH - 1) {
                return new Cell(x+1, y);
            } else return null;
        }

        /**
         * Returns the cell on top.
         *
         * @return cell on the top or null for the left-corner cell
         */
        public Cell getTop() {
            if (y > 0) {
                return new Cell(x, y-1);
            } else return null;
        }

        /**
         * Returns the cell on bottom.
         *
         * @return cell on the bottom or null for the left-corner cell
         */
        public Cell getBottom() {
            if (y < FIELD_HEIGHT - 1) {
                return new Cell(x, y+1);
            } else return null;
        }

        /**
         * Returns near cells. Near cells must have one common edge.
         *
         * @return list of near cells.
         */
        public List<Cell> getNearCells() {
            LinkedList<Cell> nearCells = new LinkedList<>();
            if (getLeft() != null) nearCells.add(getLeft());
            if (getRight() != null) nearCells.add(getRight());
            if (getTop() != null) nearCells.add(getTop());
            if (getBottom() != null) nearCells.add(getBottom());
            return nearCells;
        }

        /**
         * Checks if cell is standalone. Cell is stanadalone if
         * all it's near cells are empty.
         *
         * @return true is cell is standalone
         */
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

    /**
     * Wrapper class for the game field word. Word is just an array
     * of game cells.
     */
    public static class Word {
        private List<Cell> word = Collections.synchronizedList(new LinkedList<>());

        public void pushLetter(Cell letter) {
            word.add(letter);
        }

        public Cell popLetter() {
            if (word.size() > 0) {
                Cell cellToReturn = word.get(word.size()-1);
                word.remove(word.size()-1);
                return cellToReturn;
            }
            return null;
        }

        public boolean isEmpty() {
            return word.isEmpty();
        }

        public boolean contains(Cell cell) {
            return word.contains(cell);
        }

        /**
         * Returns the last letter of the word.
         *
         * @return last letter
         */
        public Cell getLastLetter() {
            if (word.size() == 0) return null;
            return word.get(word.size()-1);
        }

        /**
         * Creates a clone of word.
         * @return the clone of word
         */
        public Word copy() {
            Word clone = new Word();
            clone.word = Collections.synchronizedList(new LinkedList<>(word));
            return clone;
        }

        /**
         * If there is empty cell inside the word, fills its with specified
         * value and returns result word as string. Note, that field stay
         * unmodified.
         *
         * @param c chat to set in gap
         * @return word without gap
         */
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

        /**
         * Reverses the word.
         */
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
