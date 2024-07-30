package com.example.demo.jwt;

import com.example.demo.auth.UserAuth;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a token used for authentication.
 */
@Entity
@Table(name = "token")
@Getter
@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Unique identifier for the token.

    private String accessToken; // The access token string.

    private String refreshToken; // The refresh token string.

    private boolean loggedOut; // Flag indicating if the token is logged out.

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAuth user; // The user associated with this token.
}
