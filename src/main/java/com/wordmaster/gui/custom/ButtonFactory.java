package com.wordmaster.gui.custom;

import com.wordmaster.gui.View;
import com.wordmaster.gui.audio.AudioPlayer;
import com.wordmaster.gui.audio.SoundType;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SoundButtonListener;

import javax.swing.JButton;
import java.awt.Font;

/**
 * Helper class to create buttons in centralized fashion
 *
 * @version 1.0
 * @author Mike
 */
public class ButtonFactory {
    /**
     * Constructs button with standard app font and sound effect
     *
     * @param parentView view-owner of button
     * @param text  text to set on button
     * @return  created button
     */
    public static JButton getStandardButton(View parentView, String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.addActionListener(new SoundButtonListener(
                parentView, SoundType.STANDARD_BUTTON));
        return button;
    }

    /**
     * Constructs button with standard app font and sound effect
     *
     * @param parentView view-owner of button
     * @return  created button
     */
    public static JButton getStandardButton(View parentView) {
        return getStandardButton(parentView, "");
    }

    /**
     * Constructs standard button which should switch view page
     *
     * @param view view-owner of button
     * @param page  page to show
     * @return  created button
     */
    public static JButton getMenuItemButton(View view, View.Pages page) {
        JButton btn = ButtonFactory.getStandardButton(view);
        btn.addActionListener(new MenuItemListener(view, page));
        return btn;
    }

}
