package com.talentflow.resumeparser.service;

import com.talentflow.common.dto.MatchResult;
import com.talentflow.common.model.ResumeData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchingService {

    private static final Map<String, Set<String>> SKILL_SYNONYMS = Map.of(
        "java", Set.of("java", "j2ee", "jdk", "jvm", "spring boot", "spring framework"),
        "javascript", Set.of("javascript", "js", "ecmascript", "node.js", "nodejs"),
        "microservices", Set.of("microservices", "micro-services", "distributed systems", "soa"),
        "cloud", Set.of("aws", "azure", "gcp", "cloud", "cloud computing"),
        "database", Set.of("sql", "postgresql", "mysql", "mongodb", "redis", "database")
    );

    public MatchResult calculateMatch(ResumeData resume, String jdText, String jdId) {
        double skillScore = calculateSkillMatch(resume.skills(), jdText);
        double experienceScore = calculateExperienceMatch(resume.yearsOfExperience(), jdText);
        double educationScore = calculateEducationMatch(resume.education(), jdText);
        double semanticScore = calculateSemanticSimilarity(resume.rawText(), jdText);

        double totalScore = (skillScore * 0.40) + (experienceScore * 0.20) +
                           (educationScore * 0.15) + (semanticScore * 0.25);
        totalScore = Math.round(totalScore * 100.0) / 100.0;

        String summary = buildMatchSummary(skillScore, experienceScore, educationScore, semanticScore);

        return new MatchResult(resume.id(), resume.candidateName(), jdId, totalScore, summary);
    }

    public List<MatchResult> rankCandidates(List<ResumeData> resumes, String jdText, String jdId) {
        return resumes.stream()
            .map(resume -> calculateMatch(resume, jdText, jdId))
            .sorted(Comparator.comparingDouble(MatchResult::matchScore).reversed())
            .toList();
    }

    double calculateSkillMatch(List<String> candidateSkills, String jdText) {
        if (candidateSkills == null || candidateSkills.isEmpty()) return 0.0;
        String lowerJd = jdText.toLowerCase();
        long matchedSkills = candidateSkills.stream()
            .filter(skill -> {
                String lowerSkill = skill.toLowerCase();
                if (lowerJd.contains(lowerSkill)) return true;
                return SKILL_SYNONYMS.values().stream()
                    .anyMatch(synonyms -> synonyms.contains(lowerSkill) &&
                        synonyms.stream().anyMatch(lowerJd::contains));
            })
            .count();
        return Math.min(100.0, (matchedSkills * 100.0 / candidateSkills.size()));
    }

    double calculateExperienceMatch(int candidateYears, String jdText) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "(\\d+)\\+?\\s*(?:years?|yrs?)", java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = pattern.matcher(jdText);
        int requiredYears = 0;
        while (matcher.find()) {
            requiredYears = Math.max(requiredYears, Integer.parseInt(matcher.group(1)));
        }
        // Default score when JD doesn't specify experience requirements
        if (requiredYears == 0) return 75.0;
        if (candidateYears >= requiredYears) return 100.0;
        if (candidateYears >= requiredYears - 1) return 80.0;
        return Math.max(0, (candidateYears * 100.0 / requiredYears));
    }

    double calculateEducationMatch(String education, String jdText) {
        if (education == null || education.equals("Not specified")) return 50.0;
        String lowerEdu = education.toLowerCase();
        if (lowerEdu.contains("phd") || lowerEdu.contains("ph.d")) return 100.0;
        if (lowerEdu.contains("master") || lowerEdu.contains("m.s.") || lowerEdu.contains("m.tech") || lowerEdu.contains("mba")) return 85.0;
        if (lowerEdu.contains("bachelor") || lowerEdu.contains("b.s.") || lowerEdu.contains("b.tech") || lowerEdu.contains("b.e.")) return 70.0;
        return 50.0;
    }

    double calculateSemanticSimilarity(String resumeText, String jdText) {
        if (resumeText == null || jdText == null) return 0.0;
        Set<String> resumeWords = new HashSet<>(Arrays.asList(resumeText.toLowerCase().split("\\W+")));
        Set<String> jdWords = new HashSet<>(Arrays.asList(jdText.toLowerCase().split("\\W+")));
        Set<String> intersection = new HashSet<>(resumeWords);
        intersection.retainAll(jdWords);
        Set<String> union = new HashSet<>(resumeWords);
        union.addAll(jdWords);
        if (union.isEmpty()) return 0.0;
        return (intersection.size() * 100.0) / union.size();
    }

    private String buildMatchSummary(double skillScore, double experienceScore, double educationScore, double semanticScore) {
        StringBuilder sb = new StringBuilder();
        sb.append("Skills: ").append(String.format("%.0f%%", skillScore));
        sb.append(" | Experience: ").append(String.format("%.0f%%", experienceScore));
        sb.append(" | Education: ").append(String.format("%.0f%%", educationScore));
        sb.append(" | Relevance: ").append(String.format("%.0f%%", semanticScore));
        return sb.toString();
    }
}
