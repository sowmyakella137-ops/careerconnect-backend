
package com.careerconnect.backend.controller;

import com.careerconnect.backend.service.StudentConnectionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connections")
public class StudentConnectionController {

    private final StudentConnectionService service;

    public StudentConnectionController(
            StudentConnectionService service) {
        this.service = service;
    }

    // Discover students. Optional query searches names,
    // skills, interests and education.
    @GetMapping("/students")
    public ResponseEntity<?> discoverStudents(
            @RequestParam(required = false) String query,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.discoverStudents(
                            authentication.getName(), query));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // View another student's public profile.
    @GetMapping("/students/{studentId}")
    public ResponseEntity<?> viewStudentProfile(
            @PathVariable Long studentId,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.viewStudentProfile(
                            authentication.getName(), studentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Send a connection request.
    @PostMapping("/students/{studentId}/request")
    public ResponseEntity<?> sendRequest(
            @PathVariable Long studentId,
            Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.sendRequest(
                            authentication.getName(), studentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // View requests received by the logged-in student.
    @GetMapping("/requests/received")
    public ResponseEntity<?> receivedRequests(
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.getReceivedRequests(
                            authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // View requests sent by the logged-in student.
    @GetMapping("/requests/sent")
    public ResponseEntity<?> sentRequests(
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.getSentRequests(
                            authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Accept a connection request.
    @PatchMapping("/requests/{connectionId}/accept")
    public ResponseEntity<?> acceptRequest(
            @PathVariable Long connectionId,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.respondToRequest(
                            authentication.getName(),
                            connectionId, true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Reject a connection request.
    @PatchMapping("/requests/{connectionId}/reject")
    public ResponseEntity<?> rejectRequest(
            @PathVariable Long connectionId,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.respondToRequest(
                            authentication.getName(),
                            connectionId, false));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // List accepted connections.
    @GetMapping
    public ResponseEntity<?> getConnections(
            Authentication authentication) {
        try {
            return ResponseEntity.ok(
                    service.getConnections(
                            authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
