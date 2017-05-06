package com.wordmaster;

import com.wordmaster.gui.GameFrame;
import com.wordmaster.gui.View;
import com.wordmaster.gui.listeners.SoundButtonListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main game class
 *
 * @author zoxal
 * @version 1.0.0
 */
public class Main {
    public static void main(String[] args) {
        LoggerFactory.getLogger(Main.class).debug("hello");
        View view = new View();
        view.show();
    }
}
