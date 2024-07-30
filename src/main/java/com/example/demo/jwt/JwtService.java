package com.example.demo.jwt;

import com.example.demo.auth.UserAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service class for handling JWT operations such as token generation, validation, and extraction of claims.
 */
@Service
public class JwtService {

    private final TokenRepository tokenRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.access-token-expiration}")
    private long accessTokenExpire;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpire;

    /**
     * Constructor to initialize JwtService with required dependencies.
     *
     * @param tokenRepository the repository to handle token persistence.
     */
    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token.
     * @return the username extracted from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Validates the JWT token against the user details.
     *
     * @param token the JWT token.
     * @param user  the user details.
     * @return true if the token is valid, false otherwise.
     */
    public boolean isValid(String token, UserDetails user) {
        String username = extractUsername(token);

        // Check if the token is not logged out and matches the username
        boolean validToken = tokenRepository
                .findByAccessToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && validToken;
    }

    /**
     * Validates the refresh token against the user details.
     *
     * @param token the refresh token.
     * @param user  the user details.
     * @return true if the refresh token is valid, false otherwise.
     */
    public boolean isValidRefreshToken(String token, UserAuth user) {
        String username = extractUsername(token);

        // Check if the refresh token is not logged out and matches the username
        boolean validRefreshToken = tokenRepository
                .findByRefreshToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        return (username.equals(user.getUsername())) && !isTokenExpired(token) && validRefreshToken;
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token.
     * @return true if the token is expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token.
     * @return the expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token    the JWT token.
     * @param resolver the function to resolve the claim.
     * @param <T>      the type of the claim.
     * @return the extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Extracts the user ID from the JWT token.
     *
     * @param token the JWT token.
     * @return the user ID extracted from the token.
     */
    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString(claims.get("uuid", String.class));
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token.
     * @return the claims extracted from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates an access token for the given user.
     *
     * @param user the user details.
     * @return the generated access token.
     */
    public String generateAccessToken(UserAuth user) {
        return generateToken(user, accessTokenExpire);
    }

    /**
     * Generates a refresh token for the given user.
     *
     * @param user the user details.
     * @return the generated refresh token.
     */
    public String generateRefreshToken(UserAuth user) {
        return generateToken(user, refreshTokenExpire);
    }

    /**
     * Generates a JWT token for the given user with a specified expiration time.
     *
     * @param user       the user details.
     * @param expireTime the expiration time in milliseconds.
     * @return the generated JWT token.
     */
    private String generateToken(UserAuth user, long expireTime) {
        return Jwts
                .builder()
                .setSubject(user.getEmail())
                .claim("uuid", user.getId().toString())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigninKey())
                .compact();
    }

    /**
     * Retrieves the signing key for JWT token generation and validation.
     *
     * @return the signing key.
     */
    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
