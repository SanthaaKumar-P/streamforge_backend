package com.streamforge.repository;

import com.streamforge.entity.EvaluationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationCommentRepository extends JpaRepository<EvaluationComment, Long> {

    List<EvaluationComment> findByEvaluationEvaluationId(Long evaluationId);

    List<EvaluationComment> findByUserUserId(Long userId);

}