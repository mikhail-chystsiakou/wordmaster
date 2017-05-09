package com.wordmaster.model;

import javax.xml.bind.annotation.*;
import java.util.List;

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

    public GameField.Word getResultWord(GameField field) {
        return field.getWord(resultWord);
    }

    public void setResultWord(GameField.Word resultWord) {
        this.resultWord = resultWord.toArray();
    }

    public char getPrevCellValue() {
        return prevCellValue;
    }

    public void setPrevCellValue(char prevCellValue) {
        this.prevCellValue = prevCellValue;
    }

    public char getNewCellValue() {
        return newCellValue;
    }

    public void setNewCellValue(char newCellValue) {
        this.newCellValue = newCellValue;
    }

    public GameField.Cell getCell(GameField field) {
        return field.getCell(cellX, cellY);
    }

    public void setCell(GameField.Cell cell) {
        this.cellX = cell.getX();
        this.cellY = cell.getY();
        this.prevCellValue = cell.getValue();
    }

    public int getResultWordSize() {
        return resultWord.length;
    }

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
