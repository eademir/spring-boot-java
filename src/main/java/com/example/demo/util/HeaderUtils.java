package com.example.demo.util;

import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtils {

    /**
     * Retrieves the value of the "Authorization" header from the given HTTP request.
     *
     * @param request the HttpServletRequest object containing the client request
     * @return the value of the "Authorization" header, or null if the header is not present
     */
    public static String getAuthHeader(HttpServletRequest request) {
        // Get the value of the "Authorization" header from the request.
        return request.getHeader("Authorization");
    }
}
