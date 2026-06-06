# APEX — AI-Powered Resume Analyzer 🚀

APEX is a premium Java-based web application that provides high-fidelity resume analysis, personalized career insights, skill roadmaps, and live job recommendations. Powered by the **Inception Mercury 2 reasoning engine**, APEX helps candidates bridge the gap between their current profile and their dream role through brutally honest AI feedback, domain mismatch detection, and automated job discovery.

---

## 🛠 Technology Stack

### Backend & Core
| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17 (Eclipse Temurin) | Primary language, servlets, business logic |
| **Java Servlet API** | 4.0.1 | HTTP request/response handling, form processing |
| **Apache Tomcat** | 9 / 7 (Maven plugin) | Web/application server |
| **Maven** | 3.x | Build automation, dependency management |
| **Docker** | Multi-stage | Containerized deployment |
| **Jackson Databind** | 2.16.1 | JSON serialization/deserialization (AI responses, data models) |
| **Apache PDFBox** | 3.0.1 | Text extraction from PDF resumes |
| **Jsoup** | 1.17.2 | Web scraping for live job listings |
| **JUnit 5** | 5.10.0 | Unit testing |

### AI / LLM Integration
| Component | Details |
|-----------|---------|
| **Model** | Mercury 2 (via Inception Labs API) |
| **Endpoint** | `https://api.inceptionlabs.ai/v1/chat/completions` |
| **Reasoning Levels** | `low` (fast), `medium` (balanced), `high` (deep analysis) |
| **Max Tokens** | 16,000 |
| **Temperature** | 0.75 |

### Frontend
- **HTML5 + JSP** — Server-rendered dynamic views
- **Vanilla CSS3** — Custom dark-mode design system with CSS variables
- **Google Fonts (Inter)** — Typography
- **Tabler Icons (CDN)** — Icon set
- **Vanilla JavaScript (ES6+)** — Drag & drop, form validation, filtering, modal controls (zero frameworks)

---

## 💾 Data Storage — XML-Based Persistence

APEX does **not** use a traditional SQL or NoSQL database. All data is persisted as **XML files** on the local filesystem. This keeps the project lightweight, portable, and zero-config.

### Storage Files & Locations

| File | Path | Purpose |
|------|------|---------|
| **users.xml** | `/users.xml` | User credentials, profile info (username, email, SHA-256 password hash) |
| **{userId}.xml** | `/results/{userId}.xml` | All analysis results per user (score, strengths, gaps, suggestions, etc.) |
| **{userId}.xml** | `~/apex-data/roadmaps/{userId}.xml` | Skill roadmaps per user (tasks, milestones, progress) |

### How Data is Structured

**users.xml:**
```xml
<users>
  <user>
    <id>uuid</id>
    <username>john_doe</username>
    <passwordHash>sha256hash</passwordHash>
    <email>john@example.com</email>
    <createdAt>2025-01-15T10:30:00</createdAt>
  </user>
</users>
```

**Analysis result (results/{userId}.xml):**
```xml
<analyses>
  <analysis id="uuid" timestamp="2025-01-15T10:30:00">
    <targetRole>Senior Java Developer</targetRole>
    <location>Bangalore</location>
    <yearsOfExperience>5</yearsOfExperience>
    <score>72</score>
    <confidence>high</confidence>
    <roleFit>Moderate</roleFit>
    <summary>...</summary>
    <strengths><strength>...</strength></strengths>
    <skillGaps>
      <gap severity="critical">
        <title>Kubernetes</title>
        <fix>Complete a hands-on 30-day project...</fix>
      </gap>
    </skillGaps>
    <keywordMisses><keyword>AWS Lambda</keyword></keywordMisses>
    <suggestions><suggestion>...</suggestion></suggestions>
    <interviewRisk>...</interviewRisk>
    <marketEdge>...</marketEdge>
    <roadmapId>uuid</roadmapId>
    <roadmapSavedAt>2025-01-15T12:00:00</roadmapSavedAt>
  </analysis>
</analyses>
```

**Roadmap (roadmaps/{userId}.xml):**
```xml
<roadmaps>
  <roadmap id="uuid" analysisId="uuid" status="active">
    <targetRole>Senior Java Developer</targetRole>
    <createdAt>...</createdAt>
    <updatedAt>...</updatedAt>
    <completionPercentage>45</completionPercentage>
    <tasks>
      <task id="uuid" isCompleted="false">
        <skill>Kubernetes</skill>
        <severity>critical</severity>
        <description>...</description>
        <action>...</action>
        <progressPercentage>50</progressPercentage>
        <milestones><milestone>Review docs</milestone></milestones>
        <completedMilestones><milestone>Review docs</milestone></completedMilestones>
      </task>
    </tasks>
  </roadmap>
</roadmaps>
```

### Password Security
- Passwords are hashed using **SHA-256** before storage
- Never stored in plain text

---

## 🤖 AI Integration — How Mercury 2 Works

### 1. Prompt Construction

