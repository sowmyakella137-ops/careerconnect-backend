
package com.careerconnect.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "internship_applications",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"internshipId", "studentUserId"}
    )
)
public class InternshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long internshipId;
    private Long studentUserId;

    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime appliedAt;

    public enum Status {
        APPLIED, UNDER_REVIEW, ACCEPTED, REJECTED
    }

    public InternshipApplication() {}

    @PrePersist
    public void onCreate() {
        appliedAt = LocalDateTime.now();
        if (status == null) status = Status.APPLIED;
    }

    public Long getId() { return id; }
    public Long getInternshipId() { return internshipId; }
    public void setInternshipId(Long internshipId) {
        this.internshipId = internshipId;
    }
    public Long getStudentUserId() { return studentUserId; }
    public void setStudentUserId(Long studentUserId) {
        this.studentUserId = studentUserId;
    }
    public String getResumeUrl() { return resumeUrl; }
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
}
