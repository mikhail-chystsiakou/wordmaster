package com.wordmaster.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * The pure data object to represent one game move, that can
 * be simply stored to and loaded from file.
 *
 * @version 1.0
 * @author Mike
 */
@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public class Move {
    @XmlAttribute
    private int cellX;
    @XmlAttribute
    private int cellY;
    @XmlAttribute
    private char newCellValue = GameField.EMPTY_CELL_VALUE;
    @XmlAttribute
    private char prevCellValue = GameField.EMPTY_CELL_VALUE;
    @XmlElement
    private int[][] resultWord;

    public static final int ALREADY_USED = 1;
    public static final int INVALID_WORD = 2;

    /**
     * Calculates compound <code>GameField.Word</code> object
     * from internal 2D array using concrete field
     *
     * @param field game field that will be owner of word
     * @return game field word
     */
    public GameField.Word getResultWord(GameField field) {
        return field.getWord(resultWord);
    }
    /**
     * Seter for result word
     *
     * @param resultWord word to set
     */
    public void setResultWord(GameField.Word resultWord) {
        this.resultWord = resultWord.toArray();
    }

    /**
     * Getter for the previous cell value
     *
     * @return cell value before move
     */
    public char getPrevCellValue() {
        return prevCellValue;
    }

    /**
     * Setter for the previous cell value
     *
     * @param prevCellValue cell value before move
     */
    public void setPrevCellValue(char prevCellValue) {
        this.prevCellValue = prevCellValue;
    }

    /**
     * Getter for new cell value
     *
     * @return the cell value after move
     */
    public char getNewCellValue() {
        return newCellValue;
    }

    /**
     * Setter for new cell value
     *
     * @param newCellValue cell value after move
     */
    public void setNewCellValue(char newCellValue) {
        this.newCellValue = newCellValue;
    }

    /**
     * Getter for cell, affected by the move
     *
     * @param field game field, the owner of the cell
     * @return affected cell
     */
    public GameField.Cell getCell(GameField field) {
        return field.getCell(cellX, cellY);
    }

    /**
     * Setter for cell, affected by the move
     *
     * @param cell cell to be affected
     */
    public void setCell(GameField.Cell cell) {
        this.cellX = cell.getX();
        this.cellY = cell.getY();
        this.prevCellValue = cell.getValue();
    }

    /**
     * Getter for result word size
     *
     * @return the size of result word
     */
    public int getResultWordSize() {
        return resultWord.length;
    }

    /**
     * Calculates the string value of resultWord
     *
     * @param field the game field owner of the word
     * @return word's string value
     */
    public String getResultWordAsString(GameField field) {
        return field.getWord(resultWord).fillGap(newCellValue);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ Move: {");
        sb.append("[");
        sb.append(cellX);
        sb.append(", ");
        sb.append(cellY);
        sb.append("] ");
        sb.append(", prev char: ");
        sb.append(prevCellValue);
        sb.append(", new char: ");
        sb.append(newCellValue);
        sb.append("}}");
        return sb.toString();
    }

}
