package com.example.demo.repository;

import com.example.demo.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    // Find all posts by a specific author.
    List<Post> findAllByAuthor(UUID author);

    // Find a post by its ID.
    Optional<Post> findById(UUID id);

    // Custom query to find posts ordered by popularity (likes minus dislikes).
    @Query("SELECT p FROM Post p ORDER BY (p.likes - p.dislikes) DESC")
    List<Post> findPostsOrderByPopularity();
}
