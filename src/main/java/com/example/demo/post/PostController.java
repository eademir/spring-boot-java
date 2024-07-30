package com.example.demo.post;

import com.example.demo.jwt.JwtService;
import com.example.demo.util.ApiResponse;
import com.example.demo.util.HeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller class for handling HTTP requests related to blog posts.
 */
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;

    /**
     * Constructor to initialize PostController with required dependencies.
     *
     * @param postService the service to handle post-related operations.
     * @param jwtService  the service to handle JWT operations.
     */
    public PostController(PostService postService, JwtService jwtService) {
        this.postService = postService;
        this.jwtService = jwtService;
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param id the UUID of the post.
     * @return the post with the specified ID.
     */
    @GetMapping("/{id}")
    public Post getPostById(@PathVariable UUID id) {
        postService.viewPost(id); // Increment the view count for the post.
        return postService.getPostById(id);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param id the UUID of the post.
     * @return a response entity containing the result of the delete operation.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable UUID id) {
        return postService.deletePost(id);
    }

    /**
     * Updates a post by its ID.
     *
     * @param id          the UUID of the post.
     * @param updatedPost the updated post data.
     * @return a response entity containing the result of the update operation.
     */
    @PreAuthorize("hasAuthority('ADMIN') || #updatedPost.author == principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePost(@PathVariable UUID id, @RequestBody Post updatedPost) {
        return postService.updatePost(id, updatedPost);
    }

    /**
     * Creates a new post.
     *
     * @param post    the post data.
     * @param request the HTTP request containing the authorization header.
     * @return the created post.
     */
    @PostMapping
    public Post createPost(@RequestBody Post post, HttpServletRequest request) {
        String authHeader = HeaderUtils.getAuthHeader(request);
        String token = authHeader.substring(7); // Extract the token from the authorization header.
        post.setAuthor(jwtService.extractUserId(token)); // Set the author of the post using the extracted user ID.
        return postService.createPost(post);
    }

    /**
     * Retrieves a post by its author.
     *
     * @param author the UUID of the author.
     * @return the post created by the specified author.
     */
    @GetMapping("/authors/{author}")
    public List<Post> getPostByAuthor(@PathVariable UUID author) {
        return postService.getPostByAuthor(author);
    }

    /**
     * Likes a post by its ID.
     *
     * @param id      the UUID of the post.
     * @param request the HTTP request containing the authorization header.
     * @return a response entity containing the result of the like operation.
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse> likePost(@PathVariable UUID id, HttpServletRequest request) {
        String authHeader = HeaderUtils.getAuthHeader(request);
        String token = authHeader.substring(7); // Extract the token from the authorization header.
        UUID userId = jwtService.extractUserId(token); // Extract the user ID from the token.
        return postService.likePost(id, userId);
    }

    /**
     * Dislikes a post by its ID.
     *
     * @param id      the UUID of the post.
     * @param request the HTTP request containing the authorization header.
     * @return a response entity containing the result of the dislike operation.
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity<ApiResponse> dislikePost(@PathVariable UUID id, HttpServletRequest request) {
        String authHeader = HeaderUtils.getAuthHeader(request);
        String token = authHeader.substring(7); // Extract the token from the authorization header.
        UUID userId = jwtService.extractUserId(token); // Extract the user ID from the token.
        return postService.dislikePost(id, userId);
    }

    /**
     * Retrieves all posts.
     *
     * @return a list of all posts.
     */
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    /**
     * Retrieves the most popular posts.
     *
     * @return a list of the most popular posts.
     */
    @GetMapping("/popular")
    public List<Post> getPopularPosts() {
        return postService.getPopularPosts();
    }

}
