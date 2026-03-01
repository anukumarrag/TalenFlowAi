package com.talentflow.resumeparser.service;

import com.talentflow.common.dto.ResumeUploadResponse;
import com.talentflow.common.model.ResumeData;
import com.talentflow.resumeparser.entity.Resume;
import com.talentflow.resumeparser.repository.ResumeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeParserService {

    private final ResumeRepository repository;

    public ResumeParserService(ResumeRepository repository) {
        this.repository = repository;
    }

    public ResumeUploadResponse parseAndStore(MultipartFile file) throws IOException {
        String rawText = new String(file.getBytes(), StandardCharsets.UTF_8);
        ResumeData parsed = extractResumeData(rawText);

        Resume entity = new Resume();
        entity.setCandidateName(parsed.candidateName());
        entity.setEmail(parsed.email());
        entity.setPhone(parsed.phone());
        entity.setSkills(String.join(", ", parsed.skills()));
        entity.setYearsOfExperience(parsed.yearsOfExperience());
        entity.setEducation(parsed.education());
        entity.setRawText(rawText);
        entity.setFileName(file.getOriginalFilename());

        Resume saved = repository.save(entity);

        return new ResumeUploadResponse(
            saved.getId(),
            saved.getCandidateName(),
            saved.getStatus(),
            "Resume parsed and stored successfully"
        );
    }

    public List<ResumeUploadResponse> bulkParse(MultipartFile[] files) throws IOException {
        List<ResumeUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            responses.add(parseAndStore(file));
        }
        return responses;
    }

    public Optional<ResumeData> getResume(String id) {
        return repository.findById(id).map(this::toResumeData);
    }

    public List<ResumeData> getAllResumes() {
        return repository.findAll().stream().map(this::toResumeData).toList();
    }

    ResumeData extractResumeData(String rawText) {
        String name = extractName(rawText);
        String email = extractEmail(rawText);
        String phone = extractPhone(rawText);
        List<String> skills = extractSkills(rawText);
        int experience = extractYearsOfExperience(rawText);
        String education = extractEducation(rawText);

        return new ResumeData(null, name, email, phone, skills, experience, education, rawText);
    }

    private ResumeData toResumeData(Resume r) {
        return new ResumeData(
            r.getId(), r.getCandidateName(), r.getEmail(), r.getPhone(),
            r.getSkills() != null ? Arrays.asList(r.getSkills().split(",\\s*")) : List.of(),
            r.getYearsOfExperience(), r.getEducation(), r.getRawText()
        );
    }

    String extractName(String text) {
        String[] lines = text.strip().split("\\n");
        for (String line : lines) {
            String trimmed = line.strip();
            if (!trimmed.isEmpty() && !trimmed.contains("@") && !trimmed.matches(".*\\d{3}.*")) {
                return trimmed;
            }
        }
        return "Unknown";
    }

    String extractEmail(String text) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "";
    }

    String extractPhone(String text) {
        Pattern pattern = Pattern.compile("\\+?[\\d\\s()-]{10,}");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group().strip() : "";
    }

    List<String> extractSkills(String text) {
        List<String> knownSkills = List.of(
            "Java", "Spring Boot", "Kubernetes", "Docker", "Python",
            "JavaScript", "TypeScript", "React", "Angular", "AWS",
            "Azure", "GCP", "PostgreSQL", "MongoDB", "Redis",
            "Kafka", "Microservices", "REST", "GraphQL", "SQL",
            "Git", "CI/CD", "Agile", "Scrum", "Machine Learning"
        );
        List<String> found = new ArrayList<>();
        String lowerText = text.toLowerCase();
        for (String skill : knownSkills) {
            if (lowerText.contains(skill.toLowerCase())) {
                found.add(skill);
            }
        }
        return found;
    }

    int extractYearsOfExperience(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\+?\\s*(?:years?|yrs?)\\s*(?:of)?\\s*(?:experience)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        int maxYears = 0;
        while (matcher.find()) {
            int years = Integer.parseInt(matcher.group(1));
            maxYears = Math.max(maxYears, years);
        }
        return maxYears;
    }

    String extractEducation(String text) {
        List<String> degrees = List.of(
            "Ph.D", "PhD", "Master", "M.S.", "M.Tech", "MBA",
            "Bachelor", "B.S.", "B.Tech", "B.E.", "B.Sc"
        );
        String lowerText = text.toLowerCase();
        for (String degree : degrees) {
            if (lowerText.contains(degree.toLowerCase())) {
                return degree;
            }
        }
        return "Not specified";
    }
}
