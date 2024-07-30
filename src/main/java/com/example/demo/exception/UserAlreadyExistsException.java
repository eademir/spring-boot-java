package com.example.demo.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructor to create a UserAlreadyExistsException with a specific message.
     *
     * @param message the detail message.
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
