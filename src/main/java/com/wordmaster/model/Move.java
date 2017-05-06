package com.wordmaster.model;

// annotate with jaxb
class Move {
    private GameField.Cell cell;
    private char newCellValue;
    private char prevCellValue;
    private GameField.Word resultWord;

    public GameField.Word getResultWord() {
        return resultWord;
    }

    public void setResultWord(GameField.Word resultWord) {
        this.resultWord = resultWord;
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

    public GameField.Cell getCell() {
        return cell;
    }

    public void setCell(GameField.Cell cell) {
        this.cell = cell;
    }

}
