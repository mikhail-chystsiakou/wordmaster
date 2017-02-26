package com.wordmaster.gui;

import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SoundButtonListener;

import javax.swing.*;
import java.awt.*;

/**
 * Builds main game panes
 *
 * @author zoxal
 * @version 1.1
 */
public class PaneFactory {
    protected GameFrame targetFrame;

    public PaneFactory(GameFrame targetFrame) {
        this.targetFrame = targetFrame;
    }

    public JPanel buildMainPane() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));

        mainPane.add(Box.createVerticalGlue());
        JButton newGameBtn = new JButton("Новая игра");
        newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameBtn.addActionListener(new MenuItemListener(targetFrame, GameFrame.Pane.NEW_GAME));
        mainPane.add(newGameBtn);

        mainPane.add(Box.createVerticalGlue());
        JButton loadGameBtn = new JButton("Загрузить игру");
        loadGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameBtn.setEnabled(false);
        mainPane.add(loadGameBtn);

        mainPane.add(Box.createVerticalGlue());
        JButton settingsBtn = new JButton("Настройки");
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.addActionListener(new MenuItemListener(targetFrame, GameFrame.Pane.SETTINGS));
        mainPane.add(settingsBtn);

        mainPane.add(Box.createVerticalGlue());
        JButton exitBtn = new JButton("Выход");
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(new MenuItemListener());
        mainPane.add(exitBtn);

        mainPane.add(Box.createVerticalGlue());
        return mainPane;
    }

    public JPanel buildSettingsPane() {
        return new JPanel();
    }
    public JPanel buildAboutPane() {
        return new JPanel();
    }
    public JPanel buildNewGamePane() {
        return new JPanel();
    }
    public JPanel buildGamePane() {
        return new JPanel();
    }
}
