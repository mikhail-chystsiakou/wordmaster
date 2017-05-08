package com.wordmaster.gui.page;

import com.wordmaster.gui.custom.ButtonFactory;
import com.wordmaster.gui.custom.LabelFactory;
import com.wordmaster.gui.View;
import com.wordmaster.gui.custom.WordmasterUtils;
import com.wordmaster.gui.i18n.Language;
import com.wordmaster.model.GameField;
import com.wordmaster.model.GameModel;
import com.wordmaster.model.ModelAware;
import com.wordmaster.model.Player;
import com.wordmaster.model.exception.ModelException;
import com.wordmaster.model.exception.ModelStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static com.wordmaster.model.GameField.EMPTY_CELL_VALUE;

public class GamePage extends Page implements ModelAware {
    private final static Logger logger = LoggerFactory.getLogger(GamePage.class);
    private GameModel model;
    private Player currentPlayer;

    private Map<Labels, JLabel> pageLabels = new HashMap<>();
    private Map<Buttons, JButton> pageButtons = new HashMap<>();
    private Map<WordLists, DefaultListModel<String>> pageWordLists = new HashMap<>();

    private JLabel[][] gameFieldLabels;
    private Color DEFAULT_CELL_COLOR = Color.WHITE;
    private Color SELECTED_CELL_COLOR = Color.LIGHT_GRAY;
    private GameField.Cell setCell;
    private GameField.Cell selectedCell;
    private GameField.Word currentWord = new GameField.Word();

    private KeyEventDispatcher keyListener;

    private enum Labels {
        TIME, CURRENT_PLAYER_LABEL, CURRENT_PLAYER_NAME,
        MOVE_NUMBER_LABEL, MOVE_NUMBER,
        FP_HEADER, FP_TIME, FP_SCORE_LABEL, FP_SCORE,
        SP_HEADER, SP_TIME, SP_SCORE_LABEL, SP_SCORE
    }
    private enum Buttons {
        BACK, UNDO, REDO, APPLY, SAVE, SURRENDER
    }
    private enum WordLists {
        FP_LIST, SP_LIST
    }

    public GamePage(View parentView) {
        super(parentView);
    }

    public void preShow() {
        super.preShow();
        if (model == null) {
            throw new PageException("Page can not be shown without model", null);
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyListener);
        // update players names
        System.out.println(model.getPlayers().get(0).getName());
        pageLabels.get(Labels.FP_HEADER).setText(model.getPlayers().get(0).getName());
        pageLabels.get(Labels.SP_HEADER).setText(model.getPlayers().get(1).getName());
    }

