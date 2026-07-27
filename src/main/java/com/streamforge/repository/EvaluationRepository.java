package com.streamforge.repository;

import com.streamforge.entity.Evaluation;
import com.streamforge.enums.EvaluationDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByShowShowId(Long showId);

    List<Evaluation> findByEvaluatorUserId(Long evaluatorId);

    List<Evaluation> findByDecision(EvaluationDecision decision);

}