package com.wordmaster.gui.page;

import com.wordmaster.gui.custom.ButtonFactory;
import com.wordmaster.gui.custom.LabelFactory;
import com.wordmaster.gui.View;
import com.wordmaster.gui.custom.LimitCharactersDocument;
import com.wordmaster.gui.custom.WordmasterUtils;
import com.wordmaster.gui.i18n.Language;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.model.ComputerPlayer;
import com.wordmaster.model.GameField;
import com.wordmaster.model.GameModel;
import com.wordmaster.model.Player;
import com.wordmaster.model.algorithm.Vocabulary;
import com.wordmaster.model.exception.ModelInitializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Represent the page with new game settings
 *
 * @version 1.0
 * @author Mike
 */
public class NewGameSettingsPage extends Page {
    private static final Logger logger = LoggerFactory.getLogger(NewGameSettingsPage.class);
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

    private JTextField startWordInput;

    public NewGameSettingsPage(View parentView) {
        super(parentView);
    }

    private enum Labels {
        HEADER, START_WORD,
        FP_NAME, FP_IS_COMPUTER, FP_DIFFICULTY, FP_DELAY,
        SP_NAME, SP_IS_COMPUTER, SP_DIFFICULTY, SP_DELAY,
    }

    private enum Buttons {
        BACK, START, RANDOM_GO
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

        firstPlayerDelayInput = getDelayComboBox(new Integer[]{0, 100, 500, 1000}, 500);
        firstPlayerDelayInput.setEnabled(false);
        secondPlayerDelayInput = getDelayComboBox(new Integer[]{0, 100, 500, 1000}, 500);
        secondPlayerDelayInput.setEnabled(false);

        firstPlayerIsComputerInput = getIsComputerCheckbox(
                firstPlayerDifficultyInput, firstPlayerDelayInput
        );

        secondPlayerIsComputerInput = getIsComputerCheckbox(
                secondPlayerDifficultyInput, secondPlayerDelayInput
        );


        startWordInput = getLimitedTextField(GameField.MAX_START_WORD_SIZE);

        JButton backBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.STARTUP);
        pageButtons.put(Buttons.BACK, backBtn);

        JButton startBtn = ButtonFactory.getStandardButton(parentView);
        startBtn.addActionListener(getNewGameBtnListener());
        pageButtons.put(Buttons.START, startBtn);

        JButton randomGoBtn = ButtonFactory.getStandardButton(parentView);
        randomGoBtn.addActionListener(getRandomGoBtnListener());
        pageButtons.put(Buttons.RANDOM_GO, randomGoBtn);

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
        JPanel commonSettingsPanelRow2 = new JPanel();
        commonSettingsPanelRow2.add(startWord);
        commonSettingsPanelRow2.add(startWordInput);
        commonSettingsPanel.add(Box.createVerticalGlue());
        commonSettingsPanel.add(commonSettingsPanelRow2);
        commonSettingsPanel.add(Box.createVerticalGlue());

        Box bottomButtonsPanel = Box.createHorizontalBox();

        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(backBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(startBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(randomGoBtn);
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
        update();
    }

    protected void update() {
        setLabelsText();
        setButtonsText();
        setTitledBordersText();
    }

    /**
     * Helper method to update text on buttons
     */
    private void setButtonsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();

        pageButtons.get(Buttons.START).setText(resourceBundle.getString("start"));
        pageButtons.get(Buttons.BACK).setText(resourceBundle.getString("back"));
        pageButtons.get(Buttons.RANDOM_GO).setText(resourceBundle.getString("random_go"));
    }

    /**
     * Helper method to update text on labels
     */
    private void setLabelsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageLabels.get(Labels.HEADER).setText(resourceBundle.getString("new_game"));
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

