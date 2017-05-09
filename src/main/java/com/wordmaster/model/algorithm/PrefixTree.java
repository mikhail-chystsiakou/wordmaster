package com.wordmaster.model.algorithm;

import com.wordmaster.TreeRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class PrefixTree {
    private final static Logger logger = LoggerFactory.getLogger(PrefixTree.class);
    private Map<Character, PrefixTree> subNodes = new HashMap<>();
    private String value;

    void loadFromStream(InputStream is) throws IOException {
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(is));
            String nextWord = bf.readLine();
            while (nextWord != null) {
                addWord(nextWord);
                nextWord = bf.readLine();
            }
        } catch (IOException e) {
            logger.error("Cannot load tree", e);
            throw e;
        }
    }

    public void addWord(String word) {
        putNode(word, 0);
    }

    PrefixTree goTo(String wordPart) {
        if (wordPart.length() > 0) {
            if (!subNodes.containsKey(wordPart.charAt(0))) return null;
            return subNodes.get(wordPart.charAt(0)).goTo(wordPart.substring(1));
        } else {
            return this;
        }
    }

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

    public boolean isEnd() {
        return value != null;
    }

    PrefixTree buildPrefixTree() {
        return new PrefixTree();
    }

    PrefixTree getRandomSubtree() {
        if (subNodes.size() == 0) return null;
        int randomMapElement = new Random().nextInt(subNodes.size());
        List<PrefixTree> subNodesList = new LinkedList<>(subNodes.values());
        return subNodesList.get(randomMapElement);
    }

    public String getValue() {
        return value;
    }

    Set<Character> getSubNodesKeys() {
        return subNodes.keySet();
    }
}
