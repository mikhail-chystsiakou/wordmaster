package com.wordmaster.gui;

import com.wordmaster.gui.custom.TopMenuLabel;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SaveSettingsListener;
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
        JPanel settingsPane = new JPanel();
        settingsPane.setLayout(new BorderLayout());

        // Top panel name label
        JPanel panelName = new JPanel();
        panelName.setBorder(BorderFactory.createEmptyBorder(30,0,20,0));
        JLabel panelNameLabel = new TopMenuLabel("Настройки");
        panelName.add(Box.createHorizontalGlue());
        panelName.add(panelNameLabel);
        panelName.add(Box.createHorizontalGlue());

        // Setting controls
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
        JPanel soundControls = new JPanel();
        JPanel languageControls = new JPanel();
        JPanel lafControls = new JPanel();
        // Sound settings
        soundControls.add(Box.createHorizontalGlue());
        soundControls.add(new JLabel("Звук:"));
        soundControls.add(new JLabel("Полосочка"));
        soundControls.add(Box.createHorizontalGlue());
        // Language settings
        languageControls.add(Box.createHorizontalGlue());
        languageControls.add(new JLabel("Язык:"));
        languageControls.add(new JLabel("ДропБоксик"));
        languageControls.add(Box.createHorizontalGlue());
        // Look and Feel settings
        lafControls.add(Box.createHorizontalGlue());
        lafControls.add(new JLabel("look&feel:"));
        lafControls.add(new JLabel("ДропБоксик"));
        lafControls.add(Box.createHorizontalGlue());

        controls.add(Box.createVerticalGlue());
        controls.add(soundControls);
        controls.add(Box.createVerticalGlue());
        controls.add(languageControls);
        controls.add(Box.createVerticalGlue());
        controls.add(lafControls);
        controls.add(Box.createVerticalGlue());

        // Back and Save buttons
        JPanel bottomButtons = new JPanel();
        JButton backBtn = new JButton("В меню");
        backBtn.addActionListener(new MenuItemListener(targetFrame, GameFrame.Pane.MAIN));
        JButton saveBtn = new JButton("Сохранить");
        saveBtn.addActionListener(new SaveSettingsListener(targetFrame));

        bottomButtons.add(Box.createHorizontalGlue());
        bottomButtons.add(backBtn);
        bottomButtons.add(Box.createHorizontalStrut(50));
        bottomButtons.add(saveBtn);
        bottomButtons.add(Box.createHorizontalGlue());
        bottomButtons.setBorder(BorderFactory.createEmptyBorder(20,0,30,0));


        settingsPane.add(panelName, BorderLayout.PAGE_START);
        settingsPane.add(controls, BorderLayout.CENTER);
        settingsPane.add(bottomButtons, BorderLayout.PAGE_END);
        return settingsPane;
    }
    public JPanel buildNewGamePane() {
        return new JPanel();
    }
    public JPanel buildGamePane() {
        return new JPanel();
    }
}
