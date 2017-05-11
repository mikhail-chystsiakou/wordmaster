package com.wordmaster.model.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Recursive data structure to simplify algorithmic computations.
 * Each tree node contains map of subtrees and the keys of map are
 * the next word letters.
 *
 * @author Mike
 * @version 1.0
 */
public class PrefixTree {
    private final static Logger logger = LoggerFactory.getLogger(PrefixTree.class);
    private Map<Character, PrefixTree> subNodes = new HashMap<>();
    private String value;

    /**
     * Loads tree from input stream. Stream must contains several
     * words, separated by new line character.
     *
     * @param is input stream to read from
     * @throws IOException if any reading error occurs
     */
    void loadFromStream(InputStream is) throws IOException {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String nextWord = bf.readLine();
            int i = 0;
            while (nextWord != null) {
                addWord(nextWord);
                nextWord = bf.readLine();
                i++;
            }
        } catch (IOException e) {
            logger.error("Cannot load tree", e);
            throw e;
        }
    }

    /**
     * Adds word to the tree.
     *
     * @param word word to add
     */
    public void addWord(String word) {
        putNode(word, 0);
    }

    /**
     * Walks through the tree using input string as route path.
     *
     * @param wordPart path to go
     * @return  node that is located on this path and null if there is no such nodes
     */
    PrefixTree goTo(String wordPart) {
        if (wordPart.length() > 0) {
            if (!subNodes.containsKey(wordPart.charAt(0))) return null;
            return subNodes.get(wordPart.charAt(0)).goTo(wordPart.substring(1));
        } else {
            return this;
        }
    }

    /**
     * Returns subtree by it's key value.
     *
     * @param c key of subtree
     * @return  subtree that is located on this key and null if there is no such nodes
     */
    PrefixTree goTo(char c) {
        return subNodes.get(c);
    }

    void putNode(String word, int index) {
        if (index == word.length()) {
            this.value = word;
        } else {
            char c = word.charAt(index);
            if (subNodes.containsKey(c)) {
                subNodes.get(c).putNode(word, index+1);
            } else {
                PrefixTree subTree = buildPrefixTree();
                subTree.putNode(word, index+1);
                subNodes.put(c, subTree);
            }
        }
    }

    /**
     * Checks if this path from root node to this one represents valid
     * vocabulary word.
     *
     * @return true is this tree is node with valid word
     */
    public boolean isEnd() {
        return value != null;
    }

    /**
     * Pattern method that can be overriden in subclasses.
     *
     * @return
     */
    protected PrefixTree buildPrefixTree() {
        return new PrefixTree();
    }

    /**
     * Goes to random subtree and returns result
     *
     * @return random subtree
     */
    PrefixTree getRandomSubtree() {
        if (subNodes.size() == 0) {
            return null;
        }
        int randomMapElement = new Random().nextInt(subNodes.size());
        List<PrefixTree> subNodesList = new LinkedList<>(subNodes.values());
        return subNodesList.get(randomMapElement);
    }

    /**
     * Getter for tree value.
     *
     * @return tree node value, not null if it's valid word
     */
    public String getValue() {
        return value;
    }

    /**
     * Getter for subtrees keys
     *
     * @return subtrees keys
     */
    Set<Character> getSubNodesKeys() {
        return subNodes.keySet();
    }

    /**
     * Finds random word of desired size.
     *
     * @param wordSize the size of desired random word
     * @return random word
     */
    public String getRandomWord(int wordSize) {
        return getRandomWordHelper(this, wordSize);
    }

    private String getRandomWordHelper(PrefixTree tree, int wordSize) {
        if (tree == null) return null;
        if (wordSize == 0) return tree.getValue();
        String randomWord;
        int i = 0;
        do {
            PrefixTree randomSubtree = tree.getRandomSubtree();
            if (randomSubtree == null) {
                return null;
            }
            randomWord = getRandomWordHelper(tree.getRandomSubtree(), wordSize-1);
            i++;
        } while (randomWord == null && i < 20);
        return randomWord;
    }
}
