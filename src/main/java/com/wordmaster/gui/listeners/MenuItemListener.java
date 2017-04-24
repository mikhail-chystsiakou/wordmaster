package com.wordmaster.gui.listeners;

import com.wordmaster.gui.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuItemListener extends SoundButtonListener {
    private View.Pages pageToShow;
    private View view;

    public MenuItemListener (View view, View.Pages pageToShow) {
        super(view, SoundType.MENU);
        this.pageToShow = pageToShow;
        this.view = view;
    }
    public MenuItemListener(View view) {
        super(view, SoundType.MENU);
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        if (pageToShow == null) {
            view.destroy();
        } else {
            view.showPage(pageToShow);
        }

    }
}
