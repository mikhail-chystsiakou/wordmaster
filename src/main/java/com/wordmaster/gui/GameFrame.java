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
    private CardLayout panes = new CardLayout();
    private JFrame gameFrame;
    public final static int FRAME_WIDTH = 640;
    public final static int FRAME_HEIGHT = 480;

    /**
     * Enumerates all main game panes
     */
    public enum Pane {
        MAIN, NEW_GAME, GAME, SETTINGS
    }

    /**
     * Initializes all panes and shows the frame.
     */
    public void run() {
        gameFrame = new JFrame();
        gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameFrame.setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

        gameFrame.getContentPane().setLayout(panes);

        PaneFactory pf = new PaneFactory(this);
        gameFrame.getContentPane().add(pf.buildMainPane(), Pane.MAIN.toString());
        gameFrame.getContentPane().add(pf.buildNewGamePane(), Pane.NEW_GAME.toString());
        gameFrame.getContentPane().add(pf.buildGamePane(), Pane.GAME.toString());
        gameFrame.getContentPane().add(pf.buildSettingsPane(), Pane.SETTINGS.toString());

        panes.show(gameFrame.getContentPane(), Pane.MAIN.toString());
        gameFrame.setVisible(true);
    }

    public void show(Pane paneToShow) {
        panes.show(gameFrame.getContentPane(), Pane.NEW_GAME.toString());
    }
}
