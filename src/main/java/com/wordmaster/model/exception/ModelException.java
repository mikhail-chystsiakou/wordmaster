package com.wordmaster.model.exception;

/**
 * The root of model exceptions hierarchy.
 *
 * @author Mike
 * @version 1.0
 */
public class ModelException extends RuntimeException {
    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
