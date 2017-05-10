package com.wordmaster.gui.listeners;

import com.wordmaster.gui.View;
import com.wordmaster.gui.audio.AudioPlayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuItemListener implements ActionListener {
    private View.Pages pageToShow;
    private View view;

    public MenuItemListener (View view, View.Pages pageToShow) {
        this.pageToShow = pageToShow;
        this.view = view;
    }
    public MenuItemListener(View view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (pageToShow == null) {
            view.destroy();
        } else {
            view.showPage(pageToShow);
        }

    }
}
