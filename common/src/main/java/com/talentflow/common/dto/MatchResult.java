package com.talentflow.common.dto;

public record MatchResult(
    String resumeId,
    String candidateName,
    String jdId,
    double matchScore,
    String matchSummary
) {}
