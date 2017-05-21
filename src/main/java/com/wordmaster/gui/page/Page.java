package com.wordmaster.gui.page;

import com.wordmaster.gui.View;
import com.wordmaster.gui.i18n.Language;

import javax.swing.*;

/**
 * The root of pages hierarchy. Provides common interface
 * for the app view pages
 *
 * @version 1.0
 * @author Mike
 */
public abstract class Page {
    protected View parentView;
    protected JComponent page = null;
    protected Language currentLanguage;

    Page(View parentView) {
        this.parentView = parentView;
        this.currentLanguage = parentView.getSettings().getLanguage();
    }

    /**
     * Returns row Swing JComponent, represents page
     *
     * @return page
     */
    public final JComponent getJComponent() {
        if (page == null) {
            throw new IllegalStateException("Page has not been initialized");
        }
        return page;
    }

    /**
     * Hook to prepare page before showing
     */
    public void preShow() {
        Language newLanguage = parentView.getSettings().getLanguage();
        if (currentLanguage != newLanguage) {
            currentLanguage = newLanguage;
            update();
        }
    }

    /**
     * Hook to gracefully close page
     */
    public void postHide() {}

    /**
     * Forces page to re-render all language-depended labels
     */
    protected abstract void update();

    /**
     * Initializes page and all it's components
     */
    public abstract void initialize();
}
