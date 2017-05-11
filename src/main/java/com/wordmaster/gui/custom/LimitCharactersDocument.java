package com.wordmaster.gui.custom;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Custom text document type that supports limited amount of letters
 *
 * @version 1.0
 * @author Mike
 */
public class LimitCharactersDocument extends PlainDocument {
    private int maxChars;

    /**
     * Creates custom text document type that supports
     * limited amount of letters
     *
     * @param maxChars chars limit
     */
    public LimitCharactersDocument(int maxChars) {
        this.maxChars = maxChars;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if(str != null && (getLength() + str.length() <= maxChars)) {
            super.insertString(offs, str, a);
        }
    }
}
