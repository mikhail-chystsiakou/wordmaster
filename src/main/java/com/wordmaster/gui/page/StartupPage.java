package com.wordmaster.gui.page;

import com.wordmaster.gui.custom.ButtonFactory;
import com.wordmaster.gui.View;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SoundButtonListener;

import javax.swing.Box;
import javax.swing.JButton;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class StartupPage extends Page {
    private Map<Buttons, JButton> pageButtons = new HashMap<>();

    private enum Buttons {
        NEW_GAME, LOAD_GAME, SETTINGS, EXIT
    }

    public StartupPage(View parentView) {
        super(parentView);
    }

    public void initialize() {
        page = Box.createVerticalBox();

        page.add(Box.createVerticalGlue());
        JButton newGameBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.NEW_GAME_SETTINGS);
        newGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        pageButtons.put(Buttons.NEW_GAME, newGameBtn);
        page.add(newGameBtn);

        page.add(Box.createVerticalGlue());
        JButton loadGameBtn = ButtonFactory.getStandardButton();
        loadGameBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loadGameBtn.setEnabled(false);
        pageButtons.put(Buttons.LOAD_GAME, loadGameBtn);
        page.add(loadGameBtn);

        page.add(Box.createVerticalGlue());
        JButton settingsBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.SETTINGS);
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        pageButtons.put(Buttons.SETTINGS, settingsBtn);
        page.add(settingsBtn);

        page.add(Box.createVerticalGlue());
        JButton exitBtn = ButtonFactory.getStandardButton();
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(
                new MenuItemListener(parentView));
        exitBtn.addActionListener(
                new SoundButtonListener(parentView, SoundButtonListener.SoundType.MENU));
        pageButtons.put(Buttons.EXIT, exitBtn);
        page.add(exitBtn);

        page.add(Box.createVerticalGlue());

        setButtonsText();
    }
    protected void updateLanguage() {
        setButtonsText();
    }

    protected void setButtonsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageButtons.get(Buttons.NEW_GAME).setText(resourceBundle.getString("new_game"));
        pageButtons.get(Buttons.LOAD_GAME).setText(resourceBundle.getString("load_game"));
        pageButtons.get(Buttons.SETTINGS).setText(resourceBundle.getString("settings"));
        pageButtons.get(Buttons.EXIT).setText(resourceBundle.getString("exit"));
    }
}
