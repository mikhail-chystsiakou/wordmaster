package com.wordmaster.gui;

import javax.swing.*;
import java.awt.*;

public class ButtonFactory {
    public static JButton getStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        return button;
    }
}
