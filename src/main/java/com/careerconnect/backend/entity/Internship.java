
package com.careerconnect.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "internships")
public class Internship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyUserId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String requiredSkills;
    private String location;
    private LocalDate deadline;
    private LocalDateTime createdAt;

    public Internship() {}

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getCompanyUserId() { return companyUserId; }
    public void setCompanyUserId(Long companyUserId) {
        this.companyUserId = companyUserId;
    }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