    public void postHide() {
        super.postHide();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyListener);
        model.removeModelListener(this);
        model.destroy();
    }

    public void initialize() {
        page = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel firstPlayerHeader = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_HEADER, firstPlayerHeader);

        JLabel secondPlayerHeader = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_HEADER, secondPlayerHeader);

        DefaultListModel<String> fpListModel = new DefaultListModel<>();
        pageWordLists.put(WordLists.FP_LIST, fpListModel);
        JList<String> firstPlayerList = new JList<>(fpListModel);

        DefaultListModel<String> spListModel = new DefaultListModel<>();
        pageWordLists.put(WordLists.SP_LIST, spListModel);
        JList<String> secondPlayerList = new JList<>(spListModel);

        JLabel firstPlayerTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_TIME, firstPlayerTime);

        JLabel secondPlayerTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_TIME, secondPlayerTime);

        JLabel firstPlayerScoreLabel = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_SCORE_LABEL, firstPlayerScoreLabel);

        JLabel firstPlayerScore = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_SCORE, firstPlayerScore);

        JLabel secondPlayerScoreLabel = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_SCORE_LABEL, secondPlayerScoreLabel);

        JLabel secondPlayerScore = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_SCORE, secondPlayerScore);

        JLabel gameTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.TIME, gameTime);

        JLabel gameMoveLabel = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.MOVE_NUMBER_LABEL, gameMoveLabel);

        JLabel gameMove = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.MOVE_NUMBER, gameMove);

        JLabel currentPlayerLabel = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.CURRENT_PLAYER_LABEL, currentPlayerLabel);

        JLabel currentPlayerName = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.CURRENT_PLAYER_NAME, currentPlayerName);

        JButton backBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.STARTUP);
        pageButtons.put(Buttons.BACK, backBtn);

        JButton undoBtn = ButtonFactory.getStandardButton();
        undoBtn.addActionListener(getUndoBtnListener());
        pageButtons.put(Buttons.UNDO, undoBtn);

        JButton applyBtn = ButtonFactory.getStandardButton();
        applyBtn.addActionListener(getApplyBtnListener());
        pageButtons.put(Buttons.APPLY, applyBtn);

        JButton redoBtn = ButtonFactory.getStandardButton();
        redoBtn.addActionListener(getRedoBtnListener());
        pageButtons.put(Buttons.REDO, redoBtn);

        JButton surrenderBtn = ButtonFactory.getStandardButton();
        surrenderBtn.addActionListener(getSurrenderBtnListener());
        pageButtons.put(Buttons.SURRENDER, surrenderBtn);

        JButton saveBtn = ButtonFactory.getStandardButton();
        saveBtn.addActionListener(getSaveBtnListener());
        pageButtons.put(Buttons.SAVE, saveBtn);


        JPanel firstPlayerPanel = new JPanel(new GridBagLayout());
        firstPlayerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK),
            BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));

        JPanel firstPlayerTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstPlayerTimePanel.add(firstPlayerTime);

        JPanel firstPlayerScorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstPlayerScorePanel.add(firstPlayerScoreLabel);
        firstPlayerScorePanel.add(firstPlayerScore);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1/2;
        gbc.weightx = 1;
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        firstPlayerPanel.add(firstPlayerHeader, gbc);

        gbc.gridy = 2;
        gbc.ipady = 0;
        gbc.anchor = GridBagConstraints.WEST;
        firstPlayerPanel.add(firstPlayerTimePanel, gbc);

        gbc.gridy = 3;
        firstPlayerPanel.add(firstPlayerScorePanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 3.5;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane fpScrollPane = new JScrollPane(firstPlayerList);
        fpScrollPane.setMaximumSize(new Dimension(80, 295));
        fpScrollPane.setPreferredSize(new Dimension(80, 295));
        fpScrollPane.setMinimumSize(new Dimension(80, 295));
        firstPlayerPanel.add(fpScrollPane, gbc);

        JPanel secondPlayerPanel = new JPanel(new GridBagLayout());
        secondPlayerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)
        ));

        JPanel secondPlayerTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondPlayerTimePanel.add(secondPlayerTime);

        JPanel secondPlayerScorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondPlayerScorePanel.add(secondPlayerScoreLabel);
        secondPlayerScorePanel.add(secondPlayerScore);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1/2;
        gbc.weightx = 1;
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        secondPlayerPanel.add(secondPlayerHeader, gbc);

        gbc.gridy = 2;
        gbc.ipady = 0;
        gbc.anchor = GridBagConstraints.WEST;
        secondPlayerPanel.add(secondPlayerTimePanel, gbc);

        gbc.gridy = 3;
        secondPlayerPanel.add(secondPlayerScorePanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 3.5;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane spScrollPane = new JScrollPane(secondPlayerList);
        spScrollPane.setMaximumSize(new Dimension(80, 295));
        spScrollPane.setPreferredSize(new Dimension(80, 295));
        spScrollPane.setMinimumSize(new Dimension(80, 295));
        secondPlayerPanel.add(spScrollPane, gbc);

        Box headerPanel = Box.createVerticalBox();
        JPanel headerPanelRow1 = new JPanel();
        headerPanelRow1.add(gameTime);
        JPanel headerPanelRow2 = new JPanel();
        headerPanelRow2.add(currentPlayerLabel);
        headerPanelRow2.add(currentPlayerName);
        headerPanelRow2.add(Box.createRigidArea(new Dimension(10, 10)));
        headerPanelRow2.add(gameMoveLabel);
        headerPanelRow2.add(gameMove);
        headerPanel.add(headerPanelRow1);
        headerPanel.add(headerPanelRow2);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        JPanel gameFieldPanel = new JPanel(
                new GridLayout(GameField.FIELD_WIDTH, GameField.FIELD_HEIGHT));
        gameFieldLabels = initializeGameField();
        for (int i = 0; i < GameField.FIELD_HEIGHT; i++) {
            for (int j = 0; j < GameField.FIELD_WIDTH; j++) {
                gameFieldPanel.add(gameFieldLabels[i][j]);
            }
        }

        Box bottomButtonsPanel = Box.createHorizontalBox();
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(backBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(undoBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(redoBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(applyBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(surrenderBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.add(saveBtn);
        bottomButtonsPanel.add(Box.createHorizontalGlue());
        bottomButtonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 1;
        gbc.weighty = 5;
        gbc.fill = GridBagConstraints.BOTH;
        page.add(firstPlayerPanel, gbc);

        gbc.gridx = 2;
        page.add(secondPlayerPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.weightx = 2;
        gbc.gridheight = 1;
        page.add(headerPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 4;
        gbc.weightx = 2;
        page.add(gameFieldPanel, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 3;
        page.add(bottomButtonsPanel, gbc);

        updateLanguage();
        setupKeyListener();
    }

    void setModel(GameModel model) {
        if (this.model != null) {
            this.model.removeModelListener(this);
        }
        this.model = model;
        this.model.addModelListener(this);
        syncWithModel();
        updateField();

        // update players names
        System.out.println(model.getPlayers().get(0).getName());
        pageLabels.get(Labels.FP_HEADER).setText(model.getPlayers().get(0).getName());
        pageLabels.get(Labels.SP_HEADER).setText(model.getPlayers().get(1).getName());
    }

    private JLabel[][] initializeGameField() {
        JLabel[][] gameFieldLabels = new JLabel[GameField.FIELD_HEIGHT][GameField.FIELD_WIDTH];
        for (int y = 0; y < GameField.FIELD_HEIGHT; y++) {
            for (int x = 0; x < GameField.FIELD_WIDTH; x++) {
                JLabel fieldLabel = new JLabel(String.valueOf(GameField.EMPTY_CELL_VALUE));
                fieldLabel.setBackground(DEFAULT_CELL_COLOR);
                // every cell draws only left and top border
                if (y == 0) {
                    // top row
                    if (x != 0) {
                        fieldLabel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));
                    }
                }
                else {
                    if (x == 0) {
                        fieldLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
                    }
                    else {
                        fieldLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.BLACK));
                    }
                }
                fieldLabel.setOpaque(true);
                fieldLabel.addMouseListener(getFieldCellMouseListener());
                // setup mouse listener with logic
                gameFieldLabels[y][x] = fieldLabel;
            }
        }
        return gameFieldLabels;
    }

    private void updateField() {
        for (int y = 0; y < GameField.FIELD_HEIGHT; y++) {
            for (int x = 0; x < GameField.FIELD_WIDTH; x++) {
                GameField.Cell cell = model.getGameField().getCell(x, y);
                gameFieldLabels[y][x].setText(String.valueOf(cell.getValue()));
            }
        }
        setCell = null;
        clearWord();
    }

    protected void updateLanguage() {
        setLabelsText();
        setButtonsText();
    }

    private void setButtonsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageButtons.get(Buttons.BACK).setText(resourceBundle.getString("back"));
        pageButtons.get(Buttons.UNDO).setText(resourceBundle.getString("undo"));
        pageButtons.get(Buttons.REDO).setText(resourceBundle.getString("redo"));
        pageButtons.get(Buttons.APPLY).setText(resourceBundle.getString("apply"));
        pageButtons.get(Buttons.SURRENDER).setText(resourceBundle.getString("surrender"));
        pageButtons.get(Buttons.SAVE).setText(resourceBundle.getString("save"));
    }

    private void setLabelsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageLabels.get(Labels.TIME).setText(resourceBundle.getString("current_game_time")+": ");
        pageLabels.get(Labels.CURRENT_PLAYER_LABEL).setText(resourceBundle.getString("current_player")+": ");
        pageLabels.get(Labels.MOVE_NUMBER_LABEL).setText(resourceBundle.getString("current_move_number")+": ");

        pageLabels.get(Labels.FP_HEADER).setText(resourceBundle.getString("name"));
        pageLabels.get(Labels.FP_TIME).setText(resourceBundle.getString("time_left")+": ");
        pageLabels.get(Labels.FP_SCORE_LABEL).setText(resourceBundle.getString("score")+": ");

        pageLabels.get(Labels.SP_HEADER).setText(resourceBundle.getString("name"));
        pageLabels.get(Labels.SP_TIME).setText(resourceBundle.getString("time_left")+": ");
        pageLabels.get(Labels.SP_SCORE_LABEL).setText(resourceBundle.getString("score")+": ");
    }

    private MouseAdapter getFieldCellMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JLabel source  = (JLabel) e.getSource();
                GameField.Cell newSelectedCell = getCellByFieldLabel(e.getSource());
                if (newSelectedCell == null || currentWord.contains(newSelectedCell)
                        || newSelectedCell.equals(selectedCell)) {
                    return;
                }
                // clicked far away
                if (selectedCell != null && !selectedCell.isNear(newSelectedCell)) {
                    clearWord();
                }
                // clicked after empty
                if (selectedCell != null &&
                        getFieldLabelByCell(selectedCell).getText().charAt(0) == EMPTY_CELL_VALUE) {
                    getFieldLabelByCell(selectedCell).setBackground(DEFAULT_CELL_COLOR);
                    clearWord();
                }

                // clicked not empty
                if (getFieldLabelByCell(newSelectedCell).getText().charAt(0) != EMPTY_CELL_VALUE) {
                    currentWord.pushLetter(newSelectedCell);
                // clicked on empty
                } else {
                    // after empty or clicked empty can not be set
                    if (selectedCell != null &&
                            getFieldLabelByCell(selectedCell).getText().charAt(0) == EMPTY_CELL_VALUE
                            || currentWord.contains(setCell)) {
                        clearWord();
                    }
                }
                source.setBackground(SELECTED_CELL_COLOR);
                selectedCell = newSelectedCell;
            }
        };
    }

    private GameField.Cell getCellByFieldLabel(Object label) {
        for (int y = 0; y < GameField.FIELD_HEIGHT; y++) {
            for (int x = 0; x < GameField.FIELD_WIDTH; x++) {
                if (gameFieldLabels[y][x].equals(label)) return model.getGameField().getCell(x, y);
            }
        }
        return null;
    }

    private JLabel getFieldLabelByCell(GameField.Cell cell) {
        return gameFieldLabels[cell.getY()][cell.getX()];
    }

    private void clearWord() {
        GameField.Cell poppedCell = currentWord.popLetter();
        while(poppedCell != null) {
            getFieldLabelByCell(poppedCell).setBackground(DEFAULT_CELL_COLOR);
            poppedCell = currentWord.popLetter();
        }
    }

    private void setupKeyListener() {
        keyListener = new KeyEventDispatcher() {
            private long lastPressProcessed = 0;

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if(System.currentTimeMillis() - lastPressProcessed > 500) {
                    if (selectedCell == null) return false;
                    if (selectedCell.isStandalone()) return false;
                    JLabel selectedLabel = getFieldLabelByCell(selectedCell);

                    if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        selectedLabel.setText(String.valueOf(EMPTY_CELL_VALUE));
                        return false;
                    }
                    if (!currentLanguage.validateLetter(e.getKeyChar()))  return false;

                    if (selectedLabel.getText().charAt(0) == EMPTY_CELL_VALUE
                            || selectedCell == setCell) {
                        if (setCell != null) {
                            selectedLabel.setText(String.valueOf(EMPTY_CELL_VALUE));
                        }
                        if (setCell == selectedCell) {
                            currentWord.popLetter();
                        }
                        selectedLabel.setText(String.valueOf(e.getKeyChar()));
                        currentWord.pushLetter(selectedCell);
                        setCell = selectedCell;
                    }
                }
                lastPressProcessed = System.currentTimeMillis();
                return false;
            }
        };
    }

    private ActionListener getApplyBtnListener() {
        return (ActionEvent e) -> {
            if (setCell == null) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(),
                                            "You must set letter at one field");
                return;
            }
            if (selectedCell != null && !currentWord.isEmpty()) {
                pageButtons.get(Buttons.APPLY).setEnabled(false);
                pageButtons.get(Buttons.UNDO).setEnabled(false);
                pageButtons.get(Buttons.REDO).setEnabled(false);
                char currentLetter = getFieldLabelByCell(setCell).getText().charAt(0);
                model.makeMove(currentLetter, setCell, currentWord.copy());
            } else {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Invalid word");
            }
        };
    }

    private ActionListener getUndoBtnListener() {
        return (ActionEvent e) -> {
            try {
                model.undo();
            } catch (ModelStateException ex) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Cannot undo");
            }
        };
    }

    private ActionListener getRedoBtnListener() {
        return (ActionEvent e) -> {
            try {
                model.redo();
            } catch (ModelStateException ex) {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Cannot undo");
            }
        };
    }

    private ActionListener getSaveBtnListener() {
        return (ActionEvent e) -> {
            try {
                model.pause();
                saveModel();
                model.resume();
            } catch (ModelException ex) {
                logger.error("Cannot save game", ex);
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Cannot save game");
            }
        };
    }

    private ActionListener getSurrenderBtnListener() {
        return (ActionEvent e) -> {
            model.surrender();
        };
    }

    private void saveModel() {
        System.out.println("saving model");
        model.pause();
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./"));
        fc.showSaveDialog(parentView.getFrame());
        File file = fc.getSelectedFile();
        if (file == null) {
            return;
        }
        try {
            model.save(file);
        }
        catch (ModelException exception) {
            WordmasterUtils.showErrorAlert(parentView.getFrame(), "Model save error");
        }
        model.resume();
    }

    private void syncWithModel() {
        // update current player
        currentPlayer = model.getCurrentPlayer();
        pageLabels.get(Labels.CURRENT_PLAYER_NAME).setText(currentPlayer.getName());

        // update current move
        pageLabels.get(Labels.MOVE_NUMBER).setText(String.valueOf(model.getCurrentMove()));

        // update scores
        pageLabels.get(Labels.FP_SCORE).setText(String.valueOf(model.getPlayers().get(0).getScore()));
        pageLabels.get(Labels.SP_SCORE).setText(String.valueOf(model.getPlayers().get(1).getScore()));

        // update field
        updateField();

        // update players lists
        pageWordLists.get(WordLists.FP_LIST).clear();
        pageWordLists.get(WordLists.SP_LIST).clear();
        for (String word : model.getPlayers().get(0).getWords()) {
            pageWordLists.get(WordLists.FP_LIST).addElement(word);
        }
        for (String word : model.getPlayers().get(1).getWords()) {
            pageWordLists.get(WordLists.SP_LIST).addElement(word);
        }

        updateButtonsState();
    }

    private void updateButtonsState() {
        if (!currentPlayer.isComputer()) {
            if (model.isReplay()) {
                pageButtons.get(Buttons.APPLY).setEnabled(false);
                pageButtons.get(Buttons.SAVE).setEnabled(false);
                pageButtons.get(Buttons.SURRENDER).setEnabled(false);
            } else {
                pageButtons.get(Buttons.APPLY).setEnabled(true);
            }
            pageButtons.get(Buttons.UNDO).setEnabled(model.canUndo());
            pageButtons.get(Buttons.REDO).setEnabled(model.canRedo());
        }
    }

    @Override
    public void onMove() {
        try {
            SwingUtilities.invokeAndWait(this::syncWithModel);
        } catch (Exception e) {
            logger.error("Cannot apply move model changes", e);
        }
    }

    @Override
    public void onFinish() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                syncWithModel();
                WordmasterUtils.showGameEndsAlert(parentView.getFrame(), model.getWinners(),
                                                    parentView.getSettings().getLanguage());

                if (WordmasterUtils.askSaveReplay(parentView.getFrame(),
                                                    parentView.getSettings().getLanguage())) {
                    saveModel();
                }
                parentView.showPage(View.Pages.STARTUP);
            });
        } catch (Exception e) {
            logger.error("Cannot apply end game model changes", e);
        }
    }
    @Override
    public void onInvalidMove() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                WordmasterUtils.showErrorAlert(parentView.getFrame(), "Invalid move");
                updateButtonsState();
            });
        } catch (Exception e) {
            logger.error("Cannot apply end game model changes", e);
        }
    }
}
