package com.wordmaster.model.algorithm;

import com.wordmaster.model.GameField;
import com.wordmaster.model.Move;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Performs game move calculations.
 *
 * @author Mike
 * @version  1.0
 */
public class Algorithm {
    private PrefixTree prefixTree;
    private ReversedPrefixTree reversedPrefixTree;
    private GameField gameField;

    public Algorithm(GameField gameField, Vocabulary vocabulary) {
        this.gameField = gameField;
        prefixTree = vocabulary.getPrefixTree();
        reversedPrefixTree = vocabulary.getReversedPrefixTree();
    }

    /**
     * Checks if move is valid.
     *
     * @param move move to check
     * @return true if move is valid, false otherwise
     */
    public boolean validateMove(Move move) {
        String suggestion = move.getResultWordAsString(gameField);
        PrefixTree searchResult = prefixTree.goTo(suggestion);
        return searchResult != null && searchResult.isEnd();
    }

    /**
     * Generates all available moves.
     *
     * @return list ov available moves
     */
    public List<Move> generateMoves() {
        List<Move> suggestions = new LinkedList<>();
        gameField.getAvailableCells().forEach((GameField.Cell cell) -> {
            // consider this cell is target

            for (char c : reversedPrefixTree.getSubNodesKeys()) {
                // consider you wish to insert this char
//                if (!reversedPrefixTree.getSubNodesKeys().contains(c)) continue;

                // first, get all valid reversed begins
                List<GameField.Word> reversedWordBegins = new LinkedList<>();
                GameField.Word wordFromCurrentCell = new GameField.Word();
                wordFromCurrentCell.pushLetter(cell);
                findTreeValueNodes(reversedPrefixTree.goTo(c),
                                    reversedWordBegins, wordFromCurrentCell);

                // second, try to find word by them beginning
                List<GameField.Word> validWords = new LinkedList<>();
                reversedWordBegins.forEach((GameField.Word word) -> {
                    word.reverse();
                    findTreeValueNodes(prefixTree.goTo(word.fillGap(c)), validWords, word);
                });
                // add move
                validWords.forEach((GameField.Word validWord) -> {
                    boolean alreadyUsed = false;
                    for (Move m : suggestions) {
                        if (m.getResultWord(gameField).equals(validWord)) {
                            alreadyUsed = true;
                            break;
                        }
                    }
                    if (alreadyUsed) return;
                    Move suggestion = new Move();
                    suggestion.setResultWord(validWord);
                    suggestion.setCell(cell);
                    suggestion.setNewCellValue(c);
                    suggestion.setPrevCellValue(cell.getValue());
                    suggestions.add(suggestion);
                });
            }
        });
        // that's all
        return suggestions;
    }

    /**
     * Helper method that finds all the words in tree, that
     * can be created from current word. Current word must contain
     * at least one letter.
     *
     * @param tree  tree to find words from
     * @param result    list in which results will accumulate
     * @param currentWord   word to start from
     */
    private void findTreeValueNodes(PrefixTree tree, List<GameField.Word> result,
                                    GameField.Word currentWord) {
        if (currentWord.getLastLetter() == null) return;
        if (tree == null) return;
        if (tree.isEnd()) {
            result.add(currentWord.copy());
        }

        currentWord.getLastLetter().getNearCells().forEach((GameField.Cell nearCell) -> {
            if (tree.getSubNodesKeys().contains(nearCell.getValue())
                && !currentWord.contains(nearCell)) {
                currentWord.pushLetter(nearCell);
                findTreeValueNodes(tree.goTo(nearCell.getValue()), result, currentWord);
                currentWord.popLetter();
            }
        });
    }

    /**
     * Generates words, that are different from specified 'without list'.
     *
     * @param withoutList   words that should not be included in result
     * @return list on generated words
     */
    public List<Move> generateWithout(List<String> withoutList) {
        List<Move> suggestions = generateMoves();
        if (withoutList == null) return suggestions;
        List<Move> suggestionsWithout = new LinkedList<>();
        suggestions.forEach((Move move) -> {
            if (!withoutList.contains(move.getResultWordAsString(gameField))) {
                suggestionsWithout.add(move);
            }
        });
        return suggestionsWithout;
    }

    /**
     * Allows to run move generator in additional thread with
     * calling callback after computation result.
     *
     * @param callback  callback to call after move generation
     * @param withoutList   words that should not appear in result
     */
    public void predictMove(MoveGeneratedCallback callback, List<String> withoutList) {
        new Thread(()-> {
            List<Move> suggestions = generateWithout(withoutList);
            if (suggestions.size() > 0) {
                int pos = new Random().nextInt(suggestions.size()-1);
                callback.onMove(suggestions.get(pos));
            } else {
                callback.onMove(null);
            }
        }).start();
    }

}
