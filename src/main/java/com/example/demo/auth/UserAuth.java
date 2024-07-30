package com.example.demo.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing user authentication details.
 * Implements UserDetails for Spring Security integration.
 */
@Entity
@Immutable
@Subselect("SELECT id, email, password, role FROM users")   // SQL query to fetch data from the view
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuth implements UserDetails, Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @Setter(AccessLevel.PROTECTED)
    private UUID id;  // Unique identifier for the user

    @NotEmpty
    @Email
    private String email;  // User's email address

    @NotEmpty
    private String password;  // User's password

    private Role role = Role.GUEST;  // User's role, default is GUEST

    /**
     * Returns the authorities granted to the user.
     *
     * @return a collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        role.name()
                )
        );
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username (email) of the user.
     */
    @Override
    public String getUsername() {
        return email;
    }
}
