package com.example.demo.exception;

/**
 * Exception thrown when an error occurs during user creation.
 */
public class UserCreationException extends RuntimeException {

    /**
     * Constructor to create a UserCreationException with a specific message.
     *
     * @param message the detail message.
     */
    public UserCreationException(String message) {
        super(message);
    }
}