    /**
     * Helper method to update text on titled borders
     */
    private void setTitledBordersText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageTitledBorders.get(TitledBorders.FP_BORDER).setTitle(resourceBundle.getString("player")+" 1");
        pageTitledBorders.get(TitledBorders.SP_BORDER).setTitle(resourceBundle.getString("player")+" 2");
    }

    // helper method to construct gui components
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
    private <T> JComboBox<T> getDelayComboBox(T[] values, T defaultValue) {
        JComboBox<T> comboBox = getSmallComboBox(values, defaultValue);
        comboBox.setPreferredSize(new Dimension(100, 25));
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

    /**
     * Parses players from the gui controls
     *
     * @return list of players on success validation, null on error
     */
    private List<Player> validateAndGetPlayers() {

        String fpName = firstPlayerNameInput.getText();
        String spName = secondPlayerNameInput.getText();

        // validation
        if (fpName.length() < Player.MIN_NAME_LENGTH ) {
            WordmasterUtils.showErrorAlert(parentView.getFrame(),
                    "e_fp_name", parentView.getSettings().getLanguage());
            return null;
        }
        if (spName.length() < Player.MIN_NAME_LENGTH) {
            WordmasterUtils.showErrorAlert(parentView.getFrame(),
                    "e_sp_name", parentView.getSettings().getLanguage());
            return null;
        }
        if (fpName.equals(spName)) {
            WordmasterUtils.showErrorAlert(parentView.getFrame(),
                    "e_players_equal_names", parentView.getSettings().getLanguage());
            return null;
        }

        // parsing
        List<Player> playerList = new LinkedList<>();
        Player firstPlayer;
        Player secondPlayer;

        if (firstPlayerIsComputerInput.isSelected()) {
            firstPlayer = new ComputerPlayer(
                    fpName,
                    (ComputerPlayer.Difficulty) firstPlayerDifficultyInput.getSelectedItem(),
                    (Integer) firstPlayerDelayInput.getSelectedItem()
            );
        } else {
            firstPlayer = new Player (fpName);
        }
        if (secondPlayerIsComputerInput.isSelected()) {
            secondPlayer = new ComputerPlayer(
                    spName,
                    (ComputerPlayer.Difficulty) secondPlayerDifficultyInput.getSelectedItem(),
                    (Integer) secondPlayerDelayInput.getSelectedItem()
            );
        } else {
            secondPlayer = new Player (spName);
        }
        playerList.add(firstPlayer);
        playerList.add(secondPlayer);
        return playerList;
    }

    /**
     * Validates the start word by language and field size
     * NOTE: doesn't validate the word semantics
     *
     * @return word on success validation, null on error
     */
    private String validateAndGetStartWord() {
        // validation
        Language language = parentView.getSettings().getLanguage();
        String startWord = startWordInput.getText();

        if (startWord.length() < GameField.MIN_START_WORD_SIZE || !language.validateWordLetters(startWord)) {
            WordmasterUtils.showErrorAlert(parentView.getFrame(),
                    "e_invalid_start_word", parentView.getSettings().getLanguage());
            return null;
        }
        return startWord;
    }

    /**
     * Helper method to get the vocabulary from the continious future task.
     * If task is not finished yet, shows warning alert
     * @return vocabulary object on success, null on error
     */
    private Vocabulary getVocabulary() {
        Language language = parentView.getSettings().getLanguage();
        Future<Vocabulary> vocabulary = Vocabulary.getVocabulary(language);
        if (!vocabulary.isDone()) {
            WordmasterUtils.showErrorAlert(parentView.getFrame(),
                    "e_vocabulary_loading", parentView.getSettings().getLanguage());
            return null;
        }
        try {
            return vocabulary.get();
        } catch (ExecutionException | InterruptedException exception) {
            logger.error("Exception during loading vocabulary", exception);
            WordmasterUtils.showErrorAlert(parentView.getFrame(),
                    "e_vocabulary_loading", parentView.getSettings().getLanguage());
            return null;
        }
    }

    /**
     * Creates the game model, sets it to the game page and switches the view to
     * the game page
     *
     * @param playerList the list of players
     * @param vocabulary the game vocabulary
     * @param startWord the start word
     */
    private void startGame(List<Player> playerList, Vocabulary vocabulary, String startWord) {
        try {
            GameModel newGameModel = new GameModel(playerList, vocabulary, startWord);
            GamePage gamePage = (GamePage)parentView.getPage(View.Pages.GAME);
            gamePage.setModel(newGameModel);
        } catch (ModelInitializeException e) {
            logger.error("Illegal game initializing", e);
            WordmasterUtils.showErrorAlert(parentView.getFrame(),
                    "e_game_initializing", parentView.getSettings().getLanguage());
            return;
        }
        new MenuItemListener(parentView, View.Pages.GAME).actionPerformed(null);
    }

    /**
     * Retrieves necessary parameters from helper methods and calls startGame
     *
     * @return listener to start game
     */
    private ActionListener getNewGameBtnListener() {
        return (ActionEvent e) -> {
            Vocabulary vocabulary = getVocabulary();
            if (vocabulary == null) return;

            List<Player> players = validateAndGetPlayers();
            if(players == null) return;

            String startWord = validateAndGetStartWord();
            if (startWord == null) return;

            startGame(players, vocabulary, startWord);
        };
    }

    /**
     * Starts game with random player names and start word.
     * Player types and computer player settings stays the same
     *
     * @return
     */
    private ActionListener getRandomGoBtnListener() {
        return (ActionEvent e) -> {
                Vocabulary vocabulary = getVocabulary();
                if (vocabulary == null) return;

                ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
                String[] playerNames = resourceBundle.getString("player_names").split("\t*,");
                if (playerNames.length < 2) {
                    logger.error("Not enough player names examples");
                    WordmasterUtils.showErrorAlert(parentView.getFrame(),
                            "e_game_initializing", parentView.getSettings().getLanguage());
                    return;
                }
                int fpName = (int) (Math.random() * playerNames.length);
                int spName;
                do spName = (int) (Math.random() * playerNames.length); while (spName == fpName);

                firstPlayerNameInput.setText(playerNames[fpName]);
                secondPlayerNameInput.setText(playerNames[spName]);

                List<Player> players = validateAndGetPlayers();
                if (players == null) return;
            try {
                String startWord = vocabulary.getRandomWord(GameField.MAX_START_WORD_SIZE);

                startGame(players, vocabulary, startWord);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Unexpected exception at random&go", ex);
            }
        };
    }

}
