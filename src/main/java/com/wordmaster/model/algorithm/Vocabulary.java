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

/**
 * Vocabulary contains all possible game words in
 * suitable for algorithm form (i.e. prefix tree data
 * structure). Statically keeps all game vocabularies
 * that can be loaded and later accessed by language.
 *
 * @author Mike
 * @version 1.0
 */
public class Vocabulary {
    private static final Logger logger = LoggerFactory.getLogger(Vocabulary.class);
    private static Map<Language, Future<Vocabulary>> vocabularyMap = new HashMap<>();
    private PrefixTree prefixTree = new PrefixTree();
    private ReversedPrefixTree reversedPrefixTree = new ReversedPrefixTree();

    /**
     * Creates and starts async future task of vocabulary loading.
     * Vocabulary can be statically accessed later.
     *
     * @param language language that contains stream of proper language vocabulary
     */
    public static void loadVocabulary(Language language) {
        if (!vocabularyMap.containsKey(language)) {
            FutureTask<Vocabulary> loadingTask = new FutureTask<>(() -> {
                try {
                    Vocabulary vocabulary = new Vocabulary();
                    InputStream is = language.getVocabularyInputStream();
                    vocabulary.prefixTree.loadFromStream(is);
                    is = language.getVocabularyInputStream();
                    vocabulary.reversedPrefixTree.loadFromStream(is);
                    return vocabulary;
                } catch (IOException e) {
                    VocabularyException ve = new VocabularyException("Cannot load vocabulary", e);
                    logger.error("Error in vocabulary loading thread", ve);
                    throw ve;
                }
            });
            vocabularyMap.put(language, loadingTask);
            new Thread(loadingTask).start();
        }
    }

    /**
     * Returns vocabulary by it's language
     *
     * @param language vocabulary language
     * @return vocabulary which is associated with desired language
     */
    public static Future<Vocabulary> getVocabulary(Language language) {
        if (!vocabularyMap.containsKey(language)) {
            loadVocabulary(language);
        }
        return vocabularyMap.get(language);
    }

    /**
     * Getter for reversed prefix tree
     *
     * @return reversed prefix tree
     */
    ReversedPrefixTree getReversedPrefixTree() {
        return reversedPrefixTree;
    }

    /**
     * Getter for prefix tree
     *
     * @return prefix tree
     */
    PrefixTree getPrefixTree() {
        return prefixTree;
    }

    /**
     * Returns one random word from vocabulary of desired size
     *
     * @param wordSize the size of desired random word
     * @return  random word
     */
    public String getRandomWord(int wordSize) {
        return prefixTree.getRandomWord(wordSize);
    }

}
