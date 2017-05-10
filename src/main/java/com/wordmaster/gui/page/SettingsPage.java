package com.wordmaster.gui.page;

import com.wordmaster.gui.*;
import com.wordmaster.gui.audio.AudioPlayer;
import com.wordmaster.gui.custom.ButtonFactory;
import com.wordmaster.gui.custom.LabelFactory;
import com.wordmaster.gui.custom.WordmasterUtils;
import com.wordmaster.gui.i18n.Language;
import com.wordmaster.gui.listeners.MenuItemListener;
import com.wordmaster.gui.listeners.SoundButtonListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsPage extends Page {
    private static Logger logger = LoggerFactory.getLogger(StartupPage.class);
    private Map<Labels, JLabel> pageLabels = new HashMap<>();
    private Map<Buttons, JButton> pageButtons = new HashMap<>();
    private Settings settings = new Settings(parentView.getSettings());
    private JComboBox<Language> languagePicker;
    private JComboBox<Settings.SupportedLAF> lafPicker;

    private enum Buttons {
        APPLY, SAVE, BACK
    }

    private enum Labels {
        HEADER, LANGUAGE, LAF, MUSIC, SOUNDS
    }

    public SettingsPage(View parentView) {
        super(parentView);
    }
    public void initialize() {
        page = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // Creating all page elements
        JLabel header = LabelFactory.getHeaderLabel();
        header.setBackground(Color.BLUE);
        pageLabels.put (Labels.HEADER, header);

        JLabel languageLabel = LabelFactory.getStandardLabel();
        languageLabel.setBackground(Color.BLUE);
        pageLabels.put (Labels.LANGUAGE, languageLabel);

        languagePicker = new JComboBox<>(Language.values());
        languagePicker.addItemListener((ItemEvent e) ->
            settings.setLanguage((Language)e.getItem())
        );
        languagePicker.setSelectedItem(settings.getLanguage());
        languagePicker.setPreferredSize(new Dimension(100, 25));
        languagePicker.setFont(new Font("Arial", Font.PLAIN, 17));

        JLabel lafLabel = LabelFactory.getStandardLabel();
        lafLabel.setBackground(Color.BLACK);
        pageLabels.put (Labels.LAF, lafLabel);

        lafPicker = new JComboBox<>(Settings.SupportedLAF.values());
        lafPicker.setSelectedItem(settings.getLAF());
        lafPicker.addItemListener((ItemEvent e) ->
                settings.setLAF((Settings.SupportedLAF) e.getItem())
        );
        lafPicker.setPreferredSize(new Dimension(100, 25));
        lafPicker.setFont(new Font("Arial", Font.PLAIN, 17));

        JLabel musicLabel = LabelFactory.getStandardLabel();
        musicLabel.setBackground(Color.ORANGE);
        pageLabels.put (Labels.MUSIC, musicLabel);

        JSlider musicVolume = new JSlider(0, 100, settings.getMusicVolume());
        musicVolume.addChangeListener((ChangeEvent e) ->
                settings.setMusicVolume(((JSlider)e.getSource()).getValue())
        );
        musicVolume.setPreferredSize(new Dimension(100, 40));

        JLabel soundsLabel = LabelFactory.getStandardLabel();
        soundsLabel.setBackground(Color.WHITE);
        pageLabels.put (Labels.SOUNDS, soundsLabel);

        JSlider soundsVolume = new JSlider();
        soundsVolume.addChangeListener((ChangeEvent e) ->
                settings.setSoundVolume(((JSlider)e.getSource()).getValue())
        );
        soundsVolume.setPreferredSize(new Dimension(100, 40));

        JButton saveBtn = ButtonFactory.getStandardButton(parentView);
        saveBtn.addActionListener((ActionEvent e) -> {
            parentView.applySettings(settings);   // sync call
            new MenuItemListener(parentView, View.Pages.SETTINGS).actionPerformed(e);
            Settings.saveSettings(Settings.DEFAULT_SETTINGS_FILE, parentView.getSettings(), () ->
                    WordmasterUtils.showErrorAlert(parentView.getFrame(), "Cannot save settings to file")
            );    // async call
            settings = parentView.getSettings();
        });
        saveBtn.addActionListener(
                new SoundButtonListener(parentView, AudioPlayer.SoundType.STANDARD_BUTTON)
        );

        pageButtons.put(Buttons.SAVE, saveBtn);

        JButton applyBtn = ButtonFactory.getStandardButton(parentView);
        applyBtn.addActionListener((ActionEvent e) -> {
            parentView.applySettings(settings);
            new MenuItemListener(parentView, View.Pages.SETTINGS).actionPerformed(e);
            settings = parentView.getSettings();
        });
        applyBtn.addActionListener(
                new SoundButtonListener(parentView, AudioPlayer.SoundType.STANDARD_BUTTON)
        );
        pageButtons.put(Buttons.APPLY, applyBtn);

        JButton backBtn = ButtonFactory.getMenuItemButton(parentView, View.Pages.STARTUP);
        pageButtons.put(Buttons.BACK, backBtn);

        Box buttonsPanel = Box.createVerticalBox();
        saveBtn.setAlignmentX(Box.CENTER_ALIGNMENT);
        applyBtn.setAlignmentX(Box.CENTER_ALIGNMENT);
        backBtn.setAlignmentX(Box.CENTER_ALIGNMENT);
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(applyBtn);
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(saveBtn);
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(backBtn);
        buttonsPanel.add(Box.createVerticalGlue());

        // Layout page elements
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor=GridBagConstraints.CENTER;
        gbc.weighty = 1;
        page.add(header, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        page.add(new JLabel(), gbc);

        gbc.gridwidth = 1;
        gbc.weighty = 1.25;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.ipadx = 10;
        gbc.anchor=GridBagConstraints.EAST;
        page.add(lafLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor=GridBagConstraints.WEST;
        page.add(lafPicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor=GridBagConstraints.EAST;
        page.add(languageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor=GridBagConstraints.WEST;
        page.add(languagePicker, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor=GridBagConstraints.EAST;
        page.add(musicLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor=GridBagConstraints.WEST;
        page.add(musicVolume, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor=GridBagConstraints.EAST;
        page.add(soundsLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor=GridBagConstraints.WEST;
        page.add(soundsVolume, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        page.add(new JLabel(), gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 6;
        gbc.gridheight = 6;
        gbc.anchor=GridBagConstraints.CENTER;
        page.add(buttonsPanel, gbc);

        setLabelsText();
        setButtonsText();
    }

    protected void updateLanguage() {
        setLabelsText();
        setButtonsText();
    }

    public void preShow() {
        super.preShow();
        settings = new Settings(parentView.getSettings());
        languagePicker.setSelectedItem(settings.getLanguage());
        lafPicker.setSelectedItem(settings.getLAF());
    }

    protected void setButtonsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageButtons.get(Buttons.APPLY).setText(resourceBundle.getString("apply"));
        pageButtons.get(Buttons.BACK).setText(resourceBundle.getString("back"));
        pageButtons.get(Buttons.SAVE).setText(resourceBundle.getString("save"));
    }

    protected void setLabelsText() {
        ResourceBundle resourceBundle = currentLanguage.getResourceBundle();
        pageLabels.get(Labels.HEADER).setText(resourceBundle.getString("settings"));
        pageLabels.get(Labels.LANGUAGE).setText(resourceBundle.getString("language")+": ");
        pageLabels.get(Labels.LAF).setText("LAF: ");
        pageLabels.get(Labels.MUSIC).setText(resourceBundle.getString("music")+": ");
        pageLabels.get(Labels.SOUNDS).setText(resourceBundle.getString("sounds")+": ");
    }
}
