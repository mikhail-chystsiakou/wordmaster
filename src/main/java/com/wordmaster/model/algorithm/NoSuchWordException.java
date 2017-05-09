package com.wordmaster.model.algorithm;

public class NoSuchWordException extends VocabularyException {
    public NoSuchWordException(String message, Throwable cause) {
        super(message, cause);
    }
}
