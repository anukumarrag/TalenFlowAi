package com.talentflow.resumeparser.service;

import com.talentflow.common.dto.MatchResult;
import com.talentflow.common.model.ResumeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MatchingServiceTest {

    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        matchingService = new MatchingService();
    }

    @Test
    void calculateMatch_ShouldReturnScoreBetween0And100() {
        ResumeData resume = new ResumeData(
            "r1", "John", "john@test.com", "555-1234",
            List.of("Java", "Spring Boot", "Microservices"), 5, "B.Tech",
            "Experienced Java developer with Spring Boot and Microservices skills"
        );
        String jdText = "Looking for Java developer with Spring Boot experience, 3+ years";

        MatchResult result = matchingService.calculateMatch(resume, jdText, "jd1");

        assertThat(result.matchScore()).isBetween(0.0, 100.0);
        assertThat(result.candidateName()).isEqualTo("John");
    }

    @Test
    void calculateSkillMatch_ShouldReturn100_WhenAllSkillsMatch() {
        List<String> skills = List.of("Java", "Spring Boot");
        String jdText = "We need Java and Spring Boot developers";

        double score = matchingService.calculateSkillMatch(skills, jdText);

        assertThat(score).isEqualTo(100.0);
    }

    @Test
    void calculateSkillMatch_ShouldReturn0_WhenNoSkillsProvided() {
        double score = matchingService.calculateSkillMatch(List.of(), "Some JD text");
        assertThat(score).isZero();
    }

    @Test
    void calculateExperienceMatch_ShouldReturn100_WhenExperienceExceeds() {
        double score = matchingService.calculateExperienceMatch(10, "5+ years of experience");
        assertThat(score).isEqualTo(100.0);
    }

    @Test
    void calculateEducationMatch_ShouldReturnHighScore_ForPhD() {
        double score = matchingService.calculateEducationMatch("PhD in CS", "PhD required");
        assertThat(score).isEqualTo(100.0);
    }

    @Test
    void rankCandidates_ShouldSortByScoreDescending() {
        ResumeData strongCandidate = new ResumeData(
            "r1", "Strong", "s@t.com", "555", List.of("Java", "Spring Boot", "Kubernetes"), 8, "Master",
            "Expert Java Spring Boot Kubernetes developer 8 years"
        );
        ResumeData weakCandidate = new ResumeData(
            "r2", "Weak", "w@t.com", "555", List.of("Python"), 1, "Not specified",
            "Junior Python developer"
        );
        String jdText = "Senior Java developer with Spring Boot, Kubernetes, 5+ years experience";

        List<MatchResult> ranked = matchingService.rankCandidates(
            List.of(weakCandidate, strongCandidate), jdText, "jd1"
        );

        assertThat(ranked.get(0).candidateName()).isEqualTo("Strong");
        assertThat(ranked.get(0).matchScore()).isGreaterThan(ranked.get(1).matchScore());
    }

    @Test
    void calculateSemanticSimilarity_ShouldReturn0_ForNullInput() {
        double score = matchingService.calculateSemanticSimilarity(null, "text");
        assertThat(score).isZero();
    }
}
