
package com.careerconnect.backend.repository;

import com.careerconnect.backend.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentProfileRepository
        extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserId(Long userId);
    
}
