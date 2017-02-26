package com.wordmaster.gui.listeners;

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

    public enum SoundType {
        MENU, TAKE_LETTER, PLACE_LETTER
    }

    public SoundButtonListener (SoundType soundType) {
        this.soundType = soundType;
    }

    public void actionPerformed(ActionEvent event) {
        // Get sound settings
        // Produce sound effect
    }
}
