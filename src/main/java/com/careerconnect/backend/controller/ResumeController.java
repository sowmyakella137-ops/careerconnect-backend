
package com.careerconnect.backend.controller;

import com.careerconnect.backend.entity.StudentProfile;
import com.careerconnect.backend.repository.StudentProfileRepository;
import com.careerconnect.backend.repository.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ResumeController(
        StudentProfileRepository profileRepository,
        UserRepository userRepository) {
    this.profileRepository = profileRepository;
    this.userRepository = userRepository;
}
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please upload a PDF resume."));
            }

            String filename = file.getOriginalFilename();
            if (filename == null ||
                    !filename.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Only PDF resumes are supported."));
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Maximum file size is 5 MB."));
            }

            String email = authentication.getName();

           var user = userRepository.findByEmail(email)
        .orElseThrow(() ->
                new IllegalArgumentException("User not found."));

if (user.getRole() != com.careerconnect.backend.entity.User.Role.STUDENT) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("error", "Only students can upload resumes."));
}

StudentProfile profile = profileRepository.findByUserId(user.getId())
        .orElseThrow(() ->
                new IllegalArgumentException(
                        "Student profile not found. Create your profile first."));

            String extractedText;

            try (PDDocument document = Loader.loadPDF(file.getBytes())) {
                extractedText = new PDFTextStripper().getText(document);
            }

            if (extractedText == null || extractedText.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "No text found. This PDF may be scanned; use a text-based PDF."));
            }

            Set<String> skills = extractSkills(extractedText);

            profile.setSkills(String.join(", ", skills));
            profileRepository.save(profile);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Resume uploaded and processed successfully.");
            response.put("extractedSkills", skills);
            response.put("savedSkills", profile.getSkills());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unable to read this PDF file."));
        }
    }

    private Set<String> extractSkills(String text) {
        Map<String, String> knownSkills = new LinkedHashMap<>();

        knownSkills.put("java", "Java");
        knownSkills.put("python", "Python");
        knownSkills.put("c++", "C++");
        knownSkills.put("c programming", "C");
        knownSkills.put("javascript", "JavaScript");
        knownSkills.put("typescript", "TypeScript");
        knownSkills.put("html", "HTML");
        knownSkills.put("css", "CSS");
        knownSkills.put("react", "React");
        knownSkills.put("spring boot", "Spring Boot");
        knownSkills.put("spring", "Spring");
        knownSkills.put("sql", "SQL");
        knownSkills.put("mysql", "MySQL");
        knownSkills.put("postgresql", "PostgreSQL");
        knownSkills.put("mongodb", "MongoDB");
        knownSkills.put("git", "Git");
        knownSkills.put("github", "GitHub");
        knownSkills.put("docker", "Docker");
        knownSkills.put("aws", "AWS");
        knownSkills.put("machine learning", "Machine Learning");
        knownSkills.put("data analysis", "Data Analysis");
        knownSkills.put("pandas", "Pandas");
        knownSkills.put("numpy", "NumPy");
        knownSkills.put("scikit-learn", "Scikit-learn");
        knownSkills.put("communication", "Communication");
        knownSkills.put("problem solving", "Problem Solving");

        String lowerText = text.toLowerCase(Locale.ROOT);
        Set<String> found = new LinkedHashSet<>();

        for (Map.Entry<String, String> skill : knownSkills.entrySet()) {
            String pattern = "(?<![a-z0-9+#])" +
                    Pattern.quote(skill.getKey()) +
                    "(?![a-z0-9+#])";

            if (Pattern.compile(pattern).matcher(lowerText).find()) {
                found.add(skill.getValue());
            }
        }

        return found;
    }
}
