package com.example.demo.user;

import com.example.demo.auth.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@DynamicUpdate
public class User implements Serializable {
    // Primary key for the User entity, generated as a UUID.
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    // User's name, must not be empty.
    @NotEmpty
    private String name;

    // User's email, must be unique and not empty.
    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;

    // User's password, must not be empty.
    @NotEmpty
    private String password;

    // Timestamp for when the user was created, not updatable.
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Timestamp for when the user was last updated.
    private LocalDateTime updatedAt;

    // Timestamp for when the user last logged in.
    private LocalDateTime lastLogin;

    // Status of the user, defaults to ACTIVE.
    private Status status = Status.ACTIVE;

    // Role of the user, defaults to GUEST.
    private Role role = Role.GUEST;

    // Method to set the createdAt timestamp before persisting.
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Method to set the updatedAt timestamp before updating.
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
