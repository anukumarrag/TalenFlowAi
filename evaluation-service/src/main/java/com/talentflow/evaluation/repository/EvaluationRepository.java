package com.talentflow.evaluation.repository;

import com.talentflow.evaluation.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, String> {
    List<Evaluation> findByCandidateId(String candidateId);
    List<Evaluation> findByJdId(String jdId);
    List<Evaluation> findByRecommendation(String recommendation);
    List<Evaluation> findByJdIdOrderByTechnicalProficiencyDesc(String jdId);
}
