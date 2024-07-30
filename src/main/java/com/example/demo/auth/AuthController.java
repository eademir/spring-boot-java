package com.example.demo.auth;

import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.jwt.AuthenticationResponse;
import com.example.demo.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/v1/auth")
@RestController
public class AuthController {
    private final AuthService authService;

    // Constructor to inject AuthService dependency
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to handle user login.
     *
     * @param payload  User credentials.
     * @param response HttpServletResponse to set cookies or headers.
     * @return ResponseEntity with AuthenticationResponse and appropriate HTTP status.
     */
    @PostMapping
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserAuth payload, HttpServletResponse response) {
        try {
            AuthenticationResponse authResponse = authService.authenticate(payload, response);
            return ResponseEntity.ok(authResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthenticationResponse(null, null, e.getMessage()));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse(null, null, e.getMessage()));
        }
    }

    /**
     * Endpoint to handle user registration.
     *
     * @param user User details for registration.
     * @return ResponseEntity with AuthenticationResponse and appropriate HTTP status.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User user) {
        try {
            AuthenticationResponse response = authService.register(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new AuthenticationResponse(null, null, e.getMessage()));
        }
    }

    /**
     * Endpoint to refresh authentication token.
     *
     * @param request  HttpServletRequest to get the current token.
     * @param response HttpServletResponse to set the new token.
     * @return ResponseEntity with AuthenticationResponse and appropriate HTTP status.
     */
    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return authService.refreshToken(request, response);
    }
}
