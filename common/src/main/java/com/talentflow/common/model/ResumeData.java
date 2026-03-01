package com.talentflow.common.model;

import java.util.List;

public record ResumeData(
    String id,
    String candidateName,
    String email,
    String phone,
    List<String> skills,
    int yearsOfExperience,
    String education,
    String rawText
) {}
