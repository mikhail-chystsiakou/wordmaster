package com.wordmaster.model.exception;

/**
 * Represents exception of unsupported model operation type.
 *
 * @author Mike
 * @version 1.0
 */
public class UnsupportedModelOperationException extends ModelException{
    public UnsupportedModelOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
