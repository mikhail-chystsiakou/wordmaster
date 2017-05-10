package com.wordmaster.gui.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    private final static Logger logger = LoggerFactory.getLogger(AudioPlayer.class);
    private final static String BACKGROUND_MUSIC_FILE = "sounds/neverhood.wav";
    private Clip backgroundMusic;
    private int soundsVolume = 100;

    public enum SoundType {
        STANDARD_BUTTON("sounds/bup.wav");

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
                logger.error("Cannot load sound clip {}", clipPath, e);
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

    public AudioPlayer() {
        try {
            URL backgroundMusicURL = this.getClass().getClassLoader().getResource(BACKGROUND_MUSIC_FILE);
            AudioInputStream backgroundMusicStream =
                    AudioSystem.getAudioInputStream(backgroundMusicURL);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(backgroundMusicStream);
        } catch (NullPointerException
                | IOException
                | UnsupportedAudioFileException
                | LineUnavailableException e) {
            logger.error("Cannot load background music", e);
            backgroundMusic = null;
        }
    }

    public void startBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        }
    }

    public void playSound(SoundType soundType) {
        soundType.play(soundsVolume);
    }

    public void setBackgroundMusicVolume(int volumePercent) {
        if (volumePercent > 100) volumePercent = 100;
        if (volumePercent < 0) volumePercent = 0;
        FloatControl gainControl;
        try {
            gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
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
    }

    public void setSoundsVolume(int volumePercent) {
        if (volumePercent > 100) volumePercent = 100;
        if (volumePercent < 0) volumePercent = 0;
        soundsVolume = volumePercent;
    }
}