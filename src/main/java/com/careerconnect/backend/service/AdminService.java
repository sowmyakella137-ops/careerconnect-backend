
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.Internship;
import com.careerconnect.backend.entity.InternshipApplication;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.InternshipApplicationRepository;
import com.careerconnect.backend.repository.InternshipRepository;
import com.careerconnect.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipApplicationRepository applicationRepository;

    public AdminService(
            UserRepository userRepository,
            InternshipRepository internshipRepository,
            InternshipApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.internshipRepository = internshipRepository;
        this.applicationRepository = applicationRepository;
    }

    private void checkAdmin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied. Admin role required.");
        }
    }

    public Map<String, Object> getDashboard(String email) {
        checkAdmin(email);

        List<User> users = userRepository.findAll();
        List<Internship> internships = internshipRepository.findAll();
        List<InternshipApplication> applications =
                applicationRepository.findAll();

        long students = users.stream()
                .filter(u -> u.getRole() == User.Role.STUDENT)
                .count();

        long companies = users.stream()
                .filter(u -> u.getRole() == User.Role.COMPANY)
                .count();

        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("totalUsers", users.size());
        dashboard.put("totalStudents", students);
        dashboard.put("totalCompanies", companies);
        dashboard.put("totalInternships", internships.size());
        dashboard.put("totalApplications", applications.size());

        return dashboard;
    }

    public List<Map<String, Object>> getUsers(String email) {
        checkAdmin(email);

        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", user.getId());
            item.put("name", user.getName());
            item.put("email", user.getEmail());
            item.put("role", user.getRole());
            item.put("enabled", user.isEnabled());
            return item;
        }).toList();
    }

    
public List<Map<String, Object>> getInternships(String email) {
    checkAdmin(email);

    return internshipRepository.findAll().stream().map(internship -> {
        Map<String, Object> item = new LinkedHashMap<>();

        User company = userRepository.findById(internship.getCompanyUserId())
                .orElse(null);

        item.put("id", internship.getId());
        item.put("title", internship.getTitle());
        item.put("requiredSkills", internship.getRequiredSkills());
        item.put("companyUserId", internship.getCompanyUserId());
        item.put("companyName",
                company != null ? company.getName() : "Company");

        return item;
    }).toList();
}


    
public List<Map<String, Object>> getApplications(String email) {
    checkAdmin(email);

    return applicationRepository.findAll().stream().map(application -> {
        Map<String, Object> item = new LinkedHashMap<>();

        Internship internship = internshipRepository
                .findById(application.getInternshipId())
                .orElse(null);

        User student = userRepository
                .findById(application.getStudentUserId())
                .orElse(null);

        item.put("id", application.getId());
        item.put("internshipId", application.getInternshipId());
        item.put("internshipTitle",
                internship != null
                        ? internship.getTitle()
                        : "Internship #" + application.getInternshipId());

        item.put("studentUserId", application.getStudentUserId());
        item.put("studentName",
                student != null ? student.getName() : "Student");

        item.put("status", application.getStatus());
        item.put("appliedAt", application.getAppliedAt());

        return item;
    }).toList();
}

public Map<String, Object> updateUser(
        String adminEmail, Long userId, Map<String, String> updates) {

    checkAdmin(adminEmail);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"));

    // Do not allow an admin to modify their own account here.
   

    String name = updates.get("name");
    String email = updates.get("email");

    if (name == null || name.isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Name is required");
    }

    if (email == null || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Enter a valid email address");
    }

    boolean emailUsed = userRepository.findByEmail(email.trim())
            .filter(existing -> !existing.getId().equals(userId))
            .isPresent();

    if (emailUsed) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Email is already registered");
    }

    user.setName(name.trim());
    user.setEmail(email.trim());

    User saved = userRepository.save(user);

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("id", saved.getId());
    result.put("name", saved.getName());
    result.put("email", saved.getEmail());
    result.put("role", saved.getRole());

    return result;
}

public Map<String, Object> setUserEnabled(
        String adminEmail, Long userId, boolean enabled) {

    checkAdmin(adminEmail);

    User admin = userRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Admin not found"));

    if (admin.getId().equals(userId)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "You cannot disable your own account");
    }

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"));

    user.setEnabled(enabled);
    User saved = userRepository.save(user);

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("id", saved.getId());
    result.put("name", saved.getName());
    result.put("email", saved.getEmail());
    result.put("role", saved.getRole());
    result.put("enabled", saved.isEnabled());

    return result;
}

}
