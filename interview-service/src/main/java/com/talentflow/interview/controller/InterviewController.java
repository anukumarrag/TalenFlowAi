package com.talentflow.interview.controller;

import com.talentflow.common.dto.InterviewRequest;
import com.talentflow.common.dto.InterviewResponse;
import com.talentflow.interview.service.InterviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(@Valid @RequestBody InterviewRequest request) {
        InterviewResponse response = interviewService.createInterview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterviewResponse> getInterview(@PathVariable String id) {
        return interviewService.getInterview(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<InterviewResponse> startInterview(@PathVariable String id) {
        InterviewResponse response = interviewService.startInterview(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<InterviewResponse> submitTranscript(
            @PathVariable String id,
            @RequestBody String transcript) {
        InterviewResponse response = interviewService.submitTranscript(id, transcript);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<List<InterviewResponse>> getByCandidate(@PathVariable String candidateId) {
        return ResponseEntity.ok(interviewService.getInterviewsByCandidateId(candidateId));
    }

    @GetMapping("/job/{jdId}")
    public ResponseEntity<List<InterviewResponse>> getByJob(@PathVariable String jdId) {
        return ResponseEntity.ok(interviewService.getInterviewsByJdId(jdId));
    }
}
