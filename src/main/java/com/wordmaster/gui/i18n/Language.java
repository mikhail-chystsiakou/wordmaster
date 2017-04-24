package com.wordmaster.gui.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlEnum;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
@XmlEnum
public enum Language {
    RUSSIAN {
        protected Locale getLocale() {
            return new Locale("ru");
        }
    },
    ENGLISH {
        protected Locale getLocale() {
            return new Locale("en");
        }
    };

    private static Logger logger = LoggerFactory.getLogger(Language.class);
    protected final static String RESOURCE_BUNDLE_BASE_NAME = "i18n.dictionary";
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
    public String toString() {
        return getResourceBundle().getString("locale_language");
    }
    protected abstract Locale getLocale();
}