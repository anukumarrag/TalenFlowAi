package com.talentflow.jdgenerator.repository;

import com.talentflow.jdgenerator.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobDescriptionRepository extends JpaRepository<JobDescription, String> {
    List<JobDescription> findByRole(String role);
}
