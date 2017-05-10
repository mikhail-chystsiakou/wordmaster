package com.wordmaster.gui.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlEnum;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@XmlEnum
public enum Language {
    RUSSIAN {
        protected Locale getLocale() {
            return new Locale("ru");
        }
        protected String getAlphabet() {
            return "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
        }
    },
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

    public InputStream getVocabulary() {
        return this.getClass().getClassLoader()
                .getResourceAsStream(VOCABULARY_PATH_PREFIX+getLocale().getLanguage()+".txt");
    }

    public boolean validateLetter(char c) {
        return getAlphabet().indexOf(c) != -1;
    }

    public boolean validateWord(String s) {
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

    protected abstract Locale getLocale();
    protected abstract String getAlphabet();
}