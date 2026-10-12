
package com.careerconnect.backend.dto;

import com.careerconnect.backend.entity.User;

public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private User.Role role;
    private String token;

    public AuthResponse(User user, String token) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public User.Role getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}
