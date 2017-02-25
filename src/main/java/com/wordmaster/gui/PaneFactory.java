package com.wordmaster.gui;

import javax.swing.*;
import java.awt.*;

public class PaneFactory {
    public JPanel buildMainPane() {
        JPanel mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));

        mainPane.add(Box.createVerticalGlue());
        JLabel newGameL = new JLabel("Новая игра");
        newGameL.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPane.add(newGameL);

        mainPane.add(Box.createVerticalGlue());
        JLabel settingsL = new JLabel("Настройки");
        settingsL.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPane.add(settingsL);

        mainPane.add(Box.createVerticalGlue());
        JLabel exitL = new JLabel("Выход");
        exitL.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPane.add(exitL);

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
