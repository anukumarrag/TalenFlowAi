package com.talentflow.evaluation.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String candidateId;

    private String candidateName;

    @Column(nullable = false)
    private String jdId;

    private String interviewId;
    private double technicalProficiency;
    private double communicationSkills;

    @Column(columnDefinition = "TEXT")
    private String redFlags;

    @Column(nullable = false)
    private String recommendation;

    @Column(columnDefinition = "TEXT")
    private String evaluationSummary;

    private LocalDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        evaluatedAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public String getJdId() { return jdId; }
    public void setJdId(String jdId) { this.jdId = jdId; }
    public String getInterviewId() { return interviewId; }
    public void setInterviewId(String interviewId) { this.interviewId = interviewId; }
    public double getTechnicalProficiency() { return technicalProficiency; }
    public void setTechnicalProficiency(double technicalProficiency) { this.technicalProficiency = technicalProficiency; }
    public double getCommunicationSkills() { return communicationSkills; }
    public void setCommunicationSkills(double communicationSkills) { this.communicationSkills = communicationSkills; }
    public String getRedFlags() { return redFlags; }
    public void setRedFlags(String redFlags) { this.redFlags = redFlags; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getEvaluationSummary() { return evaluationSummary; }
    public void setEvaluationSummary(String evaluationSummary) { this.evaluationSummary = evaluationSummary; }
    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
