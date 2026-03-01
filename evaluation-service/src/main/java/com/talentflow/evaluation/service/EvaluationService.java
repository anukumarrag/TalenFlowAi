package com.talentflow.evaluation.service;

import com.talentflow.common.dto.CandidateScorecard;
import com.talentflow.common.dto.EvaluationRequest;
import com.talentflow.evaluation.entity.Evaluation;
import com.talentflow.evaluation.repository.EvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final EvaluationRepository repository;

    public EvaluationService(EvaluationRepository repository) {
        this.repository = repository;
    }

    public CandidateScorecard evaluate(EvaluationRequest request) {
        String transcript = request.transcript() != null ? request.transcript() : "";

        double technicalScore = assessTechnicalProficiency(transcript);
        double communicationScore = assessCommunicationSkills(transcript);
        List<String> redFlags = detectRedFlags(transcript);
        String recommendation = generateRecommendation(technicalScore, communicationScore, redFlags);
        String summary = buildEvaluationSummary(technicalScore, communicationScore, redFlags, recommendation);

        Evaluation entity = new Evaluation();
        entity.setCandidateId(request.candidateId());
        entity.setJdId(request.jdId());
        entity.setInterviewId(request.interviewId());
        entity.setTechnicalProficiency(technicalScore);
        entity.setCommunicationSkills(communicationScore);
        entity.setRedFlags(String.join("||", redFlags));
        entity.setRecommendation(recommendation);
        entity.setEvaluationSummary(summary);

        Evaluation saved = repository.save(entity);

        return new CandidateScorecard(
            saved.getCandidateId(),
            saved.getCandidateName(),
            saved.getJdId(),
            saved.getTechnicalProficiency(),
            saved.getCommunicationSkills(),
            redFlags,
            saved.getRecommendation(),
            saved.getEvaluationSummary()
        );
    }

    public Optional<CandidateScorecard> getEvaluation(String id) {
        return repository.findById(id).map(this::toScorecard);
    }

    public List<CandidateScorecard> getShortlist(String jdId, int topN) {
        return repository.findByJdIdOrderByTechnicalProficiencyDesc(jdId).stream()
            .limit(topN)
            .map(this::toScorecard)
            .toList();
    }

    public List<CandidateScorecard> getEvaluationsByJdId(String jdId) {
        return repository.findByJdId(jdId).stream().map(this::toScorecard).toList();
    }

    double assessTechnicalProficiency(String transcript) {
        if (transcript == null || transcript.isBlank()) return 0.0;

        double score = 30.0; // Base score for providing answers
        String lower = transcript.toLowerCase();

        // Technical depth indicators
        List<String> technicalTerms = List.of(
            "algorithm", "data structure", "complexity", "design pattern",
            "microservices", "api", "database", "scalability", "performance",
            "thread", "concurrency", "memory", "cache", "security",
            "testing", "deployment", "ci/cd", "architecture", "framework"
        );
        long techTermCount = technicalTerms.stream().filter(lower::contains).count();
        score += Math.min(40.0, techTermCount * 5.0);

        // Answer depth (word count as proxy)
        int wordCount = transcript.split("\\s+").length;
        if (wordCount > 500) score += 20.0;
        else if (wordCount > 200) score += 15.0;
        else if (wordCount > 100) score += 10.0;

        // Code-related terms
        if (lower.contains("implement") || lower.contains("code") || lower.contains("function")) {
            score += 10.0;
        }

        return Math.min(100.0, score);
    }

    double assessCommunicationSkills(String transcript) {
        if (transcript == null || transcript.isBlank()) return 0.0;

        double score = 40.0; // Base score
        String[] sentences = transcript.split("[.!?]+");

        // Sentence structure
        if (sentences.length > 5) score += 15.0;
        if (sentences.length > 10) score += 10.0;

        // Average sentence length (good communication = varied but clear sentences)
        double avgLength = Arrays.stream(sentences)
            .mapToInt(s -> s.trim().split("\\s+").length)
            .average()
            .orElse(0);
        if (avgLength > 5 && avgLength < 25) score += 15.0;

        // Structured response indicators
        String lower = transcript.toLowerCase();
        if (lower.contains("first") || lower.contains("second") || lower.contains("finally")) score += 10.0;
        if (lower.contains("for example") || lower.contains("for instance")) score += 10.0;

        return Math.min(100.0, score);
    }

    List<String> detectRedFlags(String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return List.of("No transcript provided - unable to evaluate");
        }

        List<String> flags = new ArrayList<>();
        String lower = transcript.toLowerCase();

        // Vague answers
        long vagueCount = Pattern.compile("\\b(i think|maybe|i guess|not sure|i don't know)\\b")
            .matcher(lower).results().count();
        if (vagueCount > 3) {
            flags.add("Excessive uncertainty in responses (" + vagueCount + " vague phrases detected)");
        }

        // Very short answers
        int wordCount = transcript.split("\\s+").length;
        if (wordCount < 50) {
            flags.add("Very short responses - possible lack of depth or engagement");
        }

        // Check for frequent self-corrections
        long iMeanCount = Pattern.compile("\\bi mean\\b").matcher(lower).results().count();
        if (iMeanCount > 3) {
            flags.add("Frequent self-corrections may indicate inconsistency");
        }

        // Negative sentiment
        List<String> negativeTerms = List.of("hate", "terrible", "worst", "awful", "never works");
        long negativeCount = negativeTerms.stream().filter(lower::contains).count();
        if (negativeCount > 2) {
            flags.add("Negative sentiment detected in responses");
        }

        return flags;
    }

    String generateRecommendation(double technicalScore, double communicationScore, List<String> redFlags) {
        double overallScore = (technicalScore * 0.6) + (communicationScore * 0.4);
        int flagPenalty = redFlags.size() * 5;
        double adjustedScore = Math.max(0, overallScore - flagPenalty);

        if (adjustedScore >= 75) return "STRONG_HIRE";
        if (adjustedScore >= 60) return "HIRE";
        if (adjustedScore >= 45) return "MAYBE";
        return "NO_HIRE";
    }

    private String buildEvaluationSummary(double technical, double communication,
                                          List<String> redFlags, String recommendation) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Candidate Evaluation Summary ===\n\n");
        sb.append("Technical Proficiency: ").append(String.format("%.1f/100", technical)).append("\n");
        sb.append("Communication Skills: ").append(String.format("%.1f/100", communication)).append("\n\n");

        if (!redFlags.isEmpty()) {
            sb.append("Red Flags:\n");
            redFlags.forEach(flag -> sb.append("  ⚠ ").append(flag).append("\n"));
            sb.append("\n");
        }

        sb.append("Recommendation: ").append(recommendation).append("\n");
        return sb.toString();
    }

    private CandidateScorecard toScorecard(Evaluation e) {
        List<String> redFlags = e.getRedFlags() != null && !e.getRedFlags().isEmpty()
            ? Arrays.asList(e.getRedFlags().split("\\|\\|"))
            : List.of();
        return new CandidateScorecard(
            e.getCandidateId(), e.getCandidateName(), e.getJdId(),
            e.getTechnicalProficiency(), e.getCommunicationSkills(),
            redFlags, e.getRecommendation(), e.getEvaluationSummary()
        );
    }
}
