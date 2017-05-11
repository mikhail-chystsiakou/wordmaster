package com.wordmaster.model.exception;

/**
 * Represents exception during model initialization.
 *
 * @author Mike
 * @version 1.0
 */
public class ModelInitializeException extends ModelException {
    public ModelInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
