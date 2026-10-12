
package com.careerconnect.backend.repository;

import com.careerconnect.backend.entity.InternshipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InternshipApplicationRepository
        extends JpaRepository<InternshipApplication, Long> {

    boolean existsByInternshipIdAndStudentUserId(
            Long internshipId, Long studentUserId);

    List<InternshipApplication> findByStudentUserIdOrderByAppliedAtDesc(
            Long studentUserId);

    Optional<InternshipApplication> findByIdAndStudentUserId(
            Long id, Long studentUserId);
    List<InternshipApplication> findByInternshipIdIn(
        List<Long> internshipIds);
        List<InternshipApplication> findByStudentUserId(Long studentUserId);
}
