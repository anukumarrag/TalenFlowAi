package com.talentflow.interview.service;

import com.talentflow.common.dto.InterviewRequest;
import com.talentflow.common.dto.InterviewResponse;
import com.talentflow.interview.entity.Interview;
import com.talentflow.interview.repository.InterviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository repository;

    @Mock
    private QuestionGeneratorService questionGenerator;

    @InjectMocks
    private InterviewService service;

    @Test
    void createInterview_ShouldReturnResponseWithQuestions() {
        InterviewRequest request = new InterviewRequest("candidate1", "jd1", "java");

        when(questionGenerator.generateQuestions(any(), any()))
            .thenReturn(List.of("Q1?", "Q2?", "Q3?"));

        Interview saved = new Interview();
        saved.setId("interview-123");
        saved.setCandidateId("candidate1");
        saved.setStatus(Interview.InterviewStatus.SCHEDULED);
        saved.setQuestions("Q1?||Q2?||Q3?");

        when(repository.save(any())).thenReturn(saved);

        InterviewResponse response = service.createInterview(request);

        assertThat(response.interviewId()).isEqualTo("interview-123");
        assertThat(response.candidateId()).isEqualTo("candidate1");
        assertThat(response.status()).isEqualTo("SCHEDULED");
        assertThat(response.questions()).hasSize(3);
    }

    @Test
    void getInterview_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());
        Optional<InterviewResponse> result = service.getInterview("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void startInterview_ShouldUpdateStatus() {
        Interview interview = new Interview();
        interview.setId("i1");
        interview.setCandidateId("c1");
        interview.setStatus(Interview.InterviewStatus.SCHEDULED);
        interview.setQuestions("Q1?");

        when(repository.findById("i1")).thenReturn(Optional.of(interview));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InterviewResponse response = service.startInterview("i1");
        assertThat(response.status()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void startInterview_ShouldThrow_WhenNotFound() {
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.startInterview("nonexistent"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void submitTranscript_ShouldCompleteInterview() {
        Interview interview = new Interview();
        interview.setId("i1");
        interview.setCandidateId("c1");
        interview.setStatus(Interview.InterviewStatus.IN_PROGRESS);
        interview.setStartedAt(LocalDateTime.now().minusMinutes(30));
        interview.setQuestions("Q1?");

        when(repository.findById("i1")).thenReturn(Optional.of(interview));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InterviewResponse response = service.submitTranscript("i1", "Candidate answers...");
        assertThat(response.status()).isEqualTo("COMPLETED");
    }
}
