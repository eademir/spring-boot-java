package com.example.demo.post;

import com.example.demo.jwt.JwtService;
import com.example.demo.util.CustomApiResponse;
import com.example.demo.util.HeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post found"),
            @ApiResponse(responseCode = "404", description = "Post not found")
    })
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
    @Operation(summary = "Delete a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Post not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse> deletePost(@PathVariable UUID id) {
        return postService.deletePost(id);
    }

    /**
     * Updates a post by its ID.
     *
     * @param id          the UUID of the post.
     * @param updatedPost the updated post data.
     * @return a response entity containing the result of the update operation.
     */
    @Operation(summary = "Update a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "400", description = "Post not found")
    })
    @PreAuthorize("hasAuthority('ADMIN') || #updatedPost.author == principal.id")
    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse> updatePost(@PathVariable UUID id, @RequestBody Post updatedPost) {
        return postService.updatePost(id, updatedPost);
    }

    /**
     * Creates a new post.
     *
     * @param post    the post data.
     * @param request the HTTP request containing the authorization header.
     * @return the created post.
     */
    @Operation(summary = "Create a new post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid post data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
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
    @Operation(summary = "Get posts by author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts found"),
            @ApiResponse(responseCode = "404", description = "Posts not found")
    })
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
    @Operation(summary = "Like a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post liked successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot like your own post or post not found")
    })
    @PostMapping("/{id}/like")
    public ResponseEntity<CustomApiResponse> likePost(@PathVariable UUID id, HttpServletRequest request) {
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
    @Operation(summary = "Dislike a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post disliked successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot dislike your own post or post not found")
    })
    @PostMapping("/{id}/dislike")
    public ResponseEntity<CustomApiResponse> dislikePost(@PathVariable UUID id, HttpServletRequest request) {
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
    @Operation(summary = "Get all posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts found"),
            @ApiResponse(responseCode = "404", description = "Posts not found")
    })
    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    /**
     * Retrieves the most popular posts.
     *
     * @return a list of the most popular posts.
     */
    @Operation(summary = "Get popular posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Popular posts found"),
            @ApiResponse(responseCode = "404", description = "Popular posts not found")
    })
    @GetMapping("/popular")
    public List<Post> getPopularPosts() {
        return postService.getPopularPosts();
    }

}
