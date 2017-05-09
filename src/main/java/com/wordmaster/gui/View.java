package com.wordmaster.gui;

import com.wordmaster.gui.custom.WordmasterUtils;
import com.wordmaster.gui.page.*;
import com.wordmaster.model.GameField;
import com.wordmaster.model.algorithm.Vocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class View {
    private final static Logger logger = LoggerFactory.getLogger(View.class);
    private JFrame frame;
    private final static int FRAME_WIDTH = 640;
    private final static int FRAME_HEIGHT = 480;
    private final Settings settings = new Settings();
    private Map<Pages, Page> pages = new HashMap<>();
    private Page currentPage;

    private boolean isInitialized = false;

    /**
     * Enumerates all main game panes
     */
    public enum Pages {
        STARTUP, NEW_GAME_SETTINGS, GAME, SETTINGS
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Wordmaster");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        Settings loadedSettings = Settings.loadSettings(Settings.DEFAULT_SETTINGS_FILE, () ->
                WordmasterUtils.showErrorAlert(frame, "Cannot load settings")
        );
        applySettings(loadedSettings);
        isInitialized = true;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            if (!isInitialized) {
                initialize();
            }
            showPage(Pages.STARTUP);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    public void destroy() {
        System.exit(0);
    }

    public void showPage(Pages page) {
        SwingUtilities.invokeLater(() -> {
            Page newShowPage = pages.get(page);
            try {
                newShowPage.preShow();
            } catch (PageException e) {
                logger.warn(e.getMessage(), e);
                WordmasterUtils.showErrorAlert(frame, e.getMessage());
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
     * Applies provided settings and saves them to file Settings.SETTINGS_FILE.
     *
     * @param settingsToApply settings object to apply and save;
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
                    WordmasterUtils.showErrorAlert(frame, "Cannot set Look&Feel");
                }
            });
        }
        Vocabulary.loadVocabulary(settings.getLanguage());
    }
    public Settings getSettings() {
        return settings;
    }
    public JFrame getFrame() {return frame;}

    public Page getPage(Pages page) {
        return pages.get(page);
    }
}
