# APEX — AI-Powered Resume Analyzer 🚀

APEX is a high-performance Java web application designed to provide "brutally honest" resume feedback and live job recommendations. It uses advanced AI to screen resumes against target roles, identify skill gaps, and suggest actionable improvements to pass ATS filters.

## 🛠 Technology Stack

- **Backend**: Java 17, Java Servlet API 4.0
- **Build Tool**: Maven 3.x
- **Web Server**: Apache Tomcat 7 (via Maven Plugin)
- **AI Engine**: Mercury 2 (via Inception API)
- **Persistence**: XML-based Local Storage (No-SQL style)
- **Libraries**:
  - **Apache PDFBox**: High-accuracy PDF text extraction
  - **Jackson Databind**: JSON parsing and object mapping
  - **Jsoup**: Web scraping and job data extraction
  - **JSTL**: JSP standard tag library for dynamic UI rendering
- **Frontend**: Vanilla HTML5, CSS3 (Modern Dark-Mode UI with Inter typography)

## ✨ Core Features

### 1. AI Analysis Engine
- **Brutally Honest Scoring**: Calibrated 0-100 scoring based on industry standards.
- **Domain Guard**: A hard-coded validation layer that detects domain mismatches (e.g., a Backend Dev applying for an AI/ML role) and overrides scores for accuracy.
- **Skill Gap Detection**: Categorizes missing skills as Critical, Major, or Minor.
- **ATS Keyword Identification**: Lists specific words missing from the resume that are present in the Job Description.

### 2. Smart Job Recommendations
- **Automated Fetching**: Extracts location and experience from the resume to find relevant jobs.
- **Multi-Source Scraping**: Scrapes LinkedIn and Naukri for live job openings.
- **Fallback Logic**: If live scraping is blocked, it generates optimized search URLs for LinkedIn, Naukri, and Google Jobs so the user always has a path forward.

### 3. Analytics Dashboard
- **Performance History**: Tracks previous analyses with timestamps and scores.
- **Interactive UI**: Features score circles, severity badges, and actionable suggestion boxes.
- **Export to XML**: Allows users to download their analysis results in structured XML format.

## 📂 Project Structure

```text
ai-resume-analyzer/
├── src/main/java/com/resumeanalyzer/
│   ├── ai/          # AI Client, Prompt Builder, Domain Guard
│   ├── model/       # Data models (AnalysisResult, JobListing, etc.)
│   ├── parser/      # Resume parsing logic (PDFBox)
│   ├── service/     # Job Scraper Service
│   ├── servlet/     # Controller layer (Upload, Result, Dashboard)
│   ├── util/        # Config and Utility managers
│   └── xml/         # XML Persistence Layer
├── src/main/webapp/
│   ├── WEB-INF/     # Views (JSP) and web configuration
│   └── css/         # Global styles
├── pom.xml          # Project dependencies and plugins
└── README.md        # This file
```

## 🚀 Getting Started

### Prerequisites
- Java 17 installed
- Maven installed

### Setup
1. **Clone the repository**.
2. **Configure API Key**:
   - Open `src/main/resources/config.properties`.
   - Add your API Key: `INCEPTION_API_KEY=your_key_here`.
3. **Run the Application**:
   ```bash
   mvn tomcat7:run
   ```
4. **Access the App**:
   - Open `http://localhost:8080/ai-resume-analyzer` in your browser.

## 🧠 Implementation Details

### Persistence Layer
The project uses a custom XML persistence layer (`ResultXmlManager`) that stores user analyses in `src/main/resources/results/`. This allows for a lightweight, zero-configuration database that is easy to manage and inspect.

### Scraping Logic
The `JobScraperService` uses Jsoup to connect to job boards. It implements a **Headless User Agent** and **Timeout Management** to maximize success rates. When direct scraping fails, it transitions to a **Search Link Generator** to maintain a positive user experience.

### Domain Guard Protocol
The `DomainGuard` class ensures that the AI doesn't "hallucinate" high scores for unqualified candidates. It checks for core skills (e.g., Python/TensorFlow for AI roles) and enforces a maximum score of 30 if critical domain skills are missing.

---
*Created as part of a Java Mini Project.*
