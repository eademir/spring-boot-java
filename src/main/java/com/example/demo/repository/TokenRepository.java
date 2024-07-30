package com.example.demo.repository;

import com.example.demo.jwt.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    // Custom query to find all access tokens for a user that are not logged out.
    @Query("""
            select t from Token t inner join User u on t.user.id = u.id
            where t.user.id = :userId and t.loggedOut = false
            """)
    List<Token> findAllAccessTokensByUser(UUID userId);

    // Find a token by its access token string.
    Optional<Token> findByAccessToken(String token);

    // Find a token by its refresh token string.
    Optional<Token> findByRefreshToken(String token);
}
