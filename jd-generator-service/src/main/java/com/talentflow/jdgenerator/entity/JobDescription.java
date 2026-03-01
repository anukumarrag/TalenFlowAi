package com.talentflow.jdgenerator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_descriptions")
public class JobDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String role;

    private int yearsOfExperience;

    @Column(nullable = false)
    private String primaryTechStack;

    private String secondaryTechStack;

    private String projectDomain;

    @Column(columnDefinition = "TEXT")
    private String generatedDescription;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getPrimaryTechStack() { return primaryTechStack; }
    public void setPrimaryTechStack(String primaryTechStack) { this.primaryTechStack = primaryTechStack; }
    public String getSecondaryTechStack() { return secondaryTechStack; }
    public void setSecondaryTechStack(String secondaryTechStack) { this.secondaryTechStack = secondaryTechStack; }
    public String getProjectDomain() { return projectDomain; }
    public void setProjectDomain(String projectDomain) { this.projectDomain = projectDomain; }
    public String getGeneratedDescription() { return generatedDescription; }
    public void setGeneratedDescription(String generatedDescription) { this.generatedDescription = generatedDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
