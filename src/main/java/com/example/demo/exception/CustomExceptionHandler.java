package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for handling custom exceptions.
 */
@ControllerAdvice
public class CustomExceptionHandler {

    /**
     * Handles UserCreationException and returns a custom error response.
     *
     * @param ex the UserCreationException thrown.
     * @return ResponseEntity containing the error response and HTTP status.
     */
    @ExceptionHandler(UserCreationException.class)
    public ResponseEntity<Object> handleUserCreationException(UserCreationException ex) {
        // Return a custom error response with HTTP status BAD_REQUEST
        return new ResponseEntity<>(new ErrorResponse("An error occurred during user creation."),
                HttpStatus.BAD_REQUEST);
    }

    /**
     * Record to represent the error response structure.
     */
    private record ErrorResponse(String error) {
        // The error message
    }
}
