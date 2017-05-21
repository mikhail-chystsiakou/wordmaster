package com.wordmaster.gui.listeners;

import com.wordmaster.gui.View;
import com.wordmaster.gui.audio.AudioPlayer;
import com.wordmaster.gui.audio.SoundType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Produces required sound effect during action
 *
 * @author zoxal
 * @version 1.0
 */
public class SoundButtonListener implements ActionListener {
    private SoundType soundType;
    private View view;

    public SoundButtonListener (View view, SoundType soundType) {
        this.view = view;
        this.soundType = soundType;
    }

    public void actionPerformed(ActionEvent event) {
        view.getPlayer().playSound(soundType);
    }
}
