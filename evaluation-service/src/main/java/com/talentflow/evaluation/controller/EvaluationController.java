package com.talentflow.evaluation.controller;

import com.talentflow.common.dto.CandidateScorecard;
import com.talentflow.common.dto.EvaluationRequest;
import com.talentflow.evaluation.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping
    public ResponseEntity<CandidateScorecard> evaluate(@Valid @RequestBody EvaluationRequest request) {
        CandidateScorecard scorecard = evaluationService.evaluate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(scorecard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateScorecard> getEvaluation(@PathVariable String id) {
        return evaluationService.getEvaluation(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/shortlist/{jdId}")
    public ResponseEntity<List<CandidateScorecard>> getShortlist(
            @PathVariable String jdId,
            @RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(evaluationService.getShortlist(jdId, topN));
    }

    @GetMapping("/job/{jdId}")
    public ResponseEntity<List<CandidateScorecard>> getByJob(@PathVariable String jdId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByJdId(jdId));
    }
}
