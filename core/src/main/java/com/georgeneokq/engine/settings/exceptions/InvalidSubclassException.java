package com.georgeneokq.engine.settings.exceptions;

/*
 * To be thrown when a subclass' structure is incorrect.
 */
public class InvalidSubclassException extends RuntimeException {
    public InvalidSubclassException(String message) {
        super(message);
    }
}
