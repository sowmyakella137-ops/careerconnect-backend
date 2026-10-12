
package com.careerconnect.backend.controller;

import com.careerconnect.backend.entity.StudentProfile;
import com.careerconnect.backend.service.StudentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class StudentProfileController {

    private final StudentProfileService service;

    public StudentProfileController(StudentProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdateProfile(
            @RequestBody StudentProfile profile,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                service.saveProfileForUser(profile, email));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.getProfileForUser(authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
