package com.wordmaster.gui.listeners;

import com.wordmaster.gui.GameFrame;
import com.wordmaster.model.Settings;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class SaveSettingsListener extends MenuItemListener {
    private JComboBox<Settings.SupportedLAF> lafjComboBox;
    private JComboBox<Settings.Language> languagejComboBox;

    public SaveSettingsListener(GameFrame targetFrame,
                                JComboBox<Settings.SupportedLAF> lafjComboBox,
                                JComboBox<Settings.Language> languagejComboBox) {
        super(targetFrame, GameFrame.Pane.MAIN);
        this.lafjComboBox = lafjComboBox;
        this.languagejComboBox = languagejComboBox;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Settings settingsToApply = new Settings(
                lafjComboBox.getItemAt(lafjComboBox.getSelectedIndex()),
                languagejComboBox.getItemAt(languagejComboBox.getSelectedIndex())
        );
        super.actionPerformed(event);
        Settings.getInstance().apply(settingsToApply);
        SwingUtilities.updateComponentTreeUI(frame.getFrame());
    }
}
