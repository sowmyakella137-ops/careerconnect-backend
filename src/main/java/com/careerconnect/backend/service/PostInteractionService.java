
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.*;
import com.careerconnect.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostInteractionService {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostCommentRepository commentRepository;
    private final UserRepository userRepository;

    public PostInteractionService(
            PostRepository postRepository,
            PostLikeRepository likeRepository,
            PostCommentRepository commentRepository,
            UserRepository userRepository) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public String likePost(Long postId, String email) {
        var post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Post not found"));

        var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (likeRepository.findByPostIdAndUserId(postId, user.getId()).isPresent()) {
            return "You have already liked this post";
        }

        PostLike like = new PostLike();
        like.setPostId(post.getId());
        like.setUserId(user.getId());
        likeRepository.save(like);

        return "Post liked successfully";
    }
    
@Transactional
public String unlikePost(Long postId, String email) {
    // Verify that the post exists
    if (!postRepository.existsById(postId)) {
        throw new IllegalArgumentException("Post not found");
    }

    // Find the current user
    var user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

    // Find this user's like on this post
    PostLike like = likeRepository
            .findByPostIdAndUserId(postId, user.getId())
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "You have not liked this post"));

    // Remove only this user's like
    likeRepository.delete(like);

    return "Post unliked successfully";
}


    public long countLikes(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("Post not found");
        }
        return likeRepository.countByPostId(postId);
    }

    public PostComment addComment(Long postId, String email, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("Comment is too long");
        }

        var post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Post not found"));

        var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        PostComment comment = new PostComment();
        comment.setPostId(post.getId());
        comment.setUserId(user.getId());
        comment.setContent(content.trim());

        return commentRepository.save(comment);
    }

    public List<PostComment> getComments(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("Post not found");
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }
}
