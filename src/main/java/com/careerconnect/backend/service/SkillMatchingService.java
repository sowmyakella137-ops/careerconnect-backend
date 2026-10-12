
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.Internship;
import com.careerconnect.backend.entity.StudentProfile;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.InternshipRepository;
import com.careerconnect.backend.repository.StudentProfileRepository;
import com.careerconnect.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SkillMatchingService {

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final InternshipRepository internshipRepository;

    public SkillMatchingService(
            UserRepository userRepository,
            StudentProfileRepository profileRepository,
            InternshipRepository internshipRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.internshipRepository = internshipRepository;
    }

    public Map<String, Object> matchInternship(String email, Long internshipId) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (user.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException("Only students can use skill matching.");
        }

        StudentProfile profile = profileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student profile not found."));

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Internship not found."));

        Set<String> studentSkills = splitSkills(profile.getSkills());
        Set<String> requiredSkills = splitSkills(internship.getRequiredSkills());

        Set<String> matchedSkills = new LinkedHashSet<>(studentSkills);
        matchedSkills.retainAll(requiredSkills);

        Set<String> missingSkills = new LinkedHashSet<>(requiredSkills);
        missingSkills.removeAll(studentSkills);

        double percentage = requiredSkills.isEmpty()
                ? 0.0
                : (matchedSkills.size() * 100.0 / requiredSkills.size());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("internshipId", internship.getId());
        result.put("internshipTitle", internship.getTitle());
        result.put("studentSkills", studentSkills);
        result.put("requiredSkills", requiredSkills);
        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);
        result.put("matchPercentage", Math.round(percentage * 100.0) / 100.0);

        return result;
    }

    private Set<String> splitSkills(String skills) {
        if (skills == null || skills.isBlank()) {
            return new LinkedHashSet<>();
        }

        return Arrays.stream(skills.split("[,;\\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
