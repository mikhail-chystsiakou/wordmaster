package com.wordmaster.model.algorithm;

import com.wordmaster.model.GameField;
import com.wordmaster.model.Move;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
        String suggestion = move.getResultWordAsString(gameField);
        PrefixTree searchResult = prefixTree.goTo(suggestion);
        return searchResult != null && searchResult.isEnd();
    }

    public List<Move> generateMoves() {
        List<Move> suggestions = new LinkedList<>();
        gameField.getAvailableCells().forEach((GameField.Cell cell) -> {
            // consider this cell is target

            for (char c : reversedPrefixTree.getSubNodesKeys()) {
                // consider you wish to insert this char
                if (!reversedPrefixTree.getSubNodesKeys().contains(c)) continue;

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
    // currentWord must contain at least one letter
    private void findTreeValueNodes(PrefixTree tree, List<GameField.Word> result,
                                    GameField.Word currentWord) {
        if (tree == null) return;
        if (tree.isEnd()) {
            result.add(currentWord.copy());
        }

        currentWord.getLast().getNearCells().forEach((GameField.Cell nearCell) -> {
            if (tree.getSubNodesKeys().contains(nearCell.getValue())
                && !currentWord.contains(nearCell)) {
                currentWord.pushLetter(nearCell);
                findTreeValueNodes(tree.goTo(nearCell.getValue()), result, currentWord);
                currentWord.popLetter();
            }
        });
    }

    public List<Move> generateWithout(List<String> withoutList) {
        List<Move> suggestions = generateMoves();
        List<Move> suggestionsWithout = new LinkedList<>();
        suggestions.forEach((Move move) -> {
            if (!withoutList.contains(move.getResultWordAsString(gameField))) {
                suggestionsWithout.add(move);
            }
        });
        return suggestionsWithout;
    }

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
