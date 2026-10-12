
package com.careerconnect.backend.service;

import com.careerconnect.backend.dto.LoginRequest;
import com.careerconnect.backend.dto.RegisterRequest;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.UserRepository;
import com.careerconnect.backend.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.STUDENT);

        return userRepository.save(user);
    }

    public String loginUser(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        if (!user.isEnabled()) {
    throw new IllegalArgumentException(
            "Your account has been disabled. Contact the administrator.");
}

        return jwtService.generateToken(user);
    }
    
public User findUserByEmail(String email) {
    return userRepository.findByEmail(email.trim().toLowerCase())
            .orElseThrow(() ->
                new IllegalArgumentException("User not found"));
}

public User registerCompany(RegisterRequest request) {
    String email = request.getEmail().trim().toLowerCase();

    if (userRepository.findByEmail(email).isPresent()) {
        throw new IllegalArgumentException(
                "Email is already registered");
    }

    User company = new User();
    company.setName(request.getName().trim());
    company.setEmail(email);
    company.setPassword(
            passwordEncoder.encode(request.getPassword()));
    company.setRole(User.Role.COMPANY);

    return userRepository.save(company);
}

}
