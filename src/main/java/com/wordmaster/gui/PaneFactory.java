package com.wordmaster.gui;

import com.wordmaster.gui.custom.TopMenuLabel;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SaveSettingsListener;
import com.wordmaster.gui.listeners.SoundButtonListener;
import com.wordmaster.model.Settings;

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
        Settings.Language languages[] = {   Settings.Language.RUSSIAN,
                                            Settings.Language.ENGLISH};
        JComboBox<Settings.Language> languageJComboBox = new JComboBox<Settings.Language>(languages);
        languageControls.add(new JComboBox<Settings.Language>(languages));
        languageControls.add(Box.createHorizontalGlue());
        // Look and Feel settings
        lafControls.add(Box.createHorizontalGlue());

        Settings.SupportedLAF lafs[] = {
                Settings.SupportedLAF.METAL,
                Settings.SupportedLAF.SYSTEM,
                Settings.SupportedLAF.WEBLAF
        };
        lafControls.add(new JLabel("Look&feel"));

        JComboBox<Settings.SupportedLAF> lafjComboBox = new JComboBox<Settings.SupportedLAF>(lafs);
        lafControls.add(lafjComboBox);
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
        saveBtn.addActionListener(new SaveSettingsListener(targetFrame, lafjComboBox, languageJComboBox));

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
        JPanel newGamePane = new JPanel();
        newGamePane.setLayout(new BoxLayout(newGamePane, BoxLayout.PAGE_AXIS));

        // Pane name
        JPanel paneName = new JPanel();

        JLabel paneNameLabel = new TopMenuLabel("Новая игра");
        paneNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        paneName.add(paneNameLabel);

        JPanel playerSettings = new JPanel();

        // Left player settings
        JPanel leftPlayerPane = new JPanel();
        leftPlayerPane.setBorder(BorderFactory.createTitledBorder("Игрок 1"));
        leftPlayerPane.setLayout(new BoxLayout(leftPlayerPane, BoxLayout.PAGE_AXIS));
        JPanel leftNameRow = new JPanel();
        leftNameRow.add(new JLabel("Имя: "));
        leftNameRow.add(new JTextField(10));
        leftNameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPlayerPane.add(leftNameRow);
        JCheckBox leftComputer = new JCheckBox("Компьютер: ");
        leftComputer.setHorizontalTextPosition(SwingConstants.LEFT);
        leftComputer.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPlayerPane.add(leftComputer);

        // Right player settings
        JPanel rightPlayerPane = new JPanel();
        rightPlayerPane.setBorder(BorderFactory.createTitledBorder("Игрок 2"));
        rightPlayerPane.setLayout(new BoxLayout(rightPlayerPane, BoxLayout.PAGE_AXIS));

        JPanel rightNameRow = new JPanel();
        rightNameRow.add(new JLabel("Имя: "));
        rightNameRow.add(new JTextField(10));
        rightNameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPlayerPane.add(rightNameRow);
        JCheckBox rightComputer = new JCheckBox("Компьютер: ");
        rightComputer.setHorizontalTextPosition(SwingConstants.LEFT);
        rightComputer.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPlayerPane.add(rightComputer);

        playerSettings.add(leftPlayerPane);
        playerSettings.add(Box.createHorizontalStrut(30));
        playerSettings.add(rightPlayerPane);

        // Bottom controls

        JPanel wordPanel = new JPanel();
        wordPanel.add(Box.createHorizontalGlue());
        wordPanel.add(new JLabel("Стартовое слово:"));
        wordPanel.add(new JTextField(10));
        wordPanel.add(Box.createHorizontalGlue());


        JPanel navButtons = new JPanel();
        navButtons.setLayout(new BoxLayout(navButtons, BoxLayout.LINE_AXIS));
        navButtons.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backBtn = new JButton("Назад");
        backBtn.addActionListener(new MenuItemListener(targetFrame, GameFrame.Pane.MAIN));
        JButton startBtn = new JButton("Начать");
        startBtn.addActionListener(new MenuItemListener(targetFrame, GameFrame.Pane.GAME));

        navButtons.add(Box.createHorizontalGlue());
        navButtons.add(backBtn);
        navButtons.add(Box.createHorizontalStrut(50));
        navButtons.add(startBtn);
        navButtons.add(Box.createHorizontalGlue());


        newGamePane.add(Box.createVerticalStrut(20));
        newGamePane.add(paneName);
        newGamePane.add(playerSettings);
        newGamePane.add(wordPanel);
        newGamePane.add(navButtons);
        newGamePane.add(Box.createVerticalStrut(20));
        return newGamePane;
    }

    public JPanel buildGamePane() {
        JPanel gamePane = new JPanel();
            gamePane.setLayout(new BoxLayout(gamePane, BoxLayout.PAGE_AXIS));

        JPanel gameLabelPane = new JPanel();
        gameLabelPane.setMaximumSize(new Dimension(640, 50));
        JPanel gameFlowPane = new JPanel();
            gameFlowPane.setMaximumSize(new Dimension(640, 380));
            gameFlowPane.setLayout(new BoxLayout(gameFlowPane, BoxLayout.LINE_AXIS));
        JPanel gameControlButtons = new JPanel();
            gameControlButtons.setMaximumSize(new Dimension(640, 50));
            //gameControlButtons.setLayout(new BoxLayout(gameControlButtons, BoxLayout.PAGE_AXIS));

        JPanel leftPlayerPane = new JPanel();
            leftPlayerPane.setMaximumSize(new Dimension(140, 380));
            //leftPlayerPane.setLayout(new BoxLayout(leftPlayerPane, BoxLayout.PAGE_AXIS));

        JPanel centralPane = new JPanel();
            centralPane.setMaximumSize(new Dimension(360, 380));
            centralPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                centralPane.getBorder()));
            centralPane.setLayout(new BoxLayout(centralPane, BoxLayout.PAGE_AXIS));
            JPanel wordsGridPane = new JPanel();
                wordsGridPane.setMaximumSize(new Dimension(360, 280));
                //wordsGridPane.setLayout(new GridLayout());
            // TODO: separate creating letter selector functionality
            JPanel letterSelectingPane = new JPanel();
                letterSelectingPane.setMaximumSize(new Dimension(360, 100));
                letterSelectingPane.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.blue),
                        letterSelectingPane.getBorder()));
                //letterSelectingPane.setLayout(new GridBagLayout());

        JPanel rightPlayerPane = new JPanel();
            rightPlayerPane.setMaximumSize(new Dimension(140, 380));
            rightPlayerPane.setLayout(new BoxLayout(rightPlayerPane, BoxLayout.PAGE_AXIS));




        // TODO: set correct player name
        // Left player
        JLabel leftPlayerNamePane = new JLabel("Player 1");
        leftPlayerNamePane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JScrollPane leftPlayersWords = new JScrollPane();
        for (int i = 0; i < 10; i++) {
            leftPlayersWords.add(new JLabel("Word "+String.valueOf(i)));
        }
        //leftPlayerPane.add(leftPlayerNamePane);
        //leftPlayerPane.add(leftPlayersWords);

        // Right player
        JLabel rightPlayerNamePane = new JLabel("Player 2");
        rightPlayerNamePane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JScrollPane rightPlayersWords = new JScrollPane();
        for (int i = 0; i < 10; i++) {
            rightPlayersWords.add(new JLabel("Word "+String.valueOf(i)));
        }
        //rightPlayerPane.add(rightPlayerNamePane);
        //rightPlayerPane.add(rightPlayersWords);


        centralPane.add(wordsGridPane);
        centralPane.add(letterSelectingPane);

        centralPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black),
                centralPane.getBorder()));

        gameFlowPane.add(leftPlayerPane);
        gameFlowPane.add(centralPane);
        gameFlowPane.add(rightPlayerPane);

        gameFlowPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.red),
                gameFlowPane.getBorder()));

        gamePane.add(gameLabelPane);
        gamePane.add(gameFlowPane);
        gamePane.add(gameControlButtons);

        return gamePane;
    }
}
