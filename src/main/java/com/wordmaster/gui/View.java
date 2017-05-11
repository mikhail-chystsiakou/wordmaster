package com.wordmaster.gui;

import com.wordmaster.gui.audio.AudioPlayer;
import com.wordmaster.gui.custom.WordmasterUtils;
import com.wordmaster.gui.page.*;
import com.wordmaster.model.GameField;
import com.wordmaster.model.algorithm.Vocabulary;
import com.wordmaster.model.algorithm.VocabularyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * View for a game. Contains information about current setting
 * and the list of available pages.
 *
 * @author Mike
 * @version 1.0
 */
public class View {
    private final static Logger logger = LoggerFactory.getLogger(View.class);
    private final static int FRAME_WIDTH = 640;
    private final static int FRAME_HEIGHT = 480;
    private JFrame frame;
    private final Settings settings = new Settings();
    private Map<Pages, Page> pages = new HashMap<>();
    private Page currentPage;
    private AudioPlayer player = new AudioPlayer();
    private boolean isInitialized = false;

    /**
     * All view pages
     */
    public enum Pages {
        STARTUP, NEW_GAME_SETTINGS, GAME, SETTINGS
    }

    /**
     * Shows and initialized view if need
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {
            if (!isInitialized) {
                initialize();
            }
            showPage(Pages.STARTUP);
            frame.setResizable(false);
            player.startBackgroundMusic();
            frame.setVisible(true);
        });
    }

    /**
     * Gracefully destroys view
     */
    public void destroy() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Shows specified page
     *
     * @param page page to show
     */
    public void showPage(Pages page) {
        SwingUtilities.invokeLater(() -> {
            Page newShowPage = pages.get(page);
            try {
                newShowPage.preShow();
            } catch (PageException e) {
                WordmasterUtils.showErrorAlert(frame, "e_internal", settings.getLanguage());
                return;
            }
            CardLayout layout = (CardLayout)frame.getContentPane().getLayout();
            layout.show(frame.getContentPane(), page.toString());
            if (currentPage != null) {
                currentPage.postHide();
            }
            currentPage = newShowPage;
        });
    }

    /**
     * Applies provided settings
     *
     * @param settingsToApply settings object to apply
     */
    public void applySettings(Settings settingsToApply) {
        Settings.SupportedLAF previousLAF = settings.getLAF();
        if (!settings.applySettings(settingsToApply)) return;
        if (!previousLAF.equals(settings.getLAF())) {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(settings.getLAF().getPackageString());
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (Exception e) {
                    logger.error("Cannot set laf {}", settings.getLAF(), e);
                    WordmasterUtils.showErrorAlert(frame,
                            "e_internal", settingsToApply.getLanguage());
                }
            });
        }
        Vocabulary.loadVocabulary(settings.getLanguage());
        player.setBackgroundMusicVolume(settings.getMusicVolume());
        player.setSoundsVolume(settings.getSoundVolume());
    }

    /**
     * Settings getter
     *
     * @return Settings current view's settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Frame getter
     *
     * @return JFrame the view's JFrame
     */
    public JFrame getFrame() {return frame;}

    /**
     * AudioPlayer getter
     *
     * @return audioPlayer the view's AudioPlayer
     */
    public AudioPlayer getPlayer() {return player;}

    /**
     * Getter for any view page
     *
     * @param page specified page
     * @return page requested page
     */
    public Page getPage(Pages page) {
        return pages.get(page);
    }

    /**
     * Does all initialization staff
     * Creates the frame, initializes pages and trying to load existing settings.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Wordmaster");
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                frame.setVisible(false);
                frame.dispose();
                player.stopBackgroundMusic();
                System.exit(0);
            }
        });
        frame.setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        pages.put(Pages.STARTUP, new StartupPage(this));
        pages.put(Pages.SETTINGS, new SettingsPage(this));
        pages.put(Pages.NEW_GAME_SETTINGS, new NewGameSettingsPage(this));
        pages.put(Pages.GAME, new GamePage(this));

        frame.getContentPane().setLayout(new CardLayout());

        for (Pages key : pages.keySet()) {
            pages.get(key).initialize();
            frame.getContentPane().add(pages.get(key).getJComponent(), key.toString());
        }
        Settings loadedSettings = Settings.loadSettings(Settings.DEFAULT_SETTINGS_FILE, () -> {
            WordmasterUtils.showErrorAlert(frame,
                    "e_settings_loading", settings.getLanguage());
        });
        applySettings(loadedSettings);
        isInitialized = true;
    }
}
