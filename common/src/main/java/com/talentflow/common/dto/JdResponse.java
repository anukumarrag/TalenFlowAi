package com.talentflow.common.dto;

public record JdResponse(
    String id,
    String role,
    String jobDescription,
    String generatedAt
) {}
