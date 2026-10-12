
package com.careerconnect.backend.entity;

import jakarta.persistence.*;

@Entity
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String education;
    
    @Column(length = 1000)
    private String skills;

    @Column(length = 1000)
    private String interests;

    @Column(length = 1000)
    private String bio;

    private String resumeUrl;

    public StudentProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
}

