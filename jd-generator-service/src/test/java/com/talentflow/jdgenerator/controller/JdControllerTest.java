package com.talentflow.jdgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentflow.common.dto.JdRequest;
import com.talentflow.common.dto.JdResponse;
import com.talentflow.jdgenerator.service.JdGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JdController.class)
class JdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JdGeneratorService jdGeneratorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generateJobDescription_ShouldReturn201() throws Exception {
        JdRequest request = new JdRequest("Java Developer", 3, "Java", List.of("Spring"), "Banking");
        JdResponse response = new JdResponse("id-1", "Java Developer", "Job description", "2024-01-01T00:00:00");

        when(jdGeneratorService.generateJobDescription(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/job-descriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("id-1"))
            .andExpect(jsonPath("$.role").value("Java Developer"));
    }

    @Test
    void getJobDescription_ShouldReturn404_WhenNotFound() throws Exception {
        when(jdGeneratorService.getJobDescription("nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/job-descriptions/nonexistent"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getJobDescription_ShouldReturn200_WhenFound() throws Exception {
        JdResponse response = new JdResponse("id-1", "Java Developer", "Desc", "2024-01-01T00:00:00");
        when(jdGeneratorService.getJobDescription("id-1")).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/v1/job-descriptions/id-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("Java Developer"));
    }
}
