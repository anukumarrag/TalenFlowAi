package com.talentflow.evaluation.service;

import com.talentflow.common.dto.CandidateScorecard;
import com.talentflow.common.dto.EvaluationRequest;
import com.talentflow.evaluation.entity.Evaluation;
import com.talentflow.evaluation.repository.EvaluationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository repository;

    @InjectMocks
    private EvaluationService service;

    @Test
    void evaluate_ShouldReturnScorecard() {
        EvaluationRequest request = new EvaluationRequest(
            "interview1", "candidate1", "jd1",
            "I have extensive experience with microservices architecture and design patterns. " +
            "I implemented a scalable API using Spring Boot with proper testing and CI/CD deployment. " +
            "For example, in my previous role, I designed a high-performance caching system."
        );

        Evaluation saved = new Evaluation();
        saved.setId("eval-1");
        saved.setCandidateId("candidate1");
        saved.setJdId("jd1");
        saved.setTechnicalProficiency(70.0);
        saved.setCommunicationSkills(65.0);
        saved.setRedFlags("");
        saved.setRecommendation("HIRE");
        saved.setEvaluationSummary("Summary");

        when(repository.save(any())).thenReturn(saved);

        CandidateScorecard scorecard = service.evaluate(request);

        assertThat(scorecard).isNotNull();
        assertThat(scorecard.candidateId()).isEqualTo("candidate1");
    }

    @Test
    void assessTechnicalProficiency_ShouldReturnHighScore_ForTechnicalTranscript() {
        String transcript = "I implemented a microservices architecture using design patterns. " +
            "The system handles concurrency with proper thread management. " +
            "I focused on scalability and performance optimization with caching and database tuning. " +
            "The deployment uses CI/CD pipelines with comprehensive testing including unit and integration tests. " +
            "I also implemented security measures and API gateway patterns for the framework.";

        double score = service.assessTechnicalProficiency(transcript);
        assertThat(score).isGreaterThan(50.0);
    }

    @Test
    void assessTechnicalProficiency_ShouldReturnZero_ForBlankTranscript() {
        double score = service.assessTechnicalProficiency("");
        assertThat(score).isZero();
    }

    @Test
    void assessCommunicationSkills_ShouldReturnHighScore_ForWellStructured() {
        String transcript = "First, I would like to explain my approach. " +
            "I believe in structured problem solving. " +
            "For example, when I faced a similar challenge, I broke it down into components. " +
            "Second, I analyzed each part carefully. " +
            "Finally, I synthesized the solution and validated it through testing. " +
            "This approach consistently delivers quality results in my experience.";

        double score = service.assessCommunicationSkills(transcript);
        assertThat(score).isGreaterThan(50.0);
    }

    @Test
    void detectRedFlags_ShouldFlagVagueAnswers() {
        String transcript = "I think maybe we could do something. I guess it works somehow. " +
            "I'm not sure about the details. I think it's fine. Maybe we can try later. I guess so.";

        List<String> flags = service.detectRedFlags(transcript);
        assertThat(flags).anyMatch(f -> f.contains("uncertainty"));
    }

    @Test
    void detectRedFlags_ShouldFlagShortResponses() {
        String transcript = "Yes. No. I don't know.";
        List<String> flags = service.detectRedFlags(transcript);
        assertThat(flags).anyMatch(f -> f.contains("short"));
    }

    @Test
    void generateRecommendation_ShouldReturnStrongHire_ForHighScores() {
        String rec = service.generateRecommendation(90.0, 85.0, List.of());
        assertThat(rec).isEqualTo("STRONG_HIRE");
    }

    @Test
    void generateRecommendation_ShouldReturnNoHire_ForLowScores() {
        String rec = service.generateRecommendation(20.0, 15.0, List.of("flag1", "flag2"));
        assertThat(rec).isEqualTo("NO_HIRE");
    }

    @Test
    void generateRecommendation_ShouldAccountForRedFlagPenalty() {
        String recNoFlags = service.generateRecommendation(70.0, 70.0, List.of());
        String recWithFlags = service.generateRecommendation(70.0, 70.0, List.of("f1", "f2", "f3", "f4"));
        // With multiple flags, recommendation should be lower
        assertThat(recNoFlags).isNotEqualTo(recWithFlags);
    }

    @Test
    void getEvaluation_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findById("x")).thenReturn(Optional.empty());
        assertThat(service.getEvaluation("x")).isEmpty();
    }
}
