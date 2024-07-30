package com.example.demo.post;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a blog post.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id; // Unique identifier for the post.

    @NotEmpty
    private String title; // Title of the post.

    private String content; // Content of the post.

    @NotNull
    private UUID author; // Author of the post.

    private Status status; // Status of the post (e.g., PUBLISHED, DRAFT).

    private boolean visibility; // Visibility flag for the post.

    private boolean commentsEnabled; // Flag indicating if comments are enabled for the post.

    private Long likes; // Number of likes for the post.

    private Long dislikes; // Number of dislikes for the post.

    private Long views; // Number of views for the post.

    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image")
    private List<String> images; // List of image URLs associated with the post.

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Timestamp when the post was created.

    /**
     * Method to set default values before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.dislikes = 0L;
        this.likes = 0L;
        this.views = 0L;
        this.visibility = true;
        this.status = Status.PUBLISHED;
    }
}
