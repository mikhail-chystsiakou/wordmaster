package com.wordmaster.gui;

import javax.swing.*;
import java.awt.*;

public class LabelFactory {
    public static JLabel getStandardLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 17));
        return label;
    }

    public static JLabel getHeaderLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        return label;
    }
}
