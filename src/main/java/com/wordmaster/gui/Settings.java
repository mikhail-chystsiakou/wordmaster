package com.wordmaster.gui;

import com.wordmaster.gui.i18n.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.sun.source.tree.*;
//import com.sun.source.util.*;

import javax.swing.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Settings implements Cloneable {
    private static Logger logger = LoggerFactory.getLogger(Settings.class);
    public static final String DEFAULT_SETTINGS_FILE = "settings.xml";
    @XmlElement
    private SupportedLAF LAF = SupportedLAF.METAL;
    @XmlElement
    private Language language = Language.ENGLISH;
    @XmlElement
    private int soundVolume = 50;
    @XmlElement
    private int musicVolume = 50;

    @XmlEnum
    public enum SupportedLAF {
        METAL {
            public String toString() {
                return "Metal";
            }
            public String getPackageString() {
                return UIManager.getCrossPlatformLookAndFeelClassName();
            }
        },
        SYSTEM {
            public String toString() {
                return "System";
            }
            public String getPackageString() {
                return UIManager.getSystemLookAndFeelClassName();
            }
        },
        WEBLAF {
            public String toString() {
                return "WebLAf";
            }
            public String getPackageString() {
                return "com.alee.laf.WebLookAndFeel";
            }
        };
        public abstract String getPackageString();
    }

    public Settings(){

    }

    public Settings(Settings settingsToClone) {
        this.applySettings(settingsToClone);
    }



    public boolean applySettings(Settings settingsToApply) {
        boolean wasModified = false;
        if (settingsToApply == null) return false;
        if (settingsToApply.getLAF() != LAF) {
            this.setLAF(settingsToApply.getLAF());
            wasModified = true;
        }
        if (settingsToApply.getLanguage() != language) {
            System.out.println("new settings language value: " + settingsToApply.language+", was " + language);
            this.setLanguage(settingsToApply.getLanguage());
            wasModified = true;
        }
        if (settingsToApply.getSoundVolume() != soundVolume) {
            this.setSoundVolume(settingsToApply.getSoundVolume());
            wasModified = true;
        }
        if (settingsToApply.getMusicVolume() != musicVolume) {
            this.setMusicVolume(settingsToApply.getMusicVolume());
            wasModified = true;
        }
        return wasModified;
    }
    // async
    public static void saveSettings(String settingsFile, Settings settings, Runnable onError) {
        final Thread asyncSaving = new Thread(()->{
            try {
                JAXBContext context =
                        JAXBContext.newInstance( settings.getClass());
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( settings, new FileOutputStream(settingsFile));
            } catch (JAXBException e) {
                logger.warn("Cannot save settings to file {}", settingsFile, e);
                onError.run();
            } catch (FileNotFoundException e) {
                logger.warn("Cannot create new settings file {}", settingsFile, e);
                onError.run();
            }
        });
        asyncSaving.start();
    }
    // sync
    public static Settings loadSettings(String settingsFile, Runnable onError) {
        File expectedSettingsFile = new File(settingsFile);
        Settings savedSettings = new Settings();
        if (expectedSettingsFile.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance( Settings.class);
                Unmarshaller um = context.createUnmarshaller();
                savedSettings = (Settings)um.unmarshal(expectedSettingsFile);

            } catch (JAXBException e) {
                logger.warn("Cannot load existing settings file {}", settingsFile, e);
                onError.run();
            }
        }
        return savedSettings;
    }

    public SupportedLAF getLAF() {
        return LAF;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLAF(SupportedLAF LAF) {
        this.LAF = LAF;
    }

    public void setLanguage(Language lang) {
        this.language = lang;
    }

    public int getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(int soundVolume) {
        this.soundVolume = soundVolume;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }
}
