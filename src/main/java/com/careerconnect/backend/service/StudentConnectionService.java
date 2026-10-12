
package com.careerconnect.backend.service;

import com.careerconnect.backend.entity.*;
import com.careerconnect.backend.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentConnectionService {

    private final StudentConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;

    public StudentConnectionService(
            StudentConnectionRepository connectionRepository,
            UserRepository userRepository,
            StudentProfileRepository profileRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    private User getStudent(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (user.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "This feature is available only to students");
        }

        return user;
    }
    private User getStudentForProfile(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException("User not found"));

    if (user.getRole() != User.Role.STUDENT
            && user.getRole() != User.Role.COMPANY) {
        throw new IllegalArgumentException(
                "Only students and companies can view student profiles");
    }

    return user;
}

    private User getStudentById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Student not found"));

        if (user.getRole() != User.Role.STUDENT) {
            throw new IllegalArgumentException(
                    "The requested user is not a student");
        }

        return user;
    }

    private Map<String, Object> studentDetails(User user) {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("id", user.getId());
        result.put("name", user.getName());

        profileRepository.findByUserId(user.getId())
                .ifPresentOrElse(profile -> {
                    result.put("education", profile.getEducation());
                    result.put("skills", profile.getSkills());
                    result.put("interests", profile.getInterests());
                    result.put("bio", profile.getBio());
                }, () -> {
                    result.put("education", null);
                    result.put("skills", null);
                    result.put("interests", null);
                    result.put("bio", null);
                });

        return result;
    }

    public List<Map<String, Object>> discoverStudents(
            String email, String query) {

        User currentUser = getStudent(email);

        String search = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        return userRepository.findByRole(User.Role.STUDENT)
                .stream()
                .filter(user ->
                        !user.getId().equals(currentUser.getId()))
                .filter(user -> {
                    if (search.isBlank()) {
                        return true;
                    }

                    boolean nameMatches = user.getName() != null
                            && user.getName().toLowerCase(Locale.ROOT)
                                    .contains(search);

                    Optional<StudentProfile> profile =
                            profileRepository.findByUserId(user.getId());

                    boolean profileMatches = profile.map(p ->
                            contains(p.getSkills(), search)
                            || contains(p.getInterests(), search)
                            || contains(p.getEducation(), search)
                    ).orElse(false);

                    return nameMatches || profileMatches;
                })
                .map(this::studentDetails)
                .collect(Collectors.toList());
    }

    private boolean contains(String value, String search) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(search);
    }

    
public Map<String, Object> viewStudentProfile(
        String email, Long studentId) {

    // Allow students and companies to view student profiles.
    User currentUser = getStudentForProfile(email);

    // Find the requested student's profile.
    User student = getStudentById(studentId);

    return studentDetails(student);
}


    @Transactional
    public StudentConnection sendRequest(
            String email, Long receiverId) {

        User requester = getStudent(email);
        User receiver = getStudentById(receiverId);

        if (requester.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException(
                    "You cannot send a connection request to yourself");
        }

        List<StudentConnection> existing =
                connectionRepository
                        .findByRequesterUserIdOrReceiverUserId(
                                requester.getId(), requester.getId());

        for (StudentConnection connection : existing) {
            boolean samePair =
                    (connection.getRequesterUserId()
                            .equals(requester.getId())
                    && connection.getReceiverUserId()
                            .equals(receiver.getId()))
                    ||
                    (connection.getRequesterUserId()
                            .equals(receiver.getId())
                    && connection.getReceiverUserId()
                            .equals(requester.getId()));

            if (samePair) {
                if (connection.getStatus()
                        == StudentConnection.Status.ACCEPTED) {
                    throw new IllegalArgumentException(
                            "You are already connected");
                }

                if (connection.getStatus()
                        == StudentConnection.Status.PENDING) {
                    throw new IllegalArgumentException(
                            "A connection request already exists");
                }

                // Reuse a previously rejected request.
                connection.setRequesterUserId(requester.getId());
                connection.setReceiverUserId(receiver.getId());
                connection.setStatus(
                        StudentConnection.Status.PENDING);

                return connectionRepository.save(connection);
            }
        }

        StudentConnection connection = new StudentConnection();
        connection.setRequesterUserId(requester.getId());
        connection.setReceiverUserId(receiver.getId());
        connection.setStatus(StudentConnection.Status.PENDING);

        return connectionRepository.save(connection);
    }

    @Transactional
    public StudentConnection respondToRequest(
            String email, Long connectionId, boolean accept) {

        User receiver = getStudent(email);

        StudentConnection connection = connectionRepository
                .findById(connectionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Connection request not found"));

        if (!connection.getReceiverUserId()
                .equals(receiver.getId())) {
            throw new IllegalArgumentException(
                    "You can only respond to requests sent to you");
        }

        if (connection.getStatus()
                != StudentConnection.Status.PENDING) {
            throw new IllegalArgumentException(
                    "This request has already been processed");
        }

        connection.setStatus(
                accept
                        ? StudentConnection.Status.ACCEPTED
                        : StudentConnection.Status.REJECTED);

        return connectionRepository.save(connection);
    }

    public List<Map<String, Object>> getReceivedRequests(String email) {
        User receiver = getStudent(email);

        return connectionRepository
                .findByReceiverUserIdAndStatus(
                        receiver.getId(),
                        StudentConnection.Status.PENDING)
                .stream()
                .map(connection -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("connectionId", connection.getId());
                    result.put("createdAt", connection.getCreatedAt());
                    result.put("student", studentDetails(
                            getStudentById(
                                    connection.getRequesterUserId())));
                    return result;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSentRequests(String email) {
        User requester = getStudent(email);

        return connectionRepository
                .findByRequesterUserIdAndStatus(
                        requester.getId(),
                        StudentConnection.Status.PENDING)
                .stream()
                .map(connection -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("connectionId", connection.getId());
                    result.put("createdAt", connection.getCreatedAt());
                    result.put("student", studentDetails(
                            getStudentById(
                                    connection.getReceiverUserId())));
                    return result;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getConnections(String email) {
        User currentUser = getStudent(email);

        return connectionRepository
                .findByRequesterUserIdOrReceiverUserId(
                        currentUser.getId(), currentUser.getId())
                .stream()
                .filter(connection ->
                        connection.getStatus()
                                == StudentConnection.Status.ACCEPTED)
                .map(connection -> {
                    Long otherId =
                            connection.getRequesterUserId()
                                    .equals(currentUser.getId())
                            ? connection.getReceiverUserId()
                            : connection.getRequesterUserId();

                    return studentDetails(getStudentById(otherId));
                })
                .collect(Collectors.toList());
    }
}
