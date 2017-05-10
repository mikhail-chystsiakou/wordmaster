package com.wordmaster.gui.custom;

import com.wordmaster.model.GameField;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

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

    public static JLabel getLetterCellLabel() {
        JLabel label = new JLabel(String.valueOf(GameField.EMPTY_CELL_VALUE),
                                                        SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        return label;
    }
}
