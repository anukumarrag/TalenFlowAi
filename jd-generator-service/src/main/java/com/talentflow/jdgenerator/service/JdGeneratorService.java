package com.talentflow.jdgenerator.service;

import com.talentflow.common.dto.JdRequest;
import com.talentflow.common.dto.JdResponse;
import com.talentflow.jdgenerator.entity.JobDescription;
import com.talentflow.jdgenerator.repository.JobDescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JdGeneratorService {

    private final JobDescriptionRepository repository;

    public JdGeneratorService(JobDescriptionRepository repository) {
        this.repository = repository;
    }

    public JdResponse generateJobDescription(JdRequest request) {
        String description = buildJobDescription(request);

        JobDescription entity = new JobDescription();
        entity.setRole(request.role());
        entity.setYearsOfExperience(request.yearsOfExperience());
        entity.setPrimaryTechStack(request.primaryTechStack());
        entity.setSecondaryTechStack(
            request.secondaryTechStack() != null ? String.join(", ", request.secondaryTechStack()) : ""
        );
        entity.setProjectDomain(request.projectDomain());
        entity.setGeneratedDescription(description);

        JobDescription saved = repository.save(entity);

        return new JdResponse(
            saved.getId(),
            saved.getRole(),
            saved.getGeneratedDescription(),
            saved.getCreatedAt().toString()
        );
    }

    public Optional<JdResponse> getJobDescription(String id) {
        return repository.findById(id)
            .map(jd -> new JdResponse(jd.getId(), jd.getRole(), jd.getGeneratedDescription(), jd.getCreatedAt().toString()));
    }

    public List<JdResponse> getAllJobDescriptions() {
        return repository.findAll().stream()
            .map(jd -> new JdResponse(jd.getId(), jd.getRole(), jd.getGeneratedDescription(), jd.getCreatedAt().toString()))
            .toList();
    }

    private String buildJobDescription(JdRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Job Description: ").append(request.role()).append("\n\n");
        sb.append("## About the Role\n");
        sb.append("We are looking for an experienced ").append(request.role());
        sb.append(" with ").append(request.yearsOfExperience()).append("+ years of experience");
        if (request.projectDomain() != null && !request.projectDomain().isBlank()) {
            sb.append(" in the ").append(request.projectDomain()).append(" domain");
        }
        sb.append(".\n\n");

        sb.append("## Required Skills\n");
        sb.append("- **Primary**: ").append(request.primaryTechStack()).append("\n");
        if (request.secondaryTechStack() != null && !request.secondaryTechStack().isEmpty()) {
            sb.append("- **Secondary**: ").append(String.join(", ", request.secondaryTechStack())).append("\n");
        }
        sb.append("\n");

        sb.append("## Qualifications\n");
        sb.append("- Minimum ").append(request.yearsOfExperience()).append(" years of hands-on experience with ").append(request.primaryTechStack()).append("\n");
        sb.append("- Strong problem-solving and analytical skills\n");
        sb.append("- Excellent communication and collaboration abilities\n");
        sb.append("- Bachelor's degree in Computer Science or related field (or equivalent experience)\n\n");

        sb.append("## Responsibilities\n");
        sb.append("- Design, develop, and maintain high-quality software solutions\n");
        sb.append("- Collaborate with cross-functional teams to define and implement new features\n");
        sb.append("- Participate in code reviews and contribute to engineering best practices\n");
        sb.append("- Mentor junior team members and contribute to technical decision-making\n\n");

        sb.append("## What We Offer\n");
        sb.append("- Competitive salary and benefits package\n");
        sb.append("- Opportunity to work with cutting-edge technologies\n");
        sb.append("- Collaborative and inclusive work environment\n");
        sb.append("- Professional development and growth opportunities\n");

        return sb.toString();
    }
}
