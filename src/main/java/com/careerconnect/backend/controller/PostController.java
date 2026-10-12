
package com.careerconnect.backend.controller;

import com.careerconnect.backend.entity.Post;
import com.careerconnect.backend.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            Post post = postService.createPost(
                    authentication.getName(),
                    request.get("content"));
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.getAllPosts(authentication.getName()));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(Authentication authentication) {
        return ResponseEntity.ok(
                postService.getMyPosts(authentication.getName()));
    }
}
