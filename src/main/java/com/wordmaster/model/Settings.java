package com.wordmaster.model;

import org.apache.logging.log4j.LogManager;

import javax.swing.*;

public class Settings {
    public enum SupportedLAF {
        METAL {
            public String toString() {
                return "Metal";
            };
            public String getPackageString() {
                return UIManager.getCrossPlatformLookAndFeelClassName();
            }
        },
        SYSTEM {
            public String toString() {
                return "System";
            };
            public String getPackageString() {
                return UIManager.getSystemLookAndFeelClassName();
            }
        },
        WEBLAF {
            public String toString() {
                return "WebLAf";
            };
            public String getPackageString() {
                return "com.alee.laf.WebLookAndFeel";
            }
        };
        public abstract String getPackageString();
    }
    public enum Language {
        RUSSIAN {
            public String toString() {
                // TODO: i18n
                return "Русский";
            }
        },
        ENGLISH {
            public String toString() {
                // TODO: i18n
                return "English";
            }
        };
    };

    public SupportedLAF LAF;
    public Language language;
    private static Settings instance;

    public Settings(SupportedLAF LAF, Language language) {
        this.LAF = LAF;
        this.language = language;
    }

    public void apply(Settings settingsToApply) {
        if (settingsToApply == null) return;
        if (settingsToApply.LAF != null) {
            this.LAF = settingsToApply.LAF;
            try {
                // Whenever a Swing component is created,the component asks the UI manager
                // for the UI delegate that implements the component's L&F.
                UIManager.setLookAndFeel(instance.LAF.getPackageString());
            }
            catch ( ClassNotFoundException |
                    InstantiationException |
                    IllegalAccessException |
                    UnsupportedLookAndFeelException e) {
                LogManager.getLogger(this.getClass()).error("Cannot set new look&feel "
                        + instance.LAF.toString(), e);
                //TODO: create error popup
                return;
            }
        }
        if (settingsToApply.language != null) {
            this.language = settingsToApply.language;
        }
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings(SupportedLAF.METAL, Language.RUSSIAN);
        }
        return instance;
    }

    public SupportedLAF getLAF() {
        return LAF;
    }

    public Language getLanguage() {
        return language;
    }
}
