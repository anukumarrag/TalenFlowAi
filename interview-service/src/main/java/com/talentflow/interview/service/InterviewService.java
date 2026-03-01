package com.talentflow.interview.service;

import com.talentflow.common.dto.InterviewRequest;
import com.talentflow.common.dto.InterviewResponse;
import com.talentflow.interview.entity.Interview;
import com.talentflow.interview.repository.InterviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InterviewService {

    private final InterviewRepository repository;
    private final QuestionGeneratorService questionGenerator;

    public InterviewService(InterviewRepository repository, QuestionGeneratorService questionGenerator) {
        this.repository = repository;
        this.questionGenerator = questionGenerator;
    }

    public InterviewResponse createInterview(InterviewRequest request) {
        List<String> questions = questionGenerator.generateQuestions(
            request.interviewType(), request.interviewType()
        );

        Interview interview = new Interview();
        interview.setCandidateId(request.candidateId());
        interview.setJdId(request.jdId());
        interview.setInterviewType(request.interviewType());
        interview.setQuestions(String.join("||", questions));

        Interview saved = repository.save(interview);

        return new InterviewResponse(
            saved.getId(),
            saved.getCandidateId(),
            saved.getStatus().name(),
            questions
        );
    }

    public Optional<InterviewResponse> getInterview(String id) {
        return repository.findById(id).map(this::toResponse);
    }

    public InterviewResponse startInterview(String id) {
        Interview interview = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Interview not found: " + id));
        interview.setStatus(Interview.InterviewStatus.IN_PROGRESS);
        interview.setStartedAt(LocalDateTime.now());
        Interview saved = repository.save(interview);
        return toResponse(saved);
    }

    public InterviewResponse submitTranscript(String id, String transcript) {
        Interview interview = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Interview not found: " + id));
        interview.setTranscript(transcript);
        interview.setStatus(Interview.InterviewStatus.COMPLETED);
        interview.setCompletedAt(LocalDateTime.now());
        if (interview.getStartedAt() != null) {
            interview.setDurationSeconds(
                java.time.Duration.between(interview.getStartedAt(), interview.getCompletedAt()).getSeconds()
            );
        }
        Interview saved = repository.save(interview);
        return toResponse(saved);
    }

    public List<InterviewResponse> getInterviewsByCandidateId(String candidateId) {
        return repository.findByCandidateId(candidateId).stream().map(this::toResponse).toList();
    }

    public List<InterviewResponse> getInterviewsByJdId(String jdId) {
        return repository.findByJdId(jdId).stream().map(this::toResponse).toList();
    }

    private InterviewResponse toResponse(Interview interview) {
        List<String> questions = interview.getQuestions() != null
            ? List.of(interview.getQuestions().split("\\|\\|"))
            : List.of();
        return new InterviewResponse(
            interview.getId(),
            interview.getCandidateId(),
            interview.getStatus().name(),
            questions
        );
    }
}
