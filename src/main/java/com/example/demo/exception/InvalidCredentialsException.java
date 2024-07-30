package com.example.demo.exception;

/**
 * Exception thrown when invalid credentials are provided.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructor to create an InvalidCredentialsException with a specific message.
     *
     * @param message the detail message.
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
