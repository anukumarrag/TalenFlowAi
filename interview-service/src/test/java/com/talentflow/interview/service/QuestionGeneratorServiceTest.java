package com.talentflow.interview.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGeneratorServiceTest {

    private QuestionGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new QuestionGeneratorService();
    }

    @Test
    void generateQuestions_ShouldReturnNonEmptyList() {
        List<String> questions = service.generateQuestions("technical", "java");
        assertThat(questions).isNotEmpty();
    }

    @Test
    void generateQuestions_ShouldIncludeBehavioralQuestions() {
        List<String> questions = service.generateQuestions("technical", "java");
        assertThat(questions.size()).isGreaterThanOrEqualTo(4);
    }

    @Test
    void generateQuestions_ShouldHandleNullTechStack() {
        List<String> questions = service.generateQuestions("general", null);
        assertThat(questions).isNotEmpty();
        assertThat(questions.size()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void generateQuestions_ShouldHandleMultipleTechs() {
        List<String> questions = service.generateQuestions("technical", "java,spring boot");
        assertThat(questions.size()).isGreaterThanOrEqualTo(4);
    }

    @Test
    void selectRandom_ShouldReturnRequestedCount() {
        List<String> source = List.of("a", "b", "c", "d", "e");
        List<String> result = service.selectRandom(source, 3);
        assertThat(result).hasSize(3);
    }

    @Test
    void selectRandom_ShouldReturnEmptyForNullSource() {
        List<String> result = service.selectRandom(null, 3);
        assertThat(result).isEmpty();
    }

    @Test
    void selectRandom_ShouldNotExceedSourceSize() {
        List<String> source = List.of("a", "b");
        List<String> result = service.selectRandom(source, 5);
        assertThat(result).hasSize(2);
    }
}
