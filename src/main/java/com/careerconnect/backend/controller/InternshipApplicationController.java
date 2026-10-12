
package com.careerconnect.backend.controller;

import com.careerconnect.backend.entity.InternshipApplication;
import com.careerconnect.backend.service.InternshipApplicationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class InternshipApplicationController {

    private final InternshipApplicationService service;

    public InternshipApplicationController(
            InternshipApplicationService service) {
        this.service = service;
    }

    // Student applies for an internship
    @PostMapping("/{internshipId}")
    public ResponseEntity<?> apply(
            @PathVariable Long internshipId,
            @RequestBody(required = false) Map<String, String> request,
            Authentication authentication) {

        try {
            String resumeUrl = request == null
                    ? null
                    : request.get("resumeUrl");

            InternshipApplication application = service.apply(
                    authentication.getName(),
                    internshipId,
                    resumeUrl);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(application);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    // Student views their own applications
    @GetMapping("/me")
    public ResponseEntity<?> getMyApplications(
            Authentication authentication) {

        try {
            return ResponseEntity.ok(
                    service.getMyApplications(
                            authentication.getName()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    // Company views applications for its own internships
    @GetMapping("/company")
    public ResponseEntity<?> getCompanyApplications(
            Authentication authentication) {

        System.out.println(
                "COMPANY APPLICATIONS ENDPOINT REACHED");

        System.out.println(
                "Authenticated user: "
                        + authentication.getName());

        try {
            return ResponseEntity.ok(
                    service.getCompanyApplications(
                            authentication.getName()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    // Company updates an application's status
    @PatchMapping("/{applicationId}/status")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        try {
            String status = request.get("status");

            if (status == null || status.isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Status is required");
            }

            InternshipApplication updatedApplication =
                    service.updateApplicationStatus(
                            authentication.getName(),
                            applicationId,
                            status);

            return ResponseEntity.ok(updatedApplication);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}
