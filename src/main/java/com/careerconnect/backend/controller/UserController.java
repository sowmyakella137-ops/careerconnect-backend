
package com.careerconnect.backend.controller;

import com.careerconnect.backend.dto.AuthResponse;
import com.careerconnect.backend.dto.LoginRequest;
import com.careerconnect.backend.dto.RegisterRequest;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.security.JwtService;
import com.careerconnect.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/users/test")
    public String test() {
        return "CareerConnect API is working!";
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(user, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.loginUser(request);

            User user = userService.findUserByEmail(request.getEmail());

            return ResponseEntity.ok(new AuthResponse(user, token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }
    
@PostMapping("/auth/register/company")
public ResponseEntity<?> registerCompany(
        @Valid @RequestBody RegisterRequest request) {
    try {
        User company = userService.registerCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(company, null));
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

}
