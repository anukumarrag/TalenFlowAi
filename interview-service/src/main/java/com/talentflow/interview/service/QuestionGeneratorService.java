package com.talentflow.interview.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuestionGeneratorService {

    private static final Map<String, List<String>> TECHNICAL_QUESTIONS = new LinkedHashMap<>();
    private static final List<String> BEHAVIORAL_QUESTIONS = List.of(
        "Tell me about a challenging project you worked on and how you overcame obstacles.",
        "Describe a situation where you had to work with a difficult team member.",
        "How do you prioritize tasks when working on multiple projects simultaneously?",
        "Give an example of a time you had to learn a new technology quickly.",
        "Describe your approach to debugging a complex production issue."
    );

    static {
        TECHNICAL_QUESTIONS.put("java", List.of(
            "Explain the difference between HashMap and ConcurrentHashMap in Java.",
            "What are the key features introduced in Java 21?",
            "How does the Java Memory Model handle happens-before relationships?",
            "Explain the concept of virtual threads (Project Loom) and their benefits.",
            "What are sealed classes and how do they improve type safety?",
            "Describe the difference between CompletableFuture and traditional Future.",
            "How would you implement a thread-safe singleton in Java?",
            "Explain garbage collection strategies in modern JVMs."
        ));

        TECHNICAL_QUESTIONS.put("spring boot", List.of(
            "Explain the auto-configuration mechanism in Spring Boot.",
            "How does Spring Boot handle dependency injection?",
            "What is the difference between @Component, @Service, and @Repository annotations?",
            "How would you implement circuit breaker pattern in a Spring Boot microservice?",
            "Explain Spring Boot Actuator and its production-ready features.",
            "How does Spring Security handle authentication and authorization?"
        ));

        TECHNICAL_QUESTIONS.put("microservices", List.of(
            "What are the key principles of microservices architecture?",
            "How would you handle distributed transactions across microservices?",
            "Explain the saga pattern and when you would use it.",
            "How do you implement service discovery in a microservices architecture?",
            "What strategies would you use for inter-service communication?",
            "How do you handle data consistency in a distributed system?"
        ));

        TECHNICAL_QUESTIONS.put("kubernetes", List.of(
            "Explain the difference between a Pod, Deployment, and StatefulSet.",
            "How does Kubernetes handle service discovery and load balancing?",
            "What are readiness and liveness probes?",
            "How would you implement horizontal pod autoscaling?",
            "Explain the concept of Kubernetes namespaces and resource quotas."
        ));

        TECHNICAL_QUESTIONS.put("default", List.of(
            "Describe your experience with version control systems like Git.",
            "How do you approach writing unit tests for your code?",
            "Explain your understanding of CI/CD pipelines.",
            "What design patterns have you used in your projects?",
            "How do you ensure code quality in your development process?"
        ));
    }

    public List<String> generateQuestions(String interviewType, String techStack) {
        List<String> questions = new ArrayList<>();

        // Add technical questions based on tech stack
        if (techStack != null) {
            String[] techs = techStack.toLowerCase().split("[,;\\s]+");
            for (String tech : techs) {
                String trimmedTech = tech.trim();
                List<String> techQuestions = TECHNICAL_QUESTIONS.getOrDefault(trimmedTech, List.of());
                questions.addAll(selectRandom(techQuestions, 2));
            }
        }

        // Add default technical questions if not enough
        if (questions.size() < 3) {
            questions.addAll(selectRandom(TECHNICAL_QUESTIONS.get("default"), 3 - questions.size()));
        }

        // Add behavioral questions
        questions.addAll(selectRandom(BEHAVIORAL_QUESTIONS, 2));

        return questions;
    }

    List<String> selectRandom(List<String> source, int count) {
        if (source == null || source.isEmpty()) return List.of();
        List<String> shuffled = new ArrayList<>(source);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }
}
