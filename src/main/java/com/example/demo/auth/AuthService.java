package com.example.demo.auth;

import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.jwt.AuthenticationResponse;
import com.example.demo.jwt.JwtService;
import com.example.demo.jwt.Token;
import com.example.demo.repository.TokenRepository;
import com.example.demo.repository.UserAuthRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.user.User;
import com.example.demo.util.CookieUtils;
import com.example.demo.util.HeaderUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    // Constructor to inject dependencies
    public AuthService(UserAuthRepository userAuthRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, TokenRepository tokenRepository, UserRepository userRepository) {
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Authenticate a user and generate JWT tokens.
     *
     * @param request  User credentials.
     * @param response HttpServletResponse to set cookies.
     * @return AuthenticationResponse containing access and refresh tokens.
     */
    public AuthenticationResponse authenticate(UserAuth request, HttpServletResponse response) {
        Optional<UserAuth> userOptional = Optional.ofNullable(userAuthRepository.findByEmail(request.getEmail()));
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        UserAuth user = userOptional.get();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User userEntity = userRepository.findByEmail(request.getEmail());
        userEntity.setLastLogin(LocalDateTime.now());
        userRepository.save(userEntity);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(accessToken, refreshToken, user);

        // Set cookies for access and refresh tokens
        // Use the utility method to create cookies
        Cookie refreshTokenCookie = CookieUtils.createSecureCookie("refreshToken", refreshToken);
        Cookie accessTokenCookie = CookieUtils.createSecureCookie("accessToken", accessToken);

        response.addCookie(refreshTokenCookie);
        response.addCookie(accessTokenCookie);

        return new AuthenticationResponse(accessToken, refreshToken, "User login was successful");
    }

    /**
     * Revoke all tokens for a given user.
     *
     * @param user User whose tokens need to be revoked.
     */
    private void revokeAllTokenByUser(UserAuth user) {
        List<Token> validTokens = tokenRepository.findAllAccessTokensByUser(user.getId());
        if (validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t -> {
            t.setLoggedOut(true);
        });

        tokenRepository.saveAll(validTokens);
    }

    /**
     * Register a new user and generate JWT tokens.
     *
     * @param request User details for registration.
     * @return AuthenticationResponse containing access and refresh tokens.
     */
    public AuthenticationResponse register(User request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        Optional<UserAuth> userOptional = Optional.ofNullable(userAuthRepository.findByEmail(request.getEmail()));
        UserAuth userAuth = userOptional.get();

        String accessToken = jwtService.generateAccessToken(userAuth);
        String refreshToken = jwtService.generateRefreshToken(userAuth);

        saveUserToken(accessToken, refreshToken, userAuth);

        return new AuthenticationResponse(accessToken, refreshToken, "User registration was successful");
    }

    /**
     * Save user tokens to the repository.
     *
     * @param accessToken  Access token.
     * @param refreshToken Refresh token.
     * @param user         User to whom the tokens belong.
     */
    private void saveUserToken(String accessToken, String refreshToken, UserAuth user) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);
        tokenRepository.save(token);
    }

    /**
     * Refresh the authentication token.
     *
     * @param request  HttpServletRequest to get the current token.
     * @param response HttpServletResponse to set the new token.
     * @return ResponseEntity with AuthenticationResponse and appropriate HTTP status.
     */
    public ResponseEntity<AuthenticationResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        // Extract the token from authorization header
        String authHeader = HeaderUtils.getAuthHeader(request);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse(null, null, "Unauthorized"));
        }

        String token = authHeader.substring(7);

        // Extract username from token
        String username = jwtService.extractUsername(token);

        // Check if the user exists in the database
        UserAuth user = userAuthRepository.findByEmail(username);

        // Check if the token is valid
        if (jwtService.isValidRefreshToken(token, user)) {
            // Generate new access and refresh tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllTokenByUser(user);
            saveUserToken(accessToken, refreshToken, user);

            return ResponseEntity.ok(new AuthenticationResponse(accessToken, refreshToken, "Token refreshed successfully"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthenticationResponse(null, null, "Unauthorized"));
    }
}
