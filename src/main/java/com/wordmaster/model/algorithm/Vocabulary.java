package com.wordmaster.model.algorithm;

import com.wordmaster.gui.i18n.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Vocabulary {
    private static final Logger logger = LoggerFactory.getLogger(Vocabulary.class);
    private static Map<Language, Future<Vocabulary>> vocabularyMap = new HashMap<>();
    private PrefixTree prefixTree = new PrefixTree();
    private ReversedPrefixTree reversedPrefixTree = new ReversedPrefixTree();

    public static void loadVocabulary(Language language) {
        if (!vocabularyMap.containsKey(language)) {
            FutureTask<Vocabulary> loadingTask = new FutureTask<>(() -> {
                try {
                    Vocabulary vocabulary = new Vocabulary();
                    InputStream is = language.getVocabulary();
                    vocabulary.prefixTree.loadFromStream(is);
                    is = language.getVocabulary();
                    vocabulary.reversedPrefixTree.loadFromStream(is);
                    return vocabulary;
                } catch (IOException e) {
                    throw new VocabularyException("Cannot load tree", e);
                }
            });
            new Thread(loadingTask).start();
            vocabularyMap.put(language, loadingTask);
        }
    }

    public static Future<Vocabulary> getVocabulary(Language language) {
        if (!vocabularyMap.containsKey(language)) {
            loadVocabulary(language);
        }
        return vocabularyMap.get(language);
    }


    ReversedPrefixTree getReversedPrefixTree() {
        return reversedPrefixTree;
    }

    PrefixTree getPrefixTree() {
        return prefixTree;
    }

    public String getRandomWord(int wordSize) {
        return getRandomWordHelper(prefixTree, wordSize);
    }

    private String getRandomWordHelper(PrefixTree prefixTree, int wordSize) {
        if (prefixTree == null) return null;
        if (wordSize == 0) return prefixTree.getValue();

        String randomWord;
        int i = 0;
        do {
            randomWord = getRandomWordHelper(prefixTree.getRandomSubtree(), wordSize-1);
            i++;
        } while (randomWord == null && i < 5);
        return randomWord;
    }

}
