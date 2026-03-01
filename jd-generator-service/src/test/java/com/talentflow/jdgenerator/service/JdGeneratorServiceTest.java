package com.talentflow.jdgenerator.service;

import com.talentflow.common.dto.JdRequest;
import com.talentflow.common.dto.JdResponse;
import com.talentflow.jdgenerator.entity.JobDescription;
import com.talentflow.jdgenerator.repository.JobDescriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdGeneratorServiceTest {

    @Mock
    private JobDescriptionRepository repository;

    @InjectMocks
    private JdGeneratorService service;

    private JdRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new JdRequest(
            "Senior Java Developer",
            5,
            "Java",
            List.of("Spring Boot", "Kubernetes"),
            "FinTech"
        );
    }

    @Test
    void generateJobDescription_ShouldReturnValidResponse() {
        JobDescription saved = new JobDescription();
        saved.setId("test-id-123");
        saved.setRole("Senior Java Developer");
        saved.setGeneratedDescription("Generated JD content");
        saved.setCreatedAt(LocalDateTime.now());

        when(repository.save(any(JobDescription.class))).thenReturn(saved);

        JdResponse response = service.generateJobDescription(validRequest);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("test-id-123");
        assertThat(response.role()).isEqualTo("Senior Java Developer");
        assertThat(response.jobDescription()).isNotBlank();
    }

    @Test
    void generateJobDescription_ShouldIncludeRoleInDescription() {
        JobDescription saved = new JobDescription();
        saved.setId("test-id-456");
        saved.setRole("Senior Java Developer");
        saved.setCreatedAt(LocalDateTime.now());

        when(repository.save(any(JobDescription.class))).thenAnswer(invocation -> {
            JobDescription arg = invocation.getArgument(0);
            saved.setGeneratedDescription(arg.getGeneratedDescription());
            return saved;
        });

        JdResponse response = service.generateJobDescription(validRequest);

        assertThat(response.jobDescription()).contains("Senior Java Developer");
        assertThat(response.jobDescription()).contains("Java");
        assertThat(response.jobDescription()).contains("5");
    }

    @Test
    void getJobDescription_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<JdResponse> result = service.getJobDescription("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void getJobDescription_ShouldReturnResponse_WhenFound() {
        JobDescription jd = new JobDescription();
        jd.setId("existing-id");
        jd.setRole("Backend Developer");
        jd.setGeneratedDescription("Some description");
        jd.setCreatedAt(LocalDateTime.now());

        when(repository.findById("existing-id")).thenReturn(Optional.of(jd));

        Optional<JdResponse> result = service.getJobDescription("existing-id");

        assertThat(result).isPresent();
        assertThat(result.get().role()).isEqualTo("Backend Developer");
    }

    @Test
    void getAllJobDescriptions_ShouldReturnList() {
        JobDescription jd1 = new JobDescription();
        jd1.setId("id-1");
        jd1.setRole("Role 1");
        jd1.setGeneratedDescription("Desc 1");
        jd1.setCreatedAt(LocalDateTime.now());

        JobDescription jd2 = new JobDescription();
        jd2.setId("id-2");
        jd2.setRole("Role 2");
        jd2.setGeneratedDescription("Desc 2");
        jd2.setCreatedAt(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(jd1, jd2));

        List<JdResponse> results = service.getAllJobDescriptions();

        assertThat(results).hasSize(2);
    }
}
