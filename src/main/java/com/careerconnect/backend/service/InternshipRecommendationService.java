
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.Internship;
import com.careerconnect.backend.entity.StudentProfile;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.InternshipRepository;
import com.careerconnect.backend.repository.StudentProfileRepository;
import com.careerconnect.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InternshipRecommendationService {

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final InternshipRepository internshipRepository;

    public InternshipRecommendationService(
            UserRepository userRepository,
            StudentProfileRepository profileRepository,
            InternshipRepository internshipRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.internshipRepository = internshipRepository;
    }

    public Map<String, Object> getRecommendations(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != User.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only students can view internship recommendations");
        }

        StudentProfile profile = profileRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Create your student profile first"));

        Set<String> studentSkills = splitSkills(profile.getSkills());

        List<Map<String, Object>> recommendations =
                internshipRepository.findAll().stream()
                .map(internship -> {
                    List<String> requiredSkills =
                            new ArrayList<>(splitSkills(
                                    internship.getRequiredSkills()));

                    List<String> matchedSkills = requiredSkills.stream()
                            .filter(skill -> studentSkills.contains(
                                    normalize(skill)))
                            .collect(Collectors.toList());

                    List<String> missingSkills = requiredSkills.stream()
                            .filter(skill -> !studentSkills.contains(
                                    normalize(skill)))
                            .collect(Collectors.toList());

                    double percentage = requiredSkills.isEmpty()
                            ? 0.0
                            : Math.round(
                                    matchedSkills.size() * 10000.0
                                            / requiredSkills.size()) / 100.0;

                   
Map<String, Object> result = new LinkedHashMap<>();

User company = userRepository.findById(internship.getCompanyUserId())
        .orElse(null);

result.put("internshipId", internship.getId());
result.put("id", internship.getId());
result.put("title", internship.getTitle());
result.put("companyUserId", internship.getCompanyUserId());
result.put("companyName",
        company != null ? company.getName() : "Company");
result.put("requiredSkills", requiredSkills);
result.put("matchedSkills", matchedSkills);
result.put("missingSkills", missingSkills);
result.put("matchPercentage", percentage);

return result;

                })
                .sorted((a, b) -> Double.compare(
                        (Double) b.get("matchPercentage"),
                        (Double) a.get("matchPercentage")))
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("studentSkills", studentSkills);
        response.put("recommendations", recommendations);

        return response;
    }

    private Set<String> splitSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return new LinkedHashSet<>();
        }

        return Arrays.stream(skills.split("[,;\\n]"))
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .map(this::normalize)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String normalize(String skill) {
        return skill.trim().toLowerCase(Locale.ROOT);
    }
}
