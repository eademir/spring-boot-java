package com.example.demo.config;

import com.example.demo.jwt.Token;
import com.example.demo.repository.TokenRepository;
import com.example.demo.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * Custom logout handler to manage user logout and token invalidation.
 */
@Configuration
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    /**
     * Constructor to inject dependencies.
     *
     * @param tokenRepository Repository to manage tokens.
     */
    public CustomLogoutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Handles the logout process, invalidating the user's tokens and clearing cookies.
     *
     * @param request        HttpServletRequest to get the authorization header.
     * @param response       HttpServletResponse to clear cookies.
     * @param authentication Authentication object (not used in this implementation).
     */
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        // Extract the token from the authorization header
        String authHeader = request.getHeader("Authorization");

        // If the authorization header is missing or does not start with "Bearer ", return early
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        // Extract the token from the header
        String token = authHeader.substring(7);
        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);

        // If a valid token is found, mark it as logged out and save it
        if (storedToken != null) {
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        }

        // Create and configure cookies to clear the access and refresh tokens
        Cookie refreshTokenCookie = CookieUtils.createSecureCookie("refreshToken", "");
        refreshTokenCookie.setMaxAge(0);

        Cookie accessTokenCookie = CookieUtils.createSecureCookie("accessToken", "");
        accessTokenCookie.setMaxAge(0);

        // Add the cookies to the response to clear them on the client side
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
