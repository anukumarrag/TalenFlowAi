package com.talentflow.resumeparser.repository;

import com.talentflow.resumeparser.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {
    List<Resume> findByCandidateNameContainingIgnoreCase(String name);
    List<Resume> findByStatus(String status);
}
