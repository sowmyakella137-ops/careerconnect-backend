
package com.careerconnect.backend.controller;

import com.careerconnect.backend.entity.Internship;
import com.careerconnect.backend.service.InternshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internships")
public class InternshipController {

    private final InternshipService service;

    public InternshipController(InternshipService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllInternships() {
        return ResponseEntity.ok(service.getAllInternships());
    }

    @PostMapping
    public ResponseEntity<?> createInternship(
            @RequestBody Internship request,
            Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.createInternship(
                            authentication.getName(), request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-company")
    public ResponseEntity<?> getCompanyInternships(
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.getCompanyInternships(authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }
    @GetMapping("/{id}")
public ResponseEntity<?> getInternshipById(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(
                service.getInternshipById(id)
        );
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}
}
