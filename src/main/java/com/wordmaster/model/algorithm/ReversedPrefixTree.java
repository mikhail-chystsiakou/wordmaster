package com.wordmaster.model.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReversedPrefixTree extends PrefixTree {
    private final static Logger logger = LoggerFactory.getLogger(ReversedPrefixTree.class);

    @Override
    public void addWord(String word) {
        String reversed = new StringBuilder(word).reverse().toString();
        for (int i = 0; i < word.length(); i++) {
            putNode(reversed.substring(i), 0);
        }
    }

    @Override
    PrefixTree buildPrefixTree() {
        return new ReversedPrefixTree();
    }

    public ReversedPrefixTree goTo(String wordPart) {
        return (ReversedPrefixTree)super.goTo(wordPart);
    }

}

