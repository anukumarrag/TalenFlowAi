package com.talentflow.resumeparser.service;

import com.talentflow.common.model.ResumeData;
import com.talentflow.resumeparser.repository.ResumeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResumeParserServiceTest {

    @Mock
    private ResumeRepository repository;

    @InjectMocks
    private ResumeParserService service;

    @Test
    void extractResumeData_ShouldExtractName() {
        String resumeText = "John Smith\njohn@example.com\n+1 555-123-4567\n5 years of experience in Java";
        ResumeData data = service.extractResumeData(resumeText);
        assertThat(data.candidateName()).isEqualTo("John Smith");
    }

    @Test
    void extractResumeData_ShouldExtractEmail() {
        String resumeText = "Jane Doe\njane.doe@company.com\nSkills: Java, Python";
        ResumeData data = service.extractResumeData(resumeText);
        assertThat(data.email()).isEqualTo("jane.doe@company.com");
    }

    @Test
    void extractResumeData_ShouldExtractSkills() {
        String resumeText = "Dev Name\nExpert in Java, Spring Boot, Docker, and Kubernetes\nAlso knows Python and React";
        ResumeData data = service.extractResumeData(resumeText);
        assertThat(data.skills()).contains("Java", "Spring Boot", "Docker", "Kubernetes", "Python", "React");
    }

    @Test
    void extractResumeData_ShouldExtractExperience() {
        String resumeText = "Senior Dev\n8 years of experience in software development";
        ResumeData data = service.extractResumeData(resumeText);
        assertThat(data.yearsOfExperience()).isEqualTo(8);
    }

    @Test
    void extractResumeData_ShouldExtractEducation() {
        String resumeText = "Dev\nB.Tech in Computer Science from MIT";
        ResumeData data = service.extractResumeData(resumeText);
        assertThat(data.education()).isEqualTo("B.Tech");
    }

    @Test
    void extractEmail_ShouldReturnEmpty_WhenNoEmail() {
        String resumeText = "No email here, just a name";
        assertThat(service.extractEmail(resumeText)).isEmpty();
    }

    @Test
    void extractYearsOfExperience_ShouldReturn0_WhenNoExperience() {
        String text = "A fresh graduate with no work history";
        assertThat(service.extractYearsOfExperience(text)).isZero();
    }

    @Test
    void extractSkills_ShouldReturnEmpty_WhenNoMatchingSkills() {
        String text = "I know nothing about technology";
        assertThat(service.extractSkills(text)).isEmpty();
    }
}
