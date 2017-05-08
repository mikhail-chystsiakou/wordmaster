package com.wordmaster.model.algorithm;

import com.wordmaster.model.GameField;
import com.wordmaster.model.Move;
import sun.reflect.generics.tree.Tree;

public class Algorithm {
    private PrefixTree prefixTree;
    private ReversedPrefixTree reversedPrefixTree;
    private GameField gameField;

    public Algorithm(GameField gameField, Vocabulary vocabulary) {
        this.gameField = gameField;
        prefixTree = vocabulary.getPrefixTree();
        reversedPrefixTree = vocabulary.getReversedPrefixTree();
    }

    public boolean validateMove(Move move) {
        String suggestion = move.getResultWord(gameField)
                .fillGap(move.getNewCellValue());
        PrefixTree searchResult = prefixTree.goTo(suggestion);
        return searchResult != null && searchResult.isEnd();
    }

    public Move generateMove() {

        return new Move();
    }

    public void predictMove(MoveGeneratedCallback callback) {
        new Thread(()-> {
            System.out.println("Predicter");
            callback.onMove(null);
        }).start();
    }

}
