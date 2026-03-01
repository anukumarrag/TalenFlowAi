package com.talentflow.jdgenerator.controller;

import com.talentflow.common.dto.JdRequest;
import com.talentflow.common.dto.JdResponse;
import com.talentflow.jdgenerator.service.JdGeneratorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-descriptions")
public class JdController {

    private final JdGeneratorService jdGeneratorService;

    public JdController(JdGeneratorService jdGeneratorService) {
        this.jdGeneratorService = jdGeneratorService;
    }

    @PostMapping
    public ResponseEntity<JdResponse> generateJobDescription(@Valid @RequestBody JdRequest request) {
        JdResponse response = jdGeneratorService.generateJobDescription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JdResponse> getJobDescription(@PathVariable String id) {
        return jdGeneratorService.getJobDescription(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<JdResponse>> getAllJobDescriptions() {
        return ResponseEntity.ok(jdGeneratorService.getAllJobDescriptions());
    }
}
