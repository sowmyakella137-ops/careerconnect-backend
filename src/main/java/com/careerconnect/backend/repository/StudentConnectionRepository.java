
package com.careerconnect.backend.repository;

import com.careerconnect.backend.entity.StudentConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentConnectionRepository
        extends JpaRepository<StudentConnection, Long> {

    List<StudentConnection> findByRequesterUserIdOrReceiverUserId(
            Long requesterUserId,
            Long receiverUserId);

    List<StudentConnection> findByReceiverUserIdAndStatus(
            Long receiverUserId,
            StudentConnection.Status status);

    List<StudentConnection> findByRequesterUserIdAndStatus(
            Long requesterUserId,
            StudentConnection.Status status);
}