The system builds two prompts:
- **System Prompt** (`PromptBuilder.getSystemPrompt()`) — Defines the AI's persona as a "brutally honest resume screening engine" with strict rules:
  - Domain mismatch detection (checks primary domain skills first)
  - 5-check scoring protocol (domain skills 35%, role title 20%, experience 15%, impact 20%, ATS keywords 10%)
  - JSON-only output contract (no markdown, no explanations)
  - Severity classification for skill gaps (critical/major/minor)

- **User Prompt** (`PromptBuilder.buildUserPrompt()`) — Contains:
  - Target role
  - Full job description
  - Extracted resume text
  - Step-by-step analysis protocol (8 steps)

### 2. API Call

```java
MercuryApiClient client = new MercuryApiClient(apiKey, reasoningEffort);
String rawResponse = client.sendPrompt(systemPrompt, userPrompt);
```

The client sends a POST request to `https://api.inceptionlabs.ai/v1/chat/completions` with:
- Model: `mercury-2`
- Max tokens: 16,000
- Temperature: 0.75
- Reasoning effort: user-configurable (low/medium/high)

### 3. Response Parsing

The raw JSON response from Mercury 2 is parsed into an `AnalysisResult` model:
```java
AnalysisResult result = mapJsonToResult(rawResponse);
```

The JSON is cleaned (fence removal, extraction from markdown) before parsing with Jackson.

### 4. Domain Guard Override

After AI returns a result, **DomainGuard.validate()** runs a secondary check:
- Maps the target role to a domain (AI/ML, Frontend, DevOps, Android, iOS, Data Engineering, etc.)
- Scans the resume for required domain skills
- If coverage is below 40%, it **overrides** the score (max 30) and sets role_fit to "Mismatch"
- This prevents false positives from the AI (e.g., a Java dev scoring 80 for an AI/ML role)

### 5. Roadmap Generation

When a user saves a roadmap:
1. `SaveRoadmapServlet` creates a basic roadmap from skill gaps
2. For enhanced roadmaps, a separate Mercury 2 call generates a full 12-week learning plan with:
   - Skill categorization (Critical/Major/Minor)
   - 3-phase learning pathway (Foundation → Hands-On → Mastery)
   - Week-by-week timeline
   - Resource recommendations with YouTube search links
   - Motivational metrics (salary growth, success probability)

---

## 🔄 How APEX Generates a Response — End-to-End Flow

```
User Uploads Resume + Target Role + Job Description
                │
                ▼
  ┌──────────────────────────────┐
  │   UploadServlet.doPost()     │
  └──────────────────────────────┘
                │
                ▼
  ┌──────────────────────────────┐
  │  ResumeParser.parse()        │
  │  • PDF → PDFBox text extract │
  │  • TXT → UTF-8 read          │
  └──────────────────────────────┘
                │
                ▼
  ┌──────────────────────────────┐
  │  PromptBuilder.build()       │
  │  • System prompt (persona)   │
  │  • User prompt (role + JD    │
  │    + resume + 8-step guide)  │
  └──────────────────────────────┘
                │
                ▼
  ┌──────────────────────────────┐
  │  MercuryApiClient.sendPrompt │
  │  • POST to Inception API     │
  │  • Mercury-2 reasoning       │
  │  • Returns JSON response     │
  └──────────────────────────────┘
                │
                ▼
  ┌──────────────────────────────┐
  │  DomainGuard.validate()      │
  │  • Check domain match        │
  │  • Override score if needed  │
  └──────────────────────────────┘
                │
                ▼
  ┌──────────────────────────────┐
  │  ResultXmlManager.saveResult │
  │  • Write to users XML file   │
  └──────────────────────────────┘
                │
                ▼
  ┌──────────────────────────────┐
  │  ResultServlet               │
  │  • Fetch AI result           │
  │  • Fetch live jobs via       │
  │    JobScraperService (Jsoup) │
  │  • Forward to result.jsp     │
  └──────────────────────────────┘
                │
                ▼
  ┌──────────────────────────────┐
  │  result.jsp rendered         │
  │  • Score, fit, strengths     │
  │  • Skill gaps, suggestions   │
  │  • ATS keyword misses        │
  │  • Roadmap CTA button        │
  │  • Live job listings grid    │
  └──────────────────────────────┘
```

---

## ✨ Core Features

### 1. Mercury 2 Analysis Engine
- Deep reasoning on resume vs. job description
- 5-check scoring protocol (domain skills 35%, title 20%, experience 15%, impact 20%, ATS keywords 10%)
- Returns: score, role fit, strengths, skill gaps, suggestions, ATS keyword misses, interview risk, market edge

### 2. Domain Guard Protocol
- Prevents AI from giving high scores to wrong-domain candidates
- Maps 10+ domains (AI/ML, Frontend, DevOps, Android, iOS, Data Engineering, Cybersecurity, Blockchain, etc.)
- Automatically detects current domain from resume text
- Hard caps mismatch scores at 30 and sets role_fit to "Mismatch"

