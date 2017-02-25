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
    private JFrame gameFrame;

    /**
     * Initializes all panes and shows the frame.
     */
    public void run() {
        gameFrame = new JFrame();
        gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameFrame.setSize(new Dimension(640, 480));


        JPanel contentPane = (JPanel)gameFrame.getContentPane();
        CardLayout cardLayout = new CardLayout();
        contentPane.setLayout(cardLayout);

        PaneFactory pf = new PaneFactory();
        contentPane.add(pf.buildMainPane(), "main");
        contentPane.add(pf.buildNewGamePane(), "new");
        contentPane.add(pf.buildGamePane(), "game");
        contentPane.add(pf.buildSettingsPane(), "settings");

        cardLayout.show(contentPane, "main");

        gameFrame.setVisible(true);
    }
}
