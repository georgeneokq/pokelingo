package com.georgeneokq.engine.settings.exceptions;

/*
 * This exception is to be thrown when the
 * settings config file's structure is incorrect.
 */
public class InvalidFormatException extends RuntimeException {
    public InvalidFormatException(String message) {
        super(message);
    }
}
