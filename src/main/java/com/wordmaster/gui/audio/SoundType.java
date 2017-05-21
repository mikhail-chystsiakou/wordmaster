package com.wordmaster.gui.audio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Represents on game sound that can be played.
 * Contains information about where it's situated.
 *
 * @author Mike
 * @version 1.1
 */
public enum SoundType {
    STANDARD_BUTTON("sounds/bup.wav");

    private final static Logger logger = LoggerFactory.getLogger(SoundType.class);
    private Clip clip;
    SoundType(String clipPath) {
        try {
            URL soundURL = this.getClass().getClassLoader().getResource(clipPath);
            AudioInputStream backgroundMusicStream =
                    AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(backgroundMusicStream);
        } catch (NullPointerException
                | IOException
                | UnsupportedAudioFileException
                | LineUnavailableException e) {
            LoggerFactory.getLogger(SoundType.class)
                    .error("Cannot load sound clip {}", clipPath, e);
            clip = null;
        }
    }

    void play(int volumePercent) {
        if (clip == null) return;
        FloatControl gainControl;
        try {
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (IllegalArgumentException e) {
            logger.error("Cannot gain sound control", e);
            return;
        }

        float volumeMaximum = gainControl.getMaximum();
        float volumeMinimum = gainControl.getMinimum();
        float newVolume = (float) (20.d * Math
                .log(volumePercent == 0 ? Double.MIN_VALUE
                        : ((double) volumePercent / 100.d)) / Math.log(10));
        if (newVolume > volumeMaximum) newVolume = volumeMaximum;
        if (newVolume < volumeMinimum) newVolume = volumeMinimum;
        gainControl.setValue(newVolume);

        clip.setFramePosition(0);
        clip.start();
    }
}
