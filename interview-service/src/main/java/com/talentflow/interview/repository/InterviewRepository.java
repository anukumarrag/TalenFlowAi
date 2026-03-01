package com.talentflow.interview.repository;

import com.talentflow.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, String> {
    List<Interview> findByCandidateId(String candidateId);
    List<Interview> findByJdId(String jdId);
    List<Interview> findByStatus(Interview.InterviewStatus status);
}
