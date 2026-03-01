# TalentFlow AI

**AI-Powered End-to-End Talent Acquisition Platform**

TalentFlow AI automates the recruitment lifecycle—from job description generation to candidate evaluation—using AI-driven microservices built with Java 17 and Spring Boot 3.x.

## Architecture

The platform follows a microservices architecture with an API Gateway routing requests to specialized services:

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │   (Port 8080)   │
                    └────────┬────────┘
            ┌────────────────┼────────────────┐────────────────┐
            ▼                ▼                ▼                ▼
   ┌────────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
   │  JD Generator  │ │ Resume Parser│ │  Interview   │ │  Evaluation  │
   │  Service       │ │ & Matcher    │ │  Service     │ │  Service     │
   │  (Port 8081)   │ │ (Port 8082)  │ │ (Port 8083)  │ │ (Port 8084)  │
   └────────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

## Modules

### Module A: JD Generator Service (`jd-generator-service`)
AI-powered Job Description generator that creates comprehensive JDs from basic parameters.
- **Input**: Role, Years of Experience, Primary/Secondary Tech Stack, Project Domain
- **Output**: Structured job description with qualifications, responsibilities, and benefits
- **API**: `POST /api/v1/job-descriptions`, `GET /api/v1/job-descriptions/{id}`, `GET /api/v1/job-descriptions`

### Module B: Resume Parser & Matching Engine (`resume-parser-service`)
Smart resume parser with semantic matching capabilities.
- **Input**: PDF/Docx resume uploads (single or bulk)
- **Features**: Name, contact, skills, experience, and education extraction; Semantic skill matching with synonym support
- **Output**: Match Score (0–100%) for candidates against JDs
- **API**: `POST /api/v1/resumes/upload`, `POST /api/v1/resumes/upload/bulk`, `POST /api/v1/resumes/match`, `GET /api/v1/resumes/{id}`

### Module C: Interview Service (`interview-service`)
AI-driven interviewer with dynamic question generation.
- **Features**: Tech-stack-specific technical questions (Java, Spring Boot, Microservices, Kubernetes); Behavioral questions; Interview lifecycle management (schedule → start → submit transcript)
- **API**: `POST /api/v1/interviews`, `POST /api/v1/interviews/{id}/start`, `POST /api/v1/interviews/{id}/submit`, `GET /api/v1/interviews/{id}`

### Module D: Evaluation Service (`evaluation-service`)
Automated candidate evaluation and shortlist reporting.
- **Features**: Technical proficiency assessment; Communication skills rating; Red flag detection; Hire/No-Hire recommendation
- **Output**: Candidate Scorecard with detailed breakdown
- **API**: `POST /api/v1/evaluations`, `GET /api/v1/evaluations/{id}`, `GET /api/v1/evaluations/shortlist/{jdId}?topN=5`

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 (Microservices) |
| API Gateway | Spring Cloud Gateway |
| Database | PostgreSQL (structured data), H2 (development) |
| AI/ML Integration | LangChain4j 0.30.0 |
| Resume Parsing | Apache PDFBox 3.0.2 |
| Event Streaming | Apache Kafka |
| Orchestration | Docker / Kubernetes |
| Build Tool | Maven |

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for infrastructure services)

### Build
```bash
mvn clean install
```

### Run with Docker Compose
```bash
# Start infrastructure (PostgreSQL, MongoDB, Kafka) and all services
docker-compose up -d
```

### Run Individual Services (Development)
```bash
# Each service uses H2 in-memory database by default
cd jd-generator-service && mvn spring-boot:run
cd resume-parser-service && mvn spring-boot:run
cd interview-service && mvn spring-boot:run
cd evaluation-service && mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

## Project Structure

```
talentflow-ai/
├── pom.xml                      # Parent POM
├── common/                      # Shared DTOs and models
├── jd-generator-service/        # Module A: JD Generation
├── resume-parser-service/       # Module B: Resume Parsing & Matching
├── interview-service/           # Module C: AI Interviewer
├── evaluation-service/          # Module D: Evaluation & Reporting
├── api-gateway/                 # API Gateway
├── docker-compose.yml           # Docker orchestration
└── Dockerfile                   # Multi-stage build
```

## Success Metrics
- **Zero Manual Screening**: TA team sees only Top 5 candidates per role
- **Consistency**: 90% agreement between AI evaluation and human interview results
- **Automated Updates**: Candidate status tracking at every stage