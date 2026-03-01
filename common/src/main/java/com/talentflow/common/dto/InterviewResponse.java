package com.talentflow.common.dto;

import java.util.List;

public record InterviewResponse(
    String interviewId,
    String candidateId,
    String status,
    List<String> questions
) {}
