
package com.careerconnect.backend.repository;

import com.careerconnect.backend.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostCommentRepository
        extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