### 3. Personalized Skill Roadmaps
- Generates a 12-week structured learning plan from identified skill gaps
- 3 phases: Foundation → Hands-On Practice → Mastery
- Each skill has milestones, resources (YouTube, courses, projects), and progress tracking
- Dashboard shows overall completion %, task count, and per-task progress

### 4. Live Job Scraper
- Fetches real-time job listings via Jsoup web scraping
- Sources: LinkedIn, Naukri (with Google fallback search generators)
- Filters by target role, location, and experience level
- Shows source badge, company, location, posted date, and apply link
- Graceful fallback: if scraping fails, generates Google search links

### 5. Analytics Dashboard
- Tracks total resumes analyzed, best score, average score, strong fit count
- Searchable/filterable analysis history table
- Roadmap management with progress bars
- Settings panel (display name, email, password, AI reasoning speed)

### 6. User Management
- Registration with SHA-256 password hashing
- Session-based authentication with 30-minute timeout
- Settings: update display name, email, password, AI reasoning effort
- Session filter protects all pages from unauthorized access

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- Maven 3.8+
- Docker (optional)

### 1. Get an API Key
1. Sign up at **[Inception Labs](https://inceptionlabs.ai)**
2. Generate an API key
3. Set it in `src/main/resources/config.properties`:
   ```properties
   INCEPTION_API_KEY=sk_your_key_here
   ```

### 2. Run Locally
```bash
cd ai-resume-analyzer
mvn clean tomcat7:run -DINCEPTION_API_KEY=sk_your_key_here
```
Access at: `http://localhost:8080/ai-resume-analyzer`

### 3. Docker Deployment
```bash
docker build -t apex-analyzer .
docker run -p 8080:8080 -e INCEPTION_API_KEY=sk_your_key_here apex-analyzer
```
Access at: `http://localhost:8080`

---

## 📁 Project Structure

```
ai-resume-analyzer/
├── src/main/java/com/resumeanalyzer/
│   ├── ai/
│   │   ├── MercuryApiClient.java    — HTTP client for Inception API
│   │   ├── PromptBuilder.java       — System + user prompt construction
│   │   └── DomainGuard.java         — Domain mismatch detection & override
│   ├── model/
│   │   ├── AnalysisResult.java      — Analysis data model (JSON mapped)
│   │   ├── SkillGap.java            — Skill gap model
│   │   ├── JobListing.java          — Job listing model
│   │   ├── Roadmap.java             — Learning roadmap model
│   │   ├── RoadmapTask.java         — Task within a roadmap
│   │   └── User.java                — User model
│   ├── parser/
│   │   └── ResumeParser.java        — PDF/TXT text extraction
│   ├── service/
│   │   └── JobScraperService.java   — Jsoup-based job scraping
│   ├── servlet/
│   │   ├── LoginServlet.java        — User login
│   │   ├── RegisterServlet.java     — User registration
│   │   ├── DashboardServlet.java    — Main dashboard
│   │   ├── UploadServlet.java       — Resume upload & analysis trigger
│   │   ├── ResultServlet.java       — Analysis result display
│   │   ├── RoadmapServlet.java      — Roadmap view
│   │   ├── SaveRoadmapServlet.java  — Create roadmap from analysis
│   │   ├── SettingsServlet.java     — User settings
│   │   ├── LogoutServlet.java       — Logout
│   │   ├── AnalysesServlet.java     — All analyses page
│   │   ├── DeleteAnalysisServlet.java — Delete analyses
│   │   └── SessionFilter.java       — Auth filter
│   ├── util/
│   │   └── ConfigManager.java       — Config (env, properties, system)
│   └── xml/
│       ├── UserXmlManager.java      — User CRUD on XML
│       ├── ResultXmlManager.java    — Analysis CRUD on XML
│       └── RoadmapXmlManager.java   — Roadmap CRUD on XML
├── src/main/webapp/
│   ├── index.jsp                    — Landing page
│   ├── css/style.css                — Global styles
│   ├── assets/js/                   — Static assets
│   ├── META-INF/context.xml         — Tomcat context
│   └── WEB-INF/
│       ├── web.xml                  — Servlet mappings & config
│       └── views/
│           ├── login.jsp            — Login page
│           ├── register.jsp         — Register page
│           ├── dashboard.jsp        — Main dashboard
│           ├── analyses.jsp         — All analyses list
│           ├── result.jsp           — Analysis report (score, gaps, suggestions, jobs, roadmap)
│           └── roadmap.jsp          — Skill roadmap detail view
├── src/main/resources/
│   ├── config.properties            — API keys & config
│   ├── users.xml                    — User data (auto-created)
│   └── results/                     — Analysis results (auto-created)
├── results/                         — Analysis XML files (runtime)
├── pom.xml                          — Maven build config
├── Dockerfile                       — Docker build
└── README.md                        — This file
```

---

## 📜 License

Developed as a Java Mini Project for AI-Powered Career Intelligence.