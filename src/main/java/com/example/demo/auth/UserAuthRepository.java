package com.example.demo.auth;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {
    // Find a user by their email address.
    UserAuth findByEmail(String email);
}
