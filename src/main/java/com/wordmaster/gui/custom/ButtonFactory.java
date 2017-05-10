package com.wordmaster.gui.custom;

import com.wordmaster.gui.View;
import com.wordmaster.gui.audio.AudioPlayer;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SoundButtonListener;

import javax.swing.JButton;
import java.awt.Font;

public class ButtonFactory {
    public static JButton getStandardButton(View parentView, String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.addActionListener(new SoundButtonListener(
                parentView, AudioPlayer.SoundType.STANDARD_BUTTON));
        return button;
    }

    public static JButton getStandardButton(View parentView) {
        return getStandardButton(parentView, "");
    }

    public static JButton getMenuItemButton(View view, View.Pages page) {
        JButton btn = ButtonFactory.getStandardButton(view);
        btn.addActionListener(new MenuItemListener(view, page));
        return btn;
    }

}
