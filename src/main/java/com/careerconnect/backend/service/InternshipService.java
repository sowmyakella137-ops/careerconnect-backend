
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.Internship;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.InternshipRepository;
import com.careerconnect.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;

    public InternshipService(
            InternshipRepository internshipRepository,
            UserRepository userRepository) {
        this.internshipRepository = internshipRepository;
        this.userRepository = userRepository;
    }

    
public List<Map<String, Object>> getAllInternships() {
    return internshipRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(internship -> {
                User company = userRepository
                        .findById(internship.getCompanyUserId())
                        .orElse(null);

                Map<String, Object> result = new LinkedHashMap<>();

                result.put("id", internship.getId());
                result.put("title", internship.getTitle());
                result.put("description", internship.getDescription());
                result.put("location", internship.getLocation());
                result.put("requiredSkills", internship.getRequiredSkills());
                result.put("deadline", internship.getDeadline());
                result.put("createdAt", internship.getCreatedAt());
                result.put("companyUserId", internship.getCompanyUserId());
                result.put(
                        "companyName",
                        company != null ? company.getName() : null
                );

                return result;
            })
            .toList();
}


    public Internship createInternship(String email, Internship request) {
        User company = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (company.getRole() != User.Role.COMPANY) {
            throw new IllegalArgumentException(
                    "Only company accounts can publish internships");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (request.getDeadline() != null
                && request.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Deadline cannot be in the past");
        }

        Internship internship = new Internship();
        internship.setCompanyUserId(company.getId());
        internship.setTitle(request.getTitle().trim());
        internship.setDescription(request.getDescription());
        internship.setRequiredSkills(request.getRequiredSkills());
        internship.setLocation(request.getLocation());
        internship.setDeadline(request.getDeadline());

        return internshipRepository.save(internship);
    }

    public List<Internship> getCompanyInternships(String email) {
        User company = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (company.getRole() != User.Role.COMPANY) {
            throw new IllegalArgumentException("Company access required");
        }

        return internshipRepository
                .findByCompanyUserIdOrderByCreatedAtDesc(company.getId());
    }

    public Internship getInternshipById(Long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Internship not found"));
    }
}
