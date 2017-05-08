package com.wordmaster.gui.page;

import com.wordmaster.gui.custom.ButtonFactory;
import com.wordmaster.gui.View;
import com.wordmaster.gui.custom.WordmasterUtils;
import com.wordmaster.gui.i18n.Language;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SoundButtonListener;
import com.wordmaster.model.GameModel;
import com.wordmaster.model.algorithm.Vocabulary;
import com.wordmaster.model.exception.ModelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StartupPage extends Page {
    private static final Logger logger = LoggerFactory.getLogger(StartupPage.class);
    private Map<Buttons, JButton> pageButtons = new HashMap<>();

    private enum Buttons {
        NEW_GAME, LOAD_GAME, ANALYZE_REPLAY, SETTINGS, EXIT
    }

    public StartupPage(View parentView) {
        super(parentView);
    }

    public void initialize() {
        page = Box.createVerticalBox();

        page.add(Box.createVerticalGlue());
        JButton newGameBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.NEW_GAME_SETTINGS);
        newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        pageButtons.put(Buttons.NEW_GAME, newGameBtn);
        page.add(newGameBtn);

        page.add(Box.createVerticalGlue());
        JButton loadGameBtn = ButtonFactory.getStandardButton();
        loadGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameBtn.addActionListener(getLoadGameActionListener());
        pageButtons.put(Buttons.LOAD_GAME, loadGameBtn);
        page.add(loadGameBtn);

        page.add(Box.createVerticalGlue());
        JButton analyzeReplayBtn = ButtonFactory.getStandardButton();
        analyzeReplayBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        analyzeReplayBtn.addActionListener(getAnalyzeReplayActionListener());
        pageButtons.put(Buttons.ANALYZE_REPLAY, analyzeReplayBtn);
        page.add(analyzeReplayBtn);

        page.add(Box.createVerticalGlue());
        JButton settingsBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.SETTINGS);
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        pageButtons.put(Buttons.SETTINGS, settingsBtn);
        page.add(settingsBtn);

        page.add(Box.createVerticalGlue());
        JButton exitBtn = ButtonFactory.getStandardButton();
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(
                new MenuItemListener(parentView));
        exitBtn.addActionListener(
                new SoundButtonListener(parentView, SoundButtonListener.SoundType.MENU));
        pageButtons.put(Buttons.EXIT, exitBtn);
        page.add(exitBtn);

        page.add(Box.createVerticalGlue());

        setButtonsText();
    }
    protected void updateLanguage() {
        setButtonsText();
    }

    protected void setButtonsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageButtons.get(Buttons.NEW_GAME).setText(resourceBundle.getString("new_game"));
        pageButtons.get(Buttons.LOAD_GAME).setText(resourceBundle.getString("load_game"));
        pageButtons.get(Buttons.ANALYZE_REPLAY).setText(resourceBundle.getString("analyze_replay"));
        pageButtons.get(Buttons.SETTINGS).setText(resourceBundle.getString("settings"));
        pageButtons.get(Buttons.EXIT).setText(resourceBundle.getString("exit"));
    }

    private GameModel loadGame(boolean isReplay) {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./"));
        fc.showOpenDialog(parentView.getFrame());
        File file = fc.getSelectedFile();
        if (file == null) {
            return null;
        }

        Language language = parentView.getSettings().getLanguage();
        Future<Vocabulary> vocabulary = Vocabulary.getVocabulary(language);
        if (!vocabulary.isDone()) {
            WordmasterUtils.showErrorAlert(parentView.getFrame(), "Wait please, vocabulary is still loading");
            return null;
        }
        try {
            return GameModel.load(file, vocabulary.get(), isReplay);
        } catch (ExecutionException | InterruptedException exception) {
            logger.error("Exception during loading vocabulary", exception);
            WordmasterUtils.showErrorAlert(parentView.getFrame(), "Vocabulary loading error");
            return null;
        } catch (ModelException exception) {
            logger.error("Exception during loading model", exception);
            WordmasterUtils.showErrorAlert(parentView.getFrame(), "Game loading error");
            return null;
        }
    }

    private ActionListener getLoadGameActionListener() {
        return (ActionEvent e) -> {
            GamePage gamePage = (GamePage)parentView.getPage(View.Pages.GAME);
            GameModel model = loadGame(false);
            if (model != null) {
                gamePage.setModel(model);
                new MenuItemListener(parentView, View.Pages.GAME).actionPerformed(e);
            }
        };
    }

    private ActionListener getAnalyzeReplayActionListener() {
        return (ActionEvent e) -> {
            GamePage gamePage = (GamePage)parentView.getPage(View.Pages.GAME);
            GameModel model = loadGame(true);
            if (model != null) {
                gamePage.setModel(model);
                new MenuItemListener(parentView, View.Pages.GAME).actionPerformed(e);
            }
        };
    }
}
