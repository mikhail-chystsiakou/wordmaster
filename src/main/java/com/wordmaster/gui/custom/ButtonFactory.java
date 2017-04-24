package com.wordmaster.gui.custom;

import com.wordmaster.gui.View;
import com.wordmaster.gui.listeners.MenuItemListener;

import javax.swing.*;
import java.awt.*;

public class ButtonFactory {
    public static JButton getStandardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        return button;
    }

    public static JButton getStandardButton() {
        return getStandardButton(null);
    }

    public static JButton getMenuItemButton(View view, View.Pages page) {
        JButton btn = ButtonFactory.getStandardButton();
        btn.addActionListener(new MenuItemListener(view, page));
        return btn;
    }

}
