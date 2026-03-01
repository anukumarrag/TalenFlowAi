package com.talentflow.common.dto;

import jakarta.validation.constraints.NotBlank;

public record InterviewRequest(
    @NotBlank String candidateId,
    @NotBlank String jdId,
    String interviewType
) {}
