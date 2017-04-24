package com.wordmaster.gui.page;

import com.wordmaster.gui.custom.ButtonFactory;
import com.wordmaster.gui.LabelFactory;
import com.wordmaster.gui.View;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class GamePage extends Page {
    private Map<Labels, JLabel> pageLabels = new HashMap<>();
    private Map<Buttons, JButton> pageButtons = new HashMap<>();


    private enum Labels {
        TIME, CURRENT_PLAYER, MOVE_NUMBER,
        FP_HEADER, FP_TIME, FP_SCORE,
        SP_HEADER, SP_TIME, SP_SCORE,
    }
    private enum Buttons {
        BACK, UNDO, REDO, APPLY, SAVE, SURRENDER
    }

    public GamePage(View parentView) {
        super(parentView);
    }

    public void initialize() {
        page = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel firstPlayerHeader = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_HEADER, firstPlayerHeader);

        JLabel secondPlayerHeader = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_HEADER, secondPlayerHeader);

        JList firstPlayerList = new JList();
        JList secondPlayerList = new JList();

        JLabel firstPlayerTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_TIME, firstPlayerTime);

        JLabel secondPlayerTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_TIME, secondPlayerTime);

        JLabel firstPlayerScore = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.FP_SCORE, firstPlayerScore);

        JLabel secondPlayerScore = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.SP_SCORE, secondPlayerScore);

        JLabel gameTime = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.TIME, gameTime);

        JLabel gameMove = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.MOVE_NUMBER, gameMove);

        JLabel currentPlayer = LabelFactory.getStandardLabel();
        pageLabels.put(Labels.CURRENT_PLAYER, currentPlayer);

        JButton backBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.STARTUP);
        pageButtons.put(Buttons.BACK, backBtn);

        JButton undoBtn = ButtonFactory.getStandardButton();
        pageButtons.put(Buttons.UNDO, undoBtn);

        JButton applyBtn = ButtonFactory.getStandardButton();
        pageButtons.put(Buttons.APPLY, applyBtn);

        JButton redoBtn = ButtonFactory.getStandardButton();
        pageButtons.put(Buttons.REDO, redoBtn);

        JButton surrenderBtn = ButtonFactory.getStandardButton();
        pageButtons.put(Buttons.SURRENDER, surrenderBtn);

        JButton saveBtn = ButtonFactory.getStandardButton();
        pageButtons.put(Buttons.SAVE, saveBtn);


        JPanel firstPlayerPanel = new JPanel(new GridBagLayout());
        firstPlayerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel firstPlayerTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstPlayerTimePanel.add(firstPlayerTime);

        JPanel firstPlayerScorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        firstPlayerPanel.add(firstPlayerList, gbc);

        JPanel secondPlayerPanel = new JPanel(new GridBagLayout());
        secondPlayerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel secondPlayerTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondPlayerTimePanel.add(secondPlayerTime);

        JPanel secondPlayerScorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        secondPlayerPanel.add(secondPlayerList, gbc);

        Box headerPanel = Box.createVerticalBox();
        JPanel headerPanelRow1 = new JPanel();
        headerPanelRow1.add(gameTime);
        JPanel headerPanelRow2 = new JPanel();
        headerPanelRow2.add(currentPlayer);
        headerPanelRow2.add(gameMove);
        headerPanel.add(headerPanelRow1);
        headerPanel.add(headerPanelRow2);

        JPanel gameFieldPanel = new JPanel(new GridLayout(5, 5));
        for (int i = 0; i < 25; i++) {
            gameFieldPanel.add(new JButton("2"));
//            JLabel tmp = new JLabel("Ok");
//            tmp.setMinimumSize(new Dimension(10,10));
//            tmp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//            gameFieldPanel.add(tmp);
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
    }
    protected void updateLanguage() {
        setLabelsText();
        setButtonsText();
    }

    protected void setButtonsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageButtons.get(Buttons.BACK).setText(resourceBundle.getString("back"));
        pageButtons.get(Buttons.UNDO).setText(resourceBundle.getString("undo"));
        pageButtons.get(Buttons.REDO).setText(resourceBundle.getString("redo"));
        pageButtons.get(Buttons.APPLY).setText(resourceBundle.getString("apply"));
        pageButtons.get(Buttons.SURRENDER).setText(resourceBundle.getString("surrender"));
        pageButtons.get(Buttons.SAVE).setText(resourceBundle.getString("save"));
    }

    protected void setLabelsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageLabels.get(Labels.TIME).setText(resourceBundle.getString("current_game_time")+": ");
        pageLabels.get(Labels.CURRENT_PLAYER).setText(resourceBundle.getString("current_player")+": ");
        pageLabels.get(Labels.MOVE_NUMBER).setText(resourceBundle.getString("current_move_number")+": ");

        pageLabels.get(Labels.FP_HEADER).setText(resourceBundle.getString("name"));
        pageLabels.get(Labels.FP_TIME).setText(resourceBundle.getString("time_left")+": ");
        pageLabels.get(Labels.FP_SCORE).setText(resourceBundle.getString("score")+": ");

        pageLabels.get(Labels.SP_HEADER).setText(resourceBundle.getString("name"));
        pageLabels.get(Labels.SP_TIME).setText(resourceBundle.getString("time_left")+": ");
        pageLabels.get(Labels.SP_SCORE).setText(resourceBundle.getString("score")+": ");
    }
}
