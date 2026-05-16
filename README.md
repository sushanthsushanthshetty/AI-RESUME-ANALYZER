# APEX — AI-Powered Resume Analyzer 🚀

APEX is a premium Java-based web application designed to provide high-fidelity resume analysis and personalized career insights. By leveraging advanced reasoning models, APEX helps candidates bridge the gap between their current profile and their dream role through brutally honest feedback, skill gap analysis, and automated job discovery.

## 🛠 Technology Stack

### Backend & Infrastructure
- **Language**: Java 17 (Eclipse Temurin)
- **Framework**: Java Servlet API 4.0.1
- **Build System**: Maven 3.x
- **Web Server**: Apache Tomcat 7/9
- **Containerization**: Docker (Multi-stage builds)
- **AI Engine**: **Mercury 2** Reasoning Model (via Inception Labs API)
- **Data Persistence**: XML-based Local Store (Custom No-SQL implementation)

### Libraries & Integration
- **Apache PDFBox 3.0.1**: High-accuracy text extraction from PDF resumes.
- **Jackson Databind 2.16.1**: Robust JSON processing for AI communication and data modeling.
- **Jsoup 1.17.2**: Advanced web scraping for live job recommendations from LinkedIn and Naukri.
- **JSTL 1.2**: JSP Standard Tag Library for dynamic UI rendering.
- **JUnit 5**: Comprehensive unit testing suite.

### Frontend & UI/UX
- **Structure**: Semantic HTML5 & JSP
- **Styling**: Vanilla CSS3 (Custom Dark-Mode Design System)
- **Typography**: **Inter** (via Google Fonts)
- **Icons**: **Tabler Icons** (Web-font)
- **Interactions**: Vanilla JavaScript (ES6+) with zero external dependencies.

---

## 🔑 API Keys & Configuration

The application requires an API key from Inception Labs to power its reasoning engine.

### Required Key
- **`INCEPTION_API_KEY`**: Used for resume scoring, skill gap detection, and roadmap generation.
  - **Current Key**: `sk_50121b26bcad390b8f436282ac5a9816`
  - **Endpoint**: `https://api.inceptionlabs.ai/v1/chat/completions`
  - **Model**: `mercury-2`

### Configuration Methods
1. **Properties File**: Set in `src/main/resources/config.properties`:
   ```properties
   INCEPTION_API_KEY=sk_50121b26bcad390b8f436282ac5a9816
   ```
2. **Environment Variable**: Set `INCEPTION_API_KEY` in your OS environment.
3. **Maven Plugin**: Configured in `pom.xml` under `tomcat7-maven-plugin` system properties.

---

## ✨ Core Features

1. **Mercury 2 Analysis Engine**: Performs deep reasoning on resume content against target job descriptions.
2. **Domain Guard Protocol**: A validation layer that detects domain mismatches and enforces scoring accuracy.
3. **Personalized Skill Roadmap**: Generates a step-by-step learning path based on identified skill gaps.
4. **Live Job Scraper**: Scrapes real-time listings from major job boards with intelligent fallback search generators.
5. **Analytics Dashboard**: Tracks performance history and provides career-growth statistics.

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Maven 3.8+
- Docker (Optional)

### Local Development
1. Clone the repository.
2. Ensure your API key is set in `config.properties`.
3. Run the application:
   ```bash
   mvn tomcat7:run
   ```
4. Access at: `http://localhost:8080/ai-resume-analyzer`

### Docker Deployment
```bash
docker build -t apex-analyzer .
docker run -p 8080:8080 -e INCEPTION_API_KEY=sk_50121b26bcad390b8f436282ac5a9816 apex-analyzer
```

---
*Developed as a Java Mini Project for AI-Powered Career Intelligence.*
