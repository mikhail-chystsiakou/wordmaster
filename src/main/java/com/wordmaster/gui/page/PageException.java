package com.wordmaster.gui.page;

/**
 * Represents the exception on the page
 *
 * @version 1.0
 * @author Mike
 */
public class PageException extends RuntimeException {
    public PageException(String message, Throwable cause) {
        super(message, cause);
    }
}
