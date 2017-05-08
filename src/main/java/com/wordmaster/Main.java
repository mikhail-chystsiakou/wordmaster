package com.wordmaster;

import com.wordmaster.gui.View;
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
