package com.wordmaster;

import com.alee.utils.SwingUtils;
import com.wordmaster.gui.GameFrame;
import com.wordmaster.gui.listeners.SoundButtonListener;

/**
 * Main game class
 *
 * @author zoxal
 * @version 1.0.0
 */
public class Main {
    public static void main(String[] args) {
        SwingUtils.invokeLater(() -> {
            GameFrame gf = GameFrame.getInstance();
            gf.initialize();
            gf.run();
        });
    }
}
