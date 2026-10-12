
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.Post;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.PostLikeRepository;
import com.careerconnect.backend.repository.PostRepository;
import com.careerconnect.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository likeRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository, PostLikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    public Post createPost(String email, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "Post content is required");
        }

        if (content.length() > 2000) {
            throw new IllegalArgumentException(
                    "Post cannot exceed 2000 characters");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        Post post = new Post();
        post.setUserId(user.getId());
        post.setContent(content.trim());

        return postRepository.save(post);
    }

    public List<Map<String, Object>> getAllPosts(String email) {
    User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

    return postRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(post -> toPostResponse(post, currentUser.getId()))
            .toList();
}

    public List<Map<String, Object>> getMyPosts(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return postRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(post -> toPostResponse(post, user.getId()))                .toList();
    }

private Map<String, Object> toPostResponse(
        Post post, Long currentUserId) {

    User author = userRepository.findById(post.getUserId())
            .orElse(null);

    Map<String, Object> response = new java.util.LinkedHashMap<>();

    response.put("id", post.getId());
    response.put("content", post.getContent());
    response.put("userId", post.getUserId());
    response.put("createdAt", post.getCreatedAt());

    response.put("authorName",
            author != null ? author.getName() : "MissingUser");

    response.put("authorRole",
            author != null && author.getRole() != null
                    ? author.getRole().toString()
                    : "STUDENT");

    response.put("likesCount", likeRepository.countByPostId(post.getId()));

    response.put("liked",
            likeRepository.findByPostIdAndUserId(
                    post.getId(), currentUserId).isPresent());

    return response;
}
}
