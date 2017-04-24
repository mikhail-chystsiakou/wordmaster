package com.wordmaster.gui.listeners;

import com.wordmaster.gui.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Produces required sound effect before action
 *
 * @author zoxal
 * @version 1.0
 */
public class SoundButtonListener implements ActionListener {
    private SoundType soundType;
    private View view;

    public enum SoundType {
        MENU, TAKE_LETTER, PLACE_LETTER
    }

    public SoundButtonListener (View view, SoundType soundType) {
        this.view = view;
        this.soundType = soundType;
    }

    public void actionPerformed(ActionEvent event) {
        // Get sound settings from view.getSettings()
        // Produce sound effect
    }
}
