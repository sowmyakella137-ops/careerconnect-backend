
package com.careerconnect.backend.controller;

import com.careerconnect.backend.service.SkillMatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/skill-matching")
public class SkillMatchingController {

    private final SkillMatchingService service;

    public SkillMatchingController(SkillMatchingService service) {
        this.service = service;
    }

    @GetMapping("/internships/{internshipId}")
    public ResponseEntity<?> matchInternship(
            @PathVariable Long internshipId,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.matchInternship(authentication.getName(), internshipId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
