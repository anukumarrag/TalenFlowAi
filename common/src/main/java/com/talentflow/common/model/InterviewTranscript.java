package com.talentflow.common.model;

import java.util.List;

public record InterviewTranscript(
    String interviewId,
    String candidateId,
    List<QAPair> qaHistory,
    long durationSeconds
) {
    public record QAPair(String question, String answer, long timestampSeconds) {}
}
