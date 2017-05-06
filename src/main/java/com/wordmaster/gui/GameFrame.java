package com.wordmaster.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Main game frame. Composites a JFrame and provides proper
 * panes control.
 *
 * @author zoxal
 * @version 1.0
 */
public class GameFrame {
    /*

    private CardLayout panes = new CardLayout();
    private JFrame gameFrame;
    private final static int DEFAULT_FRAME_WIDTH = 640;
    private final static int DEFAULT_FRAME_HEIGHT = DEFAULT_FRAME_WIDTH*3/4;
    private static GameFrame instance = null;

    private GameFrame() {
        gameFrame = new JFrame();
        gameFrame.setTitle("Wordmaster");
        gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameFrame.setSize(new Dimension(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT));
    }

    public static GameFrame getInstance() {
        if (instance == null) {
            instance = new GameFrame();
        }
        return instance;
    }


    public enum Pane {
        MAIN, NEW_GAME, GAME, SETTINGS
    }

    public void setup() {
        gameFrame.getContentPane().setLayout(panes);

        PaneFactory pf = new PaneFactory();

        gameFrame.getContentPane().add(pf.buildStartupPane(), Pane.MAIN.toString());
        panes.show(gameFrame.getContentPane(), Pane.MAIN.toString());

        gameFrame.getContentPane().add(pf.buildNewGamePane(), Pane.NEW_GAME.toString());
        gameFrame.getContentPane().add(pf.buildGamePane(), Pane.GAME.toString());
        gameFrame.getContentPane().add(pf.buildSettingsPane(), Pane.SETTINGS.toString());
    }


    public void run() {

        gameFrame.setVisible(true);
    }

    public void show(Pane paneToShow) {
        panes.show(gameFrame.getContentPane(), paneToShow.toString());
    }

    public JFrame getFrame() {
        return gameFrame;
    }

    */
}
