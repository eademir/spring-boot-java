package com.example.demo.util;

import jakarta.servlet.http.Cookie;

public class CookieUtils {

    /**
     * Creates a secure HTTP-only cookie.
     *
     * @param name  the name of the cookie
     * @param value the value of the cookie
     * @return a configured Cookie object
     */
    public static Cookie createSecureCookie(String name, String value) {
        // Create a new cookie with the specified name and value.
        Cookie cookie = new Cookie(name, value);

        // Set the cookie to be HTTP-only, which helps mitigate certain types of attacks.
        cookie.setHttpOnly(true);

        // Set the maximum age of the cookie to 7 days (in seconds).
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        // Set the path of the cookie to the root of the application.
        cookie.setPath("/");

        // Set the cookie to be secure, meaning it will only be sent over HTTPS.
        cookie.setSecure(true);

        // Return the configured cookie.
        return cookie;
    }
}
