package com.wordmaster.gui.custom;

import com.wordmaster.model.GameField;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

/**
 * Helper class to create labels in centralized fashion
 *
 * @version 1.0
 * @author Mike
 */
public class LabelFactory {
    /**
     * Constructs label with standard app font
     *
     * @return  created label
     */
    public static JLabel getStandardLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 17));
        return label;
    }

    /**
     * Constructs app header label
     *
     * @return  created label
     */
    public static JLabel getHeaderLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        return label;
    }

    /**
     * Constructs the label for game field
     *
     * @return  created label
     */
    public static JLabel getLetterCellLabel() {
        JLabel label = new JLabel(String.valueOf(GameField.EMPTY_CELL_VALUE),
                                                        SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        return label;
    }
}
