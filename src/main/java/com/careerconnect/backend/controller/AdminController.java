
package com.careerconnect.backend.controller;

import com.careerconnect.backend.service.AdminService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            Authentication authentication) {
        return ResponseEntity.ok(
                adminService.getDashboard(authentication.getName()));
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getUsers(
            Authentication authentication) {
        return ResponseEntity.ok(
                adminService.getUsers(authentication.getName()));
    }

    
@PatchMapping("/users/{id}/status")
public ResponseEntity<?> setUserStatus(
        @PathVariable Long id,
        @RequestBody Map<String, Boolean> request,
        Authentication authentication) {

    Boolean enabled = request.get("enabled");

    if (enabled == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The enabled field is required");
    }

    return ResponseEntity.ok(
            adminService.setUserEnabled(
                    authentication.getName(), id, enabled));
}


    @GetMapping("/internships")
    public ResponseEntity<List<Map<String, Object>>> getInternships(
            Authentication authentication) {
        return ResponseEntity.ok(
                adminService.getInternships(authentication.getName()));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Map<String, Object>>> getApplications(
            Authentication authentication) {
        return ResponseEntity.ok(
                adminService.getApplications(authentication.getName()));
    }

@PutMapping("/users/{id}")
public ResponseEntity<?> updateUser(
        @PathVariable Long id,
        @RequestBody Map<String, String> updates,
        Authentication authentication) {

    return ResponseEntity.ok(
            adminService.updateUser(authentication.getName(), id, updates));
}

}
