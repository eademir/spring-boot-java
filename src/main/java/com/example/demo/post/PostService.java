package com.example.demo.post;

import com.example.demo.util.CustomApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service class for handling operations related to blog posts.
 */
@Service
public class PostService {
    private final PostRepository postRepository;

    /**
     * Constructor to initialize PostService with required dependencies.
     *
     * @param postRepository the repository to handle post persistence.
     */
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param id the UUID of the post.
     * @return the post with the specified ID, or null if not found.
     */
    public Post getPostById(UUID id) {
        return postRepository.findById(id).orElse(null);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param id the UUID of the post.
     * @return a response entity containing the result of the delete operation.
     */
    public ResponseEntity<CustomApiResponse> deletePost(UUID id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomApiResponse(HttpStatus.OK, "Post deleted successfully."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST, "Post not found."));
    }

    /**
     * Updates a post by its ID.
     *
     * @param id          the UUID of the post.
     * @param updatedPost the updated post data.
     * @return a response entity containing the result of the update operation.
     */
    public ResponseEntity<CustomApiResponse> updatePost(UUID id, Post updatedPost) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost != null) {
            ResponseEntity<CustomApiResponse> response = validateUnmodifiableFields(existingPost, updatedPost);
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setContent(updatedPost.getContent());
            existingPost.setStatus(updatedPost.getStatus());
            existingPost.setVisibility(updatedPost.isVisibility());
            existingPost.setCommentsEnabled(updatedPost.isCommentsEnabled());
            existingPost.setImages(updatedPost.getImages());
            postRepository.save(existingPost);
            return response;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST, "Post not found."));
    }

    /**
     * Validates that certain fields of the post cannot be modified.
     *
     * @param existingPost the existing post data.
     * @param updatedPost  the updated post data.
     * @return a response entity indicating whether the validation passed or failed.
     */
    private ResponseEntity<CustomApiResponse> validateUnmodifiableFields(Post existingPost, Post updatedPost) {
        if (!existingPost.getLikes().equals(updatedPost.getLikes()) ||
                !existingPost.getDislikes().equals(updatedPost.getDislikes()) ||
                !existingPost.getViews().equals(updatedPost.getViews())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST, "Cannot" +
                    " update likes, dislikes, or views."));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new CustomApiResponse(HttpStatus.OK, "Post updated successfully."));
    }

    /**
     * Creates a new post.
     *
     * @param post the post data.
     * @return the created post.
     */
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    /**
     * Retrieves a post by its author.
     *
     * @param author the UUID of the author.
     * @return the post created by the specified author.
     */
    public List<Post> getPostByAuthor(UUID author) {
        return postRepository.findAllByAuthor(author);
    }

    /**
     * Likes a post by its ID.
     *
     * @param id     the UUID of the post.
     * @param userId the UUID of the user liking the post.
     * @return a response entity containing the result of the like operation.
     */
    public ResponseEntity<CustomApiResponse> likePost(UUID id, UUID userId) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null && !post.getAuthor().equals(userId)) {
            post.setLikes(post.getLikes() + 1);
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomApiResponse(HttpStatus.OK, "Post liked successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST, "Cannot like your own post or post not found."));
        }
    }

    /**
     * Dislikes a post by its ID.
     *
     * @param id     the UUID of the post.
     * @param userId the UUID of the user disliking the post.
     * @return a response entity containing the result of the dislike operation.
     */
    public ResponseEntity<CustomApiResponse> dislikePost(UUID id, UUID userId) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null && !post.getAuthor().equals(userId)) {
            post.setDislikes(post.getDislikes() + 1);
            postRepository.save(post);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomApiResponse(HttpStatus.OK, "Post disliked successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CustomApiResponse(HttpStatus.BAD_REQUEST, "Cannot dislike your own post or post not found."));
        }
    }

    /**
     * Increments the view count of a post by its ID.
     *
     * @param id the UUID of the post.
     */
    public void viewPost(UUID id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            post.setViews(post.getViews() + 1);
            postRepository.save(post);
        }
    }

    /**
     * Retrieves all posts.
     *
     * @return a list of all posts.
     */
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    /**
     * Retrieves the most popular posts.
     *
     * @return a list of the most popular posts.
     */
    public List<Post> getPopularPosts() {
        return postRepository.findPostsOrderByPopularity();
    }

}
