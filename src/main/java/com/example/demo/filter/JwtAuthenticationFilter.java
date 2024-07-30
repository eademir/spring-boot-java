package com.example.demo.filter;

import com.example.demo.jwt.JwtService;
import com.example.demo.util.HeaderUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that handles JWT authentication for incoming HTTP requests.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor to initialize JwtAuthenticationFilter with required services.
     *
     * @param jwtService         the service to handle JWT operations.
     * @param userDetailsService the service to load user details.
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters incoming requests to authenticate users based on JWT tokens.
     *
     * @param request     the HTTP request.
     * @param response    the HTTP response.
     * @param filterChain the filter chain.
     * @throws ServletException if an error occurs during filtering.
     * @throws IOException      if an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header from the request
        String authHeader = HeaderUtils.getAuthHeader(request);

        // If the Authorization header is missing or does not start with "Bearer ", continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token from the Authorization header
        String token = authHeader.substring(7);
        // Extract the username from the JWT token
        String username = jwtService.extractUsername(token);

        // If the username is not null and the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details using the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate the JWT token
            if (jwtService.isValid(token, userDetails)) {
                // Create an authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                // Set additional details for the authentication token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
