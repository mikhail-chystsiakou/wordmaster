package com.wordmaster.gui;

import com.alee.utils.SwingUtils;
import com.wordmaster.gui.page.*;
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
                System.err.println("Cannot load settings file")
        );
        applySettings(loadedSettings);
        isInitialized = true;
    }

    public void show() {
        SwingUtils.invokeLater(() -> {
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
        SwingUtils.invokeLater(() -> {
            pages.get(page).preShow();
            CardLayout layout = (CardLayout)frame.getContentPane().getLayout();
            layout.show(frame.getContentPane(), page.toString());
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
            SwingUtils.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(settings.getLAF().getPackageString());
                    SwingUtilities.updateComponentTreeUI(frame);
                } catch (Exception e) {
                    logger.error("Cannot set laf {}", settings.getLAF(), e);
                    JOptionPane.showMessageDialog(frame, "Cannot set Look&Feel");
                }
            });
        }
    }
    public Settings getSettings() {
        return settings;
    }
    public JFrame getFrame() {return frame;}
}
