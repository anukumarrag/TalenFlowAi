package com.talentflow.common.dto;

public record ResumeUploadResponse(
    String resumeId,
    String candidateName,
    String status,
    String message
) {}
