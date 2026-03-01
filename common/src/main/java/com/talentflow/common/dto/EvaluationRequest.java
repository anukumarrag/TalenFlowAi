package com.talentflow.common.dto;

import jakarta.validation.constraints.NotBlank;

public record EvaluationRequest(
    @NotBlank String interviewId,
    @NotBlank String candidateId,
    @NotBlank String jdId,
    String transcript
) {}
