
package com.careerconnect.backend.repository;

import com.careerconnect.backend.entity.Internship;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InternshipRepository
        extends JpaRepository<Internship, Long> {

    List<Internship> findAllByOrderByCreatedAtDesc();

    List<Internship> findByCompanyUserIdOrderByCreatedAtDesc(
            Long companyUserId);
    List<Internship> findByCompanyUserId(Long companyUserId);
}
