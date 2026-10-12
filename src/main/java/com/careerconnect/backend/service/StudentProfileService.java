
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.StudentProfile;
import com.careerconnect.backend.entity.User;
import com.careerconnect.backend.repository.StudentProfileRepository;
import com.careerconnect.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public StudentProfileService(
            StudentProfileRepository profileRepository,
            UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public StudentProfile saveProfileForUser(
            StudentProfile profile, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        StudentProfile existing = profileRepository
                .findByUserId(user.getId())
                .orElse(new StudentProfile());

        existing.setUserId(user.getId());
        existing.setEducation(profile.getEducation());
        existing.setSkills(profile.getSkills());
        existing.setInterests(profile.getInterests());
        existing.setBio(profile.getBio());
        existing.setResumeUrl(profile.getResumeUrl());

        return profileRepository.save(existing);
    }

    public StudentProfile getProfileForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return profileRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Student profile not found"));
    }
}
