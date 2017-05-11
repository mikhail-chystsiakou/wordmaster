package com.wordmaster.model.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Similar to <code>PrefixTree</code>, but contains
 * all valid reversed word beginnings as values.
 *
 * @author Mike
 * @version 1.0
 */
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
    protected PrefixTree buildPrefixTree() {
        return new ReversedPrefixTree();
    }
}

