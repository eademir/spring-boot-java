package com.example.demo.jwt;

public record AuthenticationResponse(
        // The access token for the authenticated user.
        String accessToken,

        // The refresh token for the authenticated user.
        String refreshToken,

        // A message related to the authentication response.
        String message) {
    // The access token is used to authenticate the user for subsequent requests.
    // The refresh token is used to obtain a new access token when the current one expires.
    // The message provides additional information about the authentication response.
}
