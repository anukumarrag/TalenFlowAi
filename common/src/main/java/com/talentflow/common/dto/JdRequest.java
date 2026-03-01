package com.talentflow.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record JdRequest(
    @NotBlank String role,
    @NotNull @Min(0) Integer yearsOfExperience,
    @NotBlank String primaryTechStack,
    List<String> secondaryTechStack,
    String projectDomain
) {}
