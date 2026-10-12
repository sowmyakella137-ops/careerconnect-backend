
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.*;
import com.careerconnect.backend.repository.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.LinkedHashMap;

import java.util.List;

@Service
public class InternshipApplicationService {

    private final InternshipApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;

    public InternshipApplicationService(
            InternshipApplicationRepository applicationRepository,
            InternshipRepository internshipRepository,
            UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    public InternshipApplication apply(
            String email, Long internshipId, String resumeUrl) {

        User student = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (student.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "Only student accounts can apply");
        }

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Internship not found"));

        if (internship.getDeadline() != null
                && internship.getDeadline().isBefore(
                        java.time.LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Application deadline has passed");
        }

        if (applicationRepository.existsByInternshipIdAndStudentUserId(
                internshipId, student.getId())) {
            throw new IllegalArgumentException(
                    "You have already applied for this internship");
        }

        InternshipApplication application = new InternshipApplication();
        application.setInternshipId(internship.getId());
        application.setStudentUserId(student.getId());
        application.setResumeUrl(resumeUrl);

        return applicationRepository.save(application);
    }

    

public List<Map<String, Object>> getMyApplications(String email) {
    User student = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException("Student not found"));

    if (student.getRole() != User.Role.STUDENT) {
        throw new IllegalArgumentException(
                "Only students can view their applications");
    }

    return applicationRepository
            .findByStudentUserId(student.getId())
            .stream()
            .map(application -> {
                Internship internship = internshipRepository
                        .findById(application.getInternshipId())
                        .orElse(null);

                User company = internship != null
                        ? userRepository.findById(
                                internship.getCompanyUserId())
                                .orElse(null)
                        : null;

                Map<String, Object> result = new LinkedHashMap<>();

                result.put("id", application.getId());
                result.put("studentUserId", application.getStudentUserId());
                result.put("internshipId", application.getInternshipId());

                result.put(
                        "internshipTitle",
                        internship != null
                                ? internship.getTitle()
                                : "Internship #" + application.getInternshipId()
                );

                result.put(
                        "companyName",
                        company != null
                                ? company.getName()
                                : "Company"
                );

                result.put("status", application.getStatus());
                result.put("appliedAt", application.getAppliedAt());
                result.put("resumeUrl", application.getResumeUrl());

                return result;
            })
            .toList();
}



public List<Map<String, Object>> getCompanyApplications(String email) {
    User company = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Company not found"));

    if (company.getRole() != User.Role.COMPANY) {
        throw new RuntimeException("Only companies can view applicants");
    }

    List<Long> internshipIds = internshipRepository
            .findByCompanyUserId(company.getId())
            .stream()
            .map(Internship::getId)
            .toList();

    if (internshipIds.isEmpty()) {
        return List.of();
    }

    return applicationRepository
            .findByInternshipIdIn(internshipIds)
            .stream()
            .map(application -> {
                User student = userRepository
                        .findById(application.getStudentUserId())
                        .orElse(null);

                Internship internship = internshipRepository
                        .findById(application.getInternshipId())
                        .orElse(null);

                Map<String, Object> result = new LinkedHashMap<>();

                result.put("id", application.getId());
                result.put("studentUserId", application.getStudentUserId());
                result.put(
                        "studentName",
                        student != null ? student.getName() : "MissingUser"
                );
                result.put(
                        "studentEmail",
                        student != null ? student.getEmail() : null
                );
                result.put("internshipId", application.getInternshipId());
                result.put(
                        "internshipTitle",
                        internship != null
                                ? internship.getTitle()
                                : "Internship"
                );
                result.put("status", application.getStatus());
                result.put("appliedAt", application.getAppliedAt());
                result.put("resumeUrl", application.getResumeUrl());

                return result;
            })
            .toList();
}


    public InternshipApplication updateApplicationStatus(
            String email, Long applicationId, String status) {

        User company = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (company.getRole() != User.Role.COMPANY) {
            throw new IllegalArgumentException(
                    "Only companies can update application status");
        }

        InternshipApplication application = applicationRepository
                .findById(applicationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Application not found"));

        Internship internship = internshipRepository
                .findById(application.getInternshipId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Internship not found"));

        if (!internship.getCompanyUserId().equals(company.getId())) {
            throw new IllegalArgumentException(
                    "You cannot update applications for another company's internship");
        }

        try {
            InternshipApplication.Status newStatus =
                    InternshipApplication.Status.valueOf(
                            status.trim().toUpperCase());

            application.setStatus(newStatus);
            return applicationRepository.save(application);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status. Use APPLIED, UNDER_REVIEW, ACCEPTED, or REJECTED");
        }
    }
}
