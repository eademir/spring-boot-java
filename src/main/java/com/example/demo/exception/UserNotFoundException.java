package com.example.demo.exception;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructor to create a UserNotFoundException with a specific message.
     *
     * @param message the detail message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
