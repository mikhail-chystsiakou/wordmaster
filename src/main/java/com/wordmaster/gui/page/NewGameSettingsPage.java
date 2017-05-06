package com.wordmaster.gui.page;

import com.wordmaster.gui.custom.ButtonFactory;
import com.wordmaster.gui.custom.LabelFactory;
import com.wordmaster.gui.View;
import com.wordmaster.gui.custom.LimitCharactersDocument;
import com.wordmaster.gui.custom.WordmasterUtils;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.model.ComputerPlayer;
import com.wordmaster.model.GameField;
import com.wordmaster.model.GameModel;
import com.wordmaster.model.Player;
import com.wordmaster.model.exception.ModelInitializeException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class NewGameSettingsPage extends Page {
    private Map<Labels, JLabel> pageLabels = new HashMap<>();
    private Map<Buttons, JButton> pageButtons = new HashMap<>();
    private Map<TitledBorders, TitledBorder> pageTitledBorders = new HashMap<>();

    private JTextField firstPlayerNameInput;
    private JTextField secondPlayerNameInput;

    private JComboBox<ComputerPlayer.Difficulty> firstPlayerDifficultyInput;
    private JComboBox<ComputerPlayer.Difficulty> secondPlayerDifficultyInput;

    private JComboBox<Integer> firstPlayerDelayInput;
    private JComboBox<Integer> secondPlayerDelayInput;

    private JCheckBox firstPlayerIsComputerInput;
    private JCheckBox secondPlayerIsComputerInput;

    private JComboBox<Integer> gameTimeInput;
    private JComboBox<Integer> moveTimeInput;

    private JTextField startWordInput;

    public NewGameSettingsPage(View parentView) {
        super(parentView);
    }

    private enum Labels {
        HEADER, GAME_TIME, MOVE_TIME, START_WORD,
        FP_NAME, FP_IS_COMPUTER, FP_DIFFICULTY, FP_DELAY,
        SP_NAME, SP_IS_COMPUTER, SP_DIFFICULTY, SP_DELAY,
    }

    private enum Buttons {
        BACK, START
    }

    private enum TitledBorders {
        FP_BORDER, SP_BORDER
    }

    public void initialize() {
        page = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel header = LabelFactory.getHeaderLabel();
        pageLabels.put(Labels.HEADER, header);

        JLabel firstPlayerName = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_NAME, firstPlayerName);

        JLabel secondPlayerName = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_NAME, secondPlayerName);

        JLabel firstPlayerIsComputer = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_IS_COMPUTER, firstPlayerIsComputer);

        JLabel secondPlayerIsComputer = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_IS_COMPUTER, secondPlayerIsComputer);

        JLabel firstPlayerDifficulty = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_DIFFICULTY, firstPlayerDifficulty);

        JLabel secondPlayerDifficulty = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_DIFFICULTY, secondPlayerDifficulty);

        JLabel firstPlayerDelay = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_DELAY, firstPlayerDelay);

        JLabel secondPlayerDelay = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_DELAY, secondPlayerDelay);

        JLabel gameTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.GAME_TIME, gameTime);

        JLabel moveTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.MOVE_TIME, moveTime);

        JLabel startWord = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.START_WORD, startWord);

        firstPlayerNameInput = getLimitedTextField(Player.MAX_NAME_LENGTH);
        secondPlayerNameInput = getLimitedTextField(Player.MAX_NAME_LENGTH);

        firstPlayerDifficultyInput =
                getWideComboBox(ComputerPlayer.Difficulty.values(), ComputerPlayer.Difficulty.EASY);
        firstPlayerDifficultyInput.setEnabled(false);
        secondPlayerDifficultyInput =
                getWideComboBox(ComputerPlayer.Difficulty.values(), ComputerPlayer.Difficulty.EASY);
        secondPlayerDifficultyInput.setEnabled(false);

        firstPlayerDelayInput = getSmallComboBox(new Integer[]{1, 5, 10}, 5);
        firstPlayerDelayInput.setEnabled(false);
        secondPlayerDelayInput = getSmallComboBox(new Integer[]{1, 5, 10}, 5);
        secondPlayerDelayInput.setEnabled(false);

        gameTimeInput = getSmallComboBox(new Integer[]{1, 5, 10}, 5);
        moveTimeInput = getSmallComboBox(new Integer[]{1, 5, 10}, 5);

        firstPlayerIsComputerInput = getIsComputerCheckbox(
                firstPlayerDifficultyInput, firstPlayerDelayInput
        );

        secondPlayerIsComputerInput = getIsComputerCheckbox(
                secondPlayerDifficultyInput, secondPlayerDelayInput
        );


        startWordInput = getLimitedTextField(5);

        JButton backBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.STARTUP);
        pageButtons.put(Buttons.BACK, backBtn);

        JButton startBtn = ButtonFactory.getStandardButton();
        startBtn.addActionListener(getNewGameBtnListener());
        pageButtons.put(Buttons.START, startBtn);

        JPanel firstPlayerPanel = new JPanel(new GridBagLayout());
        TitledBorder firstPlayerTitledBorder =  new TitledBorder("");
        pageTitledBorders.put(TitledBorders.FP_BORDER, firstPlayerTitledBorder);
        firstPlayerPanel.setBorder(BorderFactory.createCompoundBorder(
                firstPlayerTitledBorder,
                BorderFactory.createEmptyBorder(0, 50, 10, 10)
        ));
        JPanel firstPlayerPanelRow1 = new JPanel();
        firstPlayerPanelRow1.add(firstPlayerName);
        firstPlayerPanelRow1.add(firstPlayerNameInput);
        JPanel firstPlayerPanelRow2 = new JPanel();
        firstPlayerPanelRow2.add(firstPlayerIsComputer);
        firstPlayerPanelRow2.add(firstPlayerIsComputerInput);
        JPanel firstPlayerPanelRow3 = new JPanel();
        firstPlayerPanelRow3.add(firstPlayerDifficulty);
        firstPlayerPanelRow3.add(firstPlayerDifficultyInput);
        JPanel firstPlayerPanelRow4 = new JPanel();
        firstPlayerPanelRow4.add(firstPlayerDelay);
        firstPlayerPanelRow4.add(firstPlayerDelayInput);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;

        firstPlayerPanel.add(firstPlayerPanelRow1, gbc);
        gbc.gridy = 1;
        firstPlayerPanel.add(firstPlayerPanelRow2, gbc);
        gbc.gridy = 2;
        firstPlayerPanel.add(firstPlayerPanelRow3, gbc);
        gbc.gridy = 3;
        firstPlayerPanel.add(firstPlayerPanelRow4, gbc);

        JPanel secondPlayerPanel = new JPanel(new GridBagLayout());
        TitledBorder secondPlayerTitledBorder =  new TitledBorder("");
        pageTitledBorders.put(TitledBorders.SP_BORDER, secondPlayerTitledBorder);
        secondPlayerPanel.setBorder(BorderFactory.createCompoundBorder(
                secondPlayerTitledBorder,
                BorderFactory.createEmptyBorder(0, 50, 10, 10)
        ));
        JPanel secondPlayerPanelRow1 = new JPanel();
        secondPlayerPanelRow1.add(secondPlayerName);
        secondPlayerPanelRow1.add(secondPlayerNameInput);
        JPanel secondPlayerPanelRow2 = new JPanel();
        secondPlayerPanelRow2.add(secondPlayerIsComputer);
        secondPlayerPanelRow2.add(secondPlayerIsComputerInput);
        JPanel secondPlayerPanelRow3 = new JPanel();
        secondPlayerPanelRow3.add(secondPlayerDifficulty);
        secondPlayerPanelRow3.add(secondPlayerDifficultyInput);
        JPanel secondPlayerPanelRow4 = new JPanel();
        secondPlayerPanelRow4.add(secondPlayerDelay);
        secondPlayerPanelRow4.add(secondPlayerDelayInput);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        secondPlayerPanel.add(secondPlayerPanelRow1, gbc);
        gbc.gridy = 1;
        secondPlayerPanel.add(secondPlayerPanelRow2, gbc);
        gbc.gridy = 2;
        secondPlayerPanel.add(secondPlayerPanelRow3, gbc);
        gbc.gridy = 3;
        secondPlayerPanel.add(secondPlayerPanelRow4, gbc);

        Box commonSettingsPanel = Box.createVerticalBox();

        JPanel commonSettingsPanelRow1 = new JPanel();
        commonSettingsPanelRow1.add(gameTime);
        commonSettingsPanelRow1.add(gameTimeInput);
        commonSettingsPanelRow1.add(Box.createRigidArea(new Dimension(30, 0)));
        commonSettingsPanelRow1.add(moveTime);
        commonSettingsPanelRow1.add(moveTimeInput);
        commonSettingsPanel.add(commonSettingsPanelRow1);
        JPanel commonSettingsPanelRow2 = new JPanel();
        commonSettingsPanelRow2.add(startWord);
        commonSettingsPanelRow2.add(startWordInput);
        commonSettingsPanel.add(commonSettingsPanelRow1);
        commonSettingsPanel.add(commonSettingsPanelRow2);

        Box bottomButtonsPanel = Box.createHorizontalBox();

        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(backBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(startBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        page.add(header, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth=1;
        gbc.weightx=1;
        gbc.weighty=3;
        gbc.insets = new Insets(0, 20, 0, 10);
        gbc.fill = GridBagConstraints.BOTH;
        page.add(firstPlayerPanel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 0, 20);
        page.add(secondPlayerPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.ipadx = 0;
        gbc.insets = new Insets(20, 0, 0, 0);
        page.add(commonSettingsPanel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        page.add(bottomButtonsPanel, gbc);
        updateLanguage();
    }

    protected void updateLanguage() {
        setLabelsText();
        setButtonsText();
        setTitledBordersText();
    }

    private void setButtonsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();

        pageButtons.get(Buttons.START).setText(resourceBundle.getString("start"));
        pageButtons.get(Buttons.BACK).setText(resourceBundle.getString("back"));
    }

    private void setLabelsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageLabels.get(Labels.HEADER).setText(resourceBundle.getString("new_game"));
        pageLabels.get(Labels.GAME_TIME).setText(resourceBundle.getString("game_time")+": ");
        pageLabels.get(Labels.MOVE_TIME).setText(resourceBundle.getString("move_time")+": ");
        pageLabels.get(Labels.START_WORD).setText(resourceBundle.getString("start_word")+": ");

        pageLabels.get(Labels.FP_NAME).setText(resourceBundle.getString("name")+": ");
        pageLabels.get(Labels.FP_IS_COMPUTER).setText(resourceBundle.getString("is_computer")+": ");
        pageLabels.get(Labels.FP_DIFFICULTY).setText(resourceBundle.getString("difficulty")+": ");
        pageLabels.get(Labels.FP_DELAY).setText(resourceBundle.getString("delay")+": ");

        pageLabels.get(Labels.SP_NAME).setText(resourceBundle.getString("name")+": ");
        pageLabels.get(Labels.SP_IS_COMPUTER).setText(resourceBundle.getString("is_computer")+": ");
        pageLabels.get(Labels.SP_DIFFICULTY).setText(resourceBundle.getString("difficulty")+": ");
        pageLabels.get(Labels.SP_DELAY).setText(resourceBundle.getString("delay")+": ");
    }

    private void setTitledBordersText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageTitledBorders.get(TitledBorders.FP_BORDER).setTitle(resourceBundle.getString("player")+" 1");
        pageTitledBorders.get(TitledBorders.SP_BORDER).setTitle(resourceBundle.getString("player")+" 2");
    }

    private JCheckBox getIsComputerCheckbox(JComboBox<?> difficultyInput, JComboBox<?> delayInput) {
        JCheckBox isComputerInput = new JCheckBox();
        isComputerInput.addActionListener((ActionEvent e) -> {
            JCheckBox source = (JCheckBox)e.getSource();
            if (source.isSelected()) {
                delayInput.setEnabled(true);
                difficultyInput.setEnabled(true);
            } else {
                delayInput.setEnabled(false);
                difficultyInput.setEnabled(false);
            }
        });
        isComputerInput.setSelected(false);
        return isComputerInput;
    }

    private <T> JComboBox<T> getSmallComboBox(T[] values, T defaultValue) {
        JComboBox<T> comboBox = new JComboBox<>(values);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        comboBox.setPreferredSize(new Dimension(50, 25));
        comboBox.setSelectedItem(defaultValue);
        return comboBox;
    }

    private <T> JComboBox<T> getWideComboBox(T[] values, T defaultValue) {
        JComboBox<T> comboBox = getSmallComboBox(values, defaultValue);
        comboBox.setPreferredSize(new Dimension(100, 25));
        return comboBox;
    }

    private JTextField getLimitedTextField(int size) {
        JTextField textField = new JTextField();
        Font textFieldFont = new Font("Arial", Font.PLAIN, 16);
        textField.setFont(textFieldFont);
        textField.setDocument(new LimitCharactersDocument(size));
        textField.setColumns(size);
        return textField;
    }

    private ActionListener getNewGameBtnListener() {
        return (ActionEvent e) -> {
            // validation
            if (firstPlayerNameInput.getText().length() < Player.MIN_NAME_LENGTH) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Invalid first player name");
                return;
            }
            if (secondPlayerNameInput.getText().length() < Player.MIN_NAME_LENGTH) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Invalid second player name");
                return;
            }
            if (startWordInput.getText().length() < GameField.MIN_START_WORD_SIZE) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Illegal start word size");
                return;
            }
            if (firstPlayerNameInput.getText().equals(secondPlayerNameInput.getText())) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Equal player names");
                return;
            }
            List<Player> playerList = new LinkedList<>();
            Player firstPlayer;
            Player secondPlayer;

            if (firstPlayerIsComputerInput.isSelected()) {
                firstPlayer = new ComputerPlayer(
                        firstPlayerNameInput.getText(),
                        (Integer) gameTimeInput.getSelectedItem(),
                        (Integer) moveTimeInput.getSelectedItem(),
                        (ComputerPlayer.Difficulty) firstPlayerDifficultyInput.getSelectedItem(),
                        (Integer) firstPlayerDelayInput.getSelectedItem()
                );
            } else {
                firstPlayer = new Player (
                        firstPlayerNameInput.getText(),
                        (Integer) gameTimeInput.getSelectedItem(),
                        (Integer) moveTimeInput.getSelectedItem()
                );
            }
            if (secondPlayerIsComputerInput.isSelected()) {
                secondPlayer = new ComputerPlayer(
                        secondPlayerNameInput.getText(),
                        (Integer) gameTimeInput.getSelectedItem(),
                        (Integer) moveTimeInput.getSelectedItem(),
                        (ComputerPlayer.Difficulty) secondPlayerDifficultyInput.getSelectedItem(),
                        (Integer) secondPlayerDelayInput.getSelectedItem()
                );
            } else {
                secondPlayer = new Player (
                        secondPlayerNameInput.getText(),
                        (Integer) gameTimeInput.getSelectedItem(),
                        (Integer) moveTimeInput.getSelectedItem()
                );
            }
            playerList.add(firstPlayer);
            playerList.add(secondPlayer);
            try {
                GameModel newGameModel = new GameModel(playerList, null, startWordInput.getText());
                GamePage gamePage = (GamePage)parentView.getPage(View.Pages.GAME);
                gamePage.setModel(newGameModel);
            } catch (ModelInitializeException exception) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Illegal game initializing");
                return;
            }

            new MenuItemListener(parentView, View.Pages.GAME).actionPerformed(e);
        };
    }
}
