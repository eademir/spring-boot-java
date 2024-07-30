package com.example.demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to log HTTP requests and responses.
 */
@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    /**
     * Logs the HTTP request and response details.
     *
     * @param servletRequest  the ServletRequest object.
     * @param servletResponse the ServletResponse object.
     * @param filterChain     the FilterChain object.
     * @throws IOException      if an I/O error occurs during the process.
     * @throws ServletException if a servlet error occurs during the process.
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Proceed with the next filter in the chain
        filterChain.doFilter(servletRequest, servletResponse);

        // Log the request URL, HTTP method, and response status
        logger.info("Request URL: {} Method: {} Response: {}", request.getRequestURI(), request.getMethod(),
                response.getStatus());
    }
}
