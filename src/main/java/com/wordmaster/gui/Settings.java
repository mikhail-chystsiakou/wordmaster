package com.wordmaster.gui;

import com.bulenkov.darcula.DarculaLaf;
import com.wordmaster.gui.i18n.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Represents settings for the concrete view. Can be
 * saved and loaded from file
 *
 * @author Mike
 * @version 1.0
 */
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

    /**
     * Enumeration for LAF that application supports
     */
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
        DARKULA {
            public String toString() {
                return "Darcula";
            }
            public String getPackageString() {
                return DarculaLaf.class.getCanonicalName();
            }
        };
        public abstract String getPackageString();
    }

    // for the jaxb proper work
    public Settings(){

    }

    /**
     * The copy constructor
     *
     * @param settingsToClone the object to clone
     */
    public Settings(Settings settingsToClone) {
        this.applySettings(settingsToClone);
    }

    /**
     * Modifies current <code>Settings</code> object to be equals to the
     * settingsToApply parameter
     *
     * @param settingsToApply the settings to apply
     * @return wasModified if any field was modified
     */
    public boolean applySettings(Settings settingsToApply) {
        boolean wasModified = false;
        if (settingsToApply == null) return false;
        if (settingsToApply.getLAF() != LAF) {
            this.setLAF(settingsToApply.getLAF());
            wasModified = true;
        }
        if (settingsToApply.getLanguage() != language) {
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

    /**
     * Asynchronously saves provided <code>Settings</code> object to
     * specified file
     *
     * @param settingsFile path to settings file
     * @param settings <code>Settings</code> object to save
     * @param onError callback to call on error
     */
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

    /**
     * Synchronously loads settings from the file
     *
     * @param settingsFile path to settings file
     * @param onError callback to call on error
     * @return loaded <code>Settings</code> object
     */
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

    /**
     * LAF getter
     *
     * @return current LAF
     */
    public SupportedLAF getLAF() {
        return LAF;
    }

    /**
     * <code>Language</code> getter
     *
     * @return current <code>Language</code>
     */
    public Language getLanguage() {return language;}

    /**
     * Sound volume getter
     *
     * @return current sound volume
     */
    public int getSoundVolume() {
        return soundVolume;
    }

    /**
     * Music volume getter
     *
     * @return current music volume
     */
    public int getMusicVolume() {
        return musicVolume;
    }

    /**
     * LAF setter
     *
     * @param LAF LAF to set
     */
    public void setLAF(SupportedLAF LAF) {
        this.LAF = LAF;
    }

    /**
     * Language setter
     *
     * @param lang language to set
     */
    public void setLanguage(Language lang) {
        this.language = lang;
    }

    /**
     * Sound volume setter
     *
     * @param soundVolume sound volume to set
     */
    public void setSoundVolume(int soundVolume) {
        this.soundVolume = soundVolume;
    }

    /**
     * Music volume setter
     *
     * @param musicVolume music volume to set
     */
    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }
}
