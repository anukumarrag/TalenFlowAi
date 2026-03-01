package com.talentflow.resumeparser.controller;

import com.talentflow.common.dto.MatchResult;
import com.talentflow.common.dto.ResumeUploadResponse;
import com.talentflow.common.model.ResumeData;
import com.talentflow.resumeparser.service.MatchingService;
import com.talentflow.resumeparser.service.ResumeParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeParserService parserService;
    private final MatchingService matchingService;

    public ResumeController(ResumeParserService parserService, MatchingService matchingService) {
        this.parserService = parserService;
        this.matchingService = matchingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponse> uploadResume(@RequestParam("file") MultipartFile file) throws IOException {
        ResumeUploadResponse response = parserService.parseAndStore(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/upload/bulk")
    public ResponseEntity<List<ResumeUploadResponse>> bulkUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<ResumeUploadResponse> responses = parserService.bulkParse(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeData> getResume(@PathVariable String id) {
        return parserService.getResume(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ResumeData>> getAllResumes() {
        return ResponseEntity.ok(parserService.getAllResumes());
    }

    @PostMapping("/match")
    public ResponseEntity<List<MatchResult>> matchCandidates(
            @RequestParam String jdId,
            @RequestBody String jdText) {
        List<ResumeData> resumes = parserService.getAllResumes();
        List<MatchResult> ranked = matchingService.rankCandidates(resumes, jdText, jdId);
        return ResponseEntity.ok(ranked);
    }
}
