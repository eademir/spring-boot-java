package com.example.demo.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

// Lombok annotation to generate getter methods for all fields.
@Getter
// Lombok's annotation to generate setter methods for all fields.
@Setter
// Lombok's annotation to generate a no-argument constructor.
@NoArgsConstructor
// Lombok's annotation to generate an all-argument constructor.
@AllArgsConstructor
public class ApiResponse {
    // HTTP status of the response.
    private HttpStatus status;

    // Message associated with the response.
    private String message;
}
