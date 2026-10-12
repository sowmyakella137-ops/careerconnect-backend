
package com.careerconnect.backend.controller;

import com.careerconnect.backend.service.InternshipRecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class InternshipRecommendationController {

    private final InternshipRecommendationService recommendationService;

    public InternshipRecommendationController(
            InternshipRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/internships")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            Authentication authentication) {

        Map<String, Object> result =
                recommendationService.getRecommendations(
                        authentication.getName());

        return ResponseEntity.ok(result);
    }
}
