package com.wordmaster.gui.listeners;

import com.wordmaster.gui.GameFrame;
import org.apache.logging.log4j.LogManager;

import java.awt.event.ActionEvent;

public class MenuItemListener extends SoundButtonListener {
    private GameFrame.Pane paneToShow;
    private GameFrame frame;

    public MenuItemListener (GameFrame frame, GameFrame.Pane paneToShow) {
        super(SoundType.MENU);
        this.frame = frame;
        this.paneToShow = paneToShow;
    }
    public MenuItemListener () {
        super(SoundType.MENU);

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (frame == null || paneToShow == null) {
            // TODO: cleanup stuff
            LogManager.getLogger(this.getClass()).info("Application closed");
            System.exit(0);
        }
        LogManager.getLogger(this.getClass()).trace("Menu button pressed");
        frame.show(paneToShow);
    }
}
