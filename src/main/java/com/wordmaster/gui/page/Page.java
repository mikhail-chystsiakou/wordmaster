package com.wordmaster.gui.page;

import com.wordmaster.gui.View;
import com.wordmaster.gui.i18n.Language;

import javax.swing.*;

public abstract class Page {
    protected View parentView;
    protected JComponent page = null;
    protected Language currentLanguage;

    Page(View parentView) {
        this.parentView = parentView;
        this.currentLanguage = parentView.getSettings().getLanguage();
    }

    public abstract void initialize();

    public final JComponent getJComponent() {
        if (page == null) {
            throw new IllegalStateException("Page has not been initialized");
        }
        return page;
    }
    public void preShow() {
        Language newLanguage = parentView.getSettings().getLanguage();
        if (currentLanguage != newLanguage) {
            currentLanguage = newLanguage;
            updateLanguage();
        }
    }
    public void postHide() {

    }
    protected abstract void updateLanguage();
}
