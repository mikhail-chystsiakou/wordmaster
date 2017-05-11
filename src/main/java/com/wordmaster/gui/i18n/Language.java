package com.wordmaster.gui.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlEnum;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Represent one supported game language. Language is a little bit
 * complex enumeration and allows to validate word or letter,
 * get associated resource bundle and vocabulary inputStream
 */
@XmlEnum
public enum Language {
    /**
     * Russian language
     */
    RUSSIAN {
        protected Locale getLocale() {
            return new Locale("ru");
        }
        protected String getAlphabet() {
            return "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        }
    },
    /**
     * English language
     */
    ENGLISH {
        protected Locale getLocale() {
            return new Locale("en");
        }
        protected String getAlphabet() {
            return "abcdefghijklmnopqrstuvwxyz";
        }
    };

    private static final Logger logger = LoggerFactory.getLogger(Language.class);
    protected final static String RESOURCE_BUNDLE_BASE_NAME = "i18n.dictionary";
    protected final static String VOCABULARY_PATH_PREFIX = "i18n/vocabulary_";
    protected ResourceBundle resourceBundle;

    /**
     * Retrieves the resource bundle associated with language
     *
     * @return resource bundle of the language
     */
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            try {
                resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_BASE_NAME, getLocale());
            } catch (MissingResourceException e) {
                logger.warn("Cannot load resource bundle for locale {}", getLocale(), e);
                throw e;
            }
        }
        return resourceBundle;
    }

    /**
     * Retrieves the vocabulary stream associated with language
     *
     * @return vocabulary stream of the language
     */
    public InputStream getVocabularyInputStream() {
        return this.getClass().getClassLoader()
                .getResourceAsStream(VOCABULARY_PATH_PREFIX+getLocale().getLanguage()+".txt");
    }

    /**
     * Checks if letter is in language alphabet
     *
     * @param c letter to check
     * @return true if letter belongs to alphabet, false otherwise
     */
    public boolean validateLetter(char c) {
        return getAlphabet().indexOf(c) != -1;
    }

    /**
     * Checks if word consists of valid alphabet letters
     *
     * @param s string to check
     * @return true if string contains valid alphabet letters, false otherwise
     */
    public boolean validateWordLetters(String s) {
        for (char c : s.toCharArray()) {
            if (getAlphabet().indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return getResourceBundle().getString("locale_language");
    }

    /**
     * Factory method to get the local
     *
     * @return locale object
     */
    protected abstract Locale getLocale();

    /**
     * Factory method to get alphabet
     *
     * @return string of all alphabet letters
     */
    protected abstract String getAlphabet();
}