
package com.careerconnect.backend.controller;

import com.careerconnect.backend.entity.PostComment;
import com.careerconnect.backend.service.PostInteractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostInteractionController {

    private final PostInteractionService service;

    public PostInteractionController(PostInteractionService service) {
        this.service = service;
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<?> likePost(
            @PathVariable Long postId,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    Map.of("message",
                            service.likePost(postId, authentication.getName())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

@DeleteMapping("/{postId}/likes")
public ResponseEntity<?> unlikePost(
        @PathVariable Long postId,
        Authentication authentication) {
    try {
        return ResponseEntity.ok(
                Map.of("message",
                        service.unlikePost(
                                postId,
                                authentication.getName())));
    } catch (IllegalArgumentException e) {
        String message = e.getMessage();

        if ("Post not found".equals(message)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", message));
        }

        return ResponseEntity.badRequest()
                .body(Map.of("error", message));
    }
}



    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<?> countLikes(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(
                    Map.of("postId", postId, "likes",
                            service.countLikes(postId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            PostComment comment = service.addComment(
                    postId,
                    authentication.getName(),
                    request.get("content"));
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(service.getComments(postId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
