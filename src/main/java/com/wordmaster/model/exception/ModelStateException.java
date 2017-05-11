package com.wordmaster.model.exception;

/**
 * Represents exception of invalid model state.
 *
 * @author Mike
 * @version 1.0
 */
public class ModelStateException extends ModelException{
    public ModelStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
