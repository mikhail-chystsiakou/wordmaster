package com.wordmaster.gui.listeners;

import com.wordmaster.gui.GameFrame;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class SaveSettingsListener extends MenuItemListener {
    public SaveSettingsListener(GameFrame targetFrame) {
        super(targetFrame, GameFrame.Pane.MAIN);
    }
    @Override
    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        // TODO: save settings
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (   ClassNotFoundException |
                    InstantiationException |
                    IllegalAccessException |
                    UnsupportedLookAndFeelException e) {
            LogManager.getLogger(this.getClass()).error("Cannot set new look&feel");
            //TODO: create error popup
            return;
        }
        SwingUtilities.updateComponentTreeUI(frame.getFrame());
    }
}
