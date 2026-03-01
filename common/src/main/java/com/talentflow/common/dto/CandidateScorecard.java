package com.talentflow.common.dto;

import java.util.List;

public record CandidateScorecard(
    String candidateId,
    String candidateName,
    String jdId,
    double technicalProficiency,
    double communicationSkills,
    List<String> redFlags,
    String recommendation,
    String evaluationSummary
) {}
