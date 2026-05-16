<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.resumeanalyzer.model.AnalysisResult" %>
<%@ page import="com.resumeanalyzer.model.SkillGap" %>
<%@ page import="com.resumeanalyzer.model.JobListing" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analysis Report - APEX</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg: #0F172A;
            --card: #1E293B;
            --accent: #6366F1;
            --success: #10B981;
            --danger: #EF4444;
            --warning: #F59E0B;
            --text: #F8FAFC;
            --text-muted: #94A3B8;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg); color: var(--text); padding: 2rem 4rem; max-width: 1200px; margin: 0 auto; line-height: 1.6; }

        .back-link { display: inline-block; margin-bottom: 1.5rem; color: var(--text-muted); text-decoration: none; font-size: 0.875rem; transition: color 0.2s; }
        .back-link:hover { color: var(--accent); }

        .header-card { background: var(--card); padding: 2.5rem; border-radius: 1.5rem; display: flex; justify-content: space-between; align-items: center; border: 1px solid rgba(255, 255, 255, 0.05); margin-bottom: 2rem; }
        .role-title { font-size: 2rem; font-weight: 800; margin-bottom: 0.5rem; }
        .timestamp { color: var(--text-muted); font-size: 0.875rem; }

        .score-circle-container { position: relative; width: 120px; height: 120px; display: flex; align-items: center; justify-content: center; }
        .score-svg { width: 100%; height: 100%; transform: rotate(-90deg); }
        .score-bg { fill: none; stroke: rgba(255, 255, 255, 0.05); stroke-width: 8; }
        .score-progress { fill: none; stroke-width: 8; stroke-linecap: round; transition: stroke-dasharray 1s ease; }
        .score-text-container { position: absolute; text-align: center; }
        .score-num { font-size: 1.75rem; font-weight: 800; }
        .grade-letter { font-size: 1rem; font-weight: 700; color: var(--text-muted); }

        .grid-3 { display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.5rem; margin-bottom: 2rem; }
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 2rem; }
        .card { background: var(--card); padding: 1.5rem; border-radius: 1rem; border: 1px solid rgba(255, 255, 255, 0.05); }

        .badge { display: inline-block; padding: 0.25rem 0.75rem; border-radius: 999px; font-size: 0.75rem; font-weight: 700; margin-bottom: 0.5rem; }
        .confidence { font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; }

        .section-title { font-size: 1.125rem; font-weight: 700; margin-bottom: 1rem; display: flex; align-items: center; gap: 0.5rem; }

        .strengths-list { list-style: none; }
        .strengths-list li { padding: 0.75rem 0 0.75rem 1rem; border-left: 3px solid var(--success); background: rgba(16, 185, 129, 0.03); margin-bottom: 0.75rem; font-size: 0.9375rem; }

        .gap-item { margin-bottom: 1.25rem; }
        .gap-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 0.25rem; }
        .gap-name { font-weight: 600; font-size: 0.9375rem; }
        .gap-fix { font-size: 0.8125rem; color: var(--text-muted); }
        .severity-badge { font-size: 0.625rem; padding: 0.15rem 0.4rem; border-radius: 0.25rem; }

        .suggestion-box { padding: 1rem; background: rgba(99, 102, 241, 0.03); border-left: 4px solid var(--accent); margin-bottom: 1rem; border-radius: 0 0.5rem 0.5rem 0; }
        
        .pill { display: inline-block; padding: 0.4rem 0.8rem; background: var(--danger); color: white; border-radius: 999px; font-size: 0.75rem; font-weight: 600; margin: 0 0.5rem 0.5rem 0; }

        .export-link { display: block; text-align: center; margin-top: 3rem; color: var(--text-muted); text-decoration: none; font-size: 0.875rem; }
        .export-link:hover { color: var(--accent); }

        .btn-download, .btn-print { 
            display: flex; align-items: center; gap: 0.5rem; 
            padding: 0.6rem 1rem; border-radius: 0.5rem; 
            font-size: 0.875rem; font-weight: 600; cursor: pointer;
            transition: all 0.2s; text-decoration: none;
        }
        .btn-download { background: var(--accent); color: white; border: none; }
        .btn-download:hover { opacity: 0.9; transform: translateY(-1px); }
        .btn-print { background: rgba(255, 255, 255, 0.05); color: var(--text); border: 1px solid rgba(255, 255, 255, 0.1); }
        .btn-print:hover { background: rgba(255, 255, 255, 0.1); }

        @media print {
            body { background: white !important; color: black !important; padding: 0 !important; }
            .back-link, .btn-download, .btn-print, .export-link { display: none !important; }
            .header-card, .card { background: white !important; border: 1px solid #eee !important; color: black !important; box-shadow: none !important; }
            .text-muted, .timestamp, .confidence { color: #666 !important; }
            .score-bg { stroke: #eee !important; }
            .strengths-list li { background: #f9f9f9 !important; border-left-color: #10B981 !important; color: black !important; }
            .suggestion-box { background: #f0f0ff !important; border-left-color: #6366F1 !important; color: black !important; }
            .pill { background: #eee !important; color: black !important; border: 1px solid #ddd !important; }
        }

        /* Jobs Section Styling */
        .jobs-section { margin-top: 4rem; padding-top: 2rem; border-top: 1px solid rgba(255, 255, 255, 0.1); }
        .jobs-header { margin-bottom: 2rem; }
        .jobs-title { font-size: 1.75rem; font-weight: 800; color: var(--text); margin-bottom: 0.5rem; display: flex; align-items: center; gap: 0.75rem; }
        .jobs-subtitle { color: var(--text-muted); font-size: 0.9375rem; }
        
        .jobs-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 1.5rem; margin-bottom: 3rem; }
        .job-card { 
            background: var(--card); padding: 1.75rem; border-radius: 1.25rem; 
            border: 1px solid rgba(255, 255, 255, 0.05); transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            display: flex; flex-direction: column; position: relative; overflow: hidden;
        }
        .job-card:hover { transform: translateY(-5px); border-color: var(--accent); box-shadow: 0 10px 30px -10px rgba(99, 102, 241, 0.2); }
        
        .job-source-badge { 
            position: absolute; top: 1.25rem; right: 1.25rem; padding: 0.25rem 0.6rem; 
            border-radius: 0.5rem; font-size: 0.7rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em;
        }
        .source-linkedin { background: rgba(10, 102, 194, 0.1); color: #0A66C2; border: 1px solid rgba(10, 102, 194, 0.2); }
        .source-naukri { background: rgba(255, 117, 0, 0.1); color: #FF7500; border: 1px solid rgba(255, 117, 0, 0.2); }
        .source-google { background: rgba(66, 133, 244, 0.1); color: #4285F4; border: 1px solid rgba(66, 133, 244, 0.2); }
        
        .job-title { font-size: 1.125rem; font-weight: 700; margin-bottom: 0.5rem; color: var(--text); padding-right: 5rem; }
        .job-company { font-weight: 600; color: var(--accent); font-size: 0.9375rem; margin-bottom: 0.25rem; }
        .job-meta { display: flex; gap: 1rem; font-size: 0.8125rem; color: var(--text-muted); margin-bottom: 1.25rem; }
        .job-meta-item { display: flex; align-items: center; gap: 0.35rem; }
        
        .job-snippet { font-size: 0.875rem; color: var(--text-muted); line-height: 1.5; margin-bottom: 1.5rem; flex: 1; }
        
        .apply-btn { 
            display: inline-flex; align-items: center; justify-content: center; gap: 0.5rem;
            padding: 0.75rem; background: var(--accent); color: white; text-decoration: none;
            border-radius: 0.75rem; font-weight: 700; font-size: 0.875rem; transition: all 0.2s;
        }
        .apply-btn:hover { background: var(--accent-hover); opacity: 0.9; }
        
        .roadmap-cta { 
            background: linear-gradient(135deg, #1E293B 0%, #0F172A 100%); 
            padding: 2.5rem; border-radius: 1.5rem; border: 1px solid var(--accent);
            text-align: center; margin-bottom: 3rem; position: relative; overflow: hidden;
        }
        .roadmap-cta::before {
            content: ''; position: absolute; top: -50%; left: -50%; width: 200%; height: 200%;
            background: radial-gradient(circle, rgba(99, 102, 241, 0.1) 0%, transparent 70%);
            z-index: 0;
        }
        .roadmap-btn {
            display: inline-flex; align-items: center; gap: 0.75rem;
            padding: 1rem 2rem; background: var(--accent); color: white;
            text-decoration: none; border-radius: 1rem; font-weight: 800;
            font-size: 1.125rem; transition: all 0.3s; margin-top: 1.5rem;
            box-shadow: 0 10px 20px -10px var(--accent);
            position: relative; z-index: 1;
        }
        .roadmap-btn:hover { transform: translateY(-3px); box-shadow: 0 15px 30px -10px var(--accent); }

        .jobs-note { font-size: 0.75rem; color: var(--text-muted); text-align: center; margin-top: 1rem; font-style: italic; }
        
        .error-alert { 
            background: rgba(239, 68, 68, 0.1); color: var(--danger); 
            padding: 1rem; border-radius: 0.75rem; border: 1px solid rgba(239, 68, 68, 0.2);
            margin-bottom: 2rem; display: flex; align-items: center; gap: 0.75rem;
            font-size: 0.875rem; font-weight: 600;
        }
    </style>
</head>
<body>

    <c:set var="scoreColor" value="${result.score >= 70 ? 'var(--success)' : (result.score >= 50 ? 'var(--warning)' : 'var(--danger)')}"/>
    <c:set var="fitColor" value="${result.roleFit == 'Strong' ? 'var(--success)' : (result.roleFit == 'Moderate' ? 'var(--warning)' : 'var(--danger)')}"/>
    <c:set var="offset" value="${339.29 * (1 - result.score / 100.0)}"/>

    <a href="<%= request.getContextPath() %>/dashboard" class="back-link">← Back to Dashboard</a>

    <c:if test="${not empty error}">
        <div class="error-alert">
            <span>⚠️</span> ${error}
        </div>
    </c:if>

    <div class="header-card">
        <div>
            <div class="role-title">${result.targetRole}</div>
            <div class="timestamp">Analyzed on ${result.timestamp.replace('T', ' at ')}</div>
            
            <div style="margin-top: 1.5rem; display: flex; gap: 1rem;">
                <a href="${pageContext.request.contextPath}/result?id=${result.id}&format=xml" class="btn-download">
                    <span>↓</span> Download XML
                </a>
                <button onclick="window.print()" class="btn-print">
                    <span>⎙</span> Print / PDF
                </button>
            </div>
        </div>
        <div class="score-circle-container">
            <svg class="score-svg">
                <circle class="score-bg" cx="60" cy="60" r="54"></circle>
                <circle class="score-progress" cx="60" cy="60" r="54" 
                        style="stroke: ${scoreColor}; stroke-dasharray: 339.29; stroke-dashoffset: ${offset};"></circle>
            </svg>
            <div class="score-text-container">
                <div class="score-num">${result.score}</div>
                <div class="grade-letter">Grade ${result.scoreGrade}</div>
            </div>
        </div>
    </div>

    <div class="grid-3">
        <div class="card">
            <div class="badge" style="background: rgba(${fitColor.contains('success') ? '16, 185, 129' : (fitColor.contains('warning') ? '245, 158, 11' : '239, 68, 68')}, 0.1); color: ${fitColor}">
                ${result.roleFit} Fit
            </div>
            <div class="confidence">${result.confidence} confidence</div>
        </div>
        <div class="card">
            <div class="section-title" style="color: var(--warning); margin-bottom: 0.5rem;">⚠ Interview Risk</div>
            <div style="font-size: 0.875rem;">${result.interviewRisk}</div>
        </div>
        <div class="card">
            <div class="section-title" style="color: var(--success); margin-bottom: 0.5rem;">★ Market Edge</div>
            <div style="font-size: 0.875rem;">${result.marketEdge}</div>
        </div>
    </div>

    <div class="card" style="margin-bottom: 2rem;">
        <div class="section-title">Analysis Summary</div>
        <p>${result.summary}</p>
    </div>

    <div class="roadmap-cta">
        <h2 style="font-size: 1.75rem; margin-bottom: 0.5rem;">🚀 Bridge Your Skill Gaps</h2>
        <p style="color: var(--text-muted);">Get a personalized 12-week mastery roadmap with curated courses, projects, and milestones.</p>
        <a href="${pageContext.request.contextPath}/roadmap?id=${result.id}" class="roadmap-btn">
            View My Personalized Roadmap <span style="font-size: 1.2em">→</span>
        </a>
    </div>

    <div class="grid-2">
        <div class="card">
            <div class="section-title" style="color: var(--success);">✔ Strengths</div>
            <ul class="strengths-list">
                <c:forEach items="${result.strengths}" var="s">
                    <li>${s}</li>
                </c:forEach>
            </ul>
        </div>
        <div class="card">
            <div class="section-title" style="color: var(--danger);">✘ Skill Gaps</div>
            <c:forEach items="${result.skillGaps}" var="gap">
                <c:set var="sevColor" value="${gap.severity == 'critical' ? 'var(--danger)' : (gap.severity == 'major' ? 'var(--warning)' : 'var(--text-muted)')}"/>
                <div class="gap-item">
                    <div class="gap-header">
                        <span class="gap-name">${gap.gap}</span>
                        <span class="severity-badge" style="background: rgba(${sevColor.contains('danger') ? '239, 68, 68' : (sevColor.contains('warning') ? '245, 158, 11' : '148, 163, 184')}, 0.1); color: ${sevColor}">
                            ${gap.severity}
                        </span>
                    </div>
                    <div class="gap-fix">${gap.fix}</div>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="card" style="margin-bottom: 2rem;">
        <div class="section-title">Actionable Suggestions</div>
        <c:forEach items="${result.suggestions}" var="sug" varStatus="status">
            <div class="suggestion-box">
                <strong>${status.count}.</strong> ${sug}
            </div>
        </c:forEach>
    </div>

    <div class="card">
        <div class="section-title">ATS Keywords Missing from Your Resume</div>
        <div style="margin-bottom: 1rem;">
            <c:forEach items="${result.keywordMisses}" var="kw">
                <span class="pill">${kw}</span>
            </c:forEach>
        </div>
        <div style="font-size: 0.75rem; color: var(--text-muted);">Add these exact words to pass ATS filters</div>
    </div>

    <c:if test="${not empty recommendedJobs}">
    <div class="jobs-section">
        <div class="jobs-header">
            <div class="jobs-title">💼 Recommended Jobs For You</div>
            <div class="jobs-subtitle">Current openings from LinkedIn & Naukri based on your profile and target role</div>
        </div>
        
        <div class="jobs-grid">
            <c:forEach items="${recommendedJobs}" var="job">
                <div class="job-card">
                    <div class="job-source-badge ${job.source == 'LinkedIn' ? 'source-linkedin' : (job.source == 'Naukri' ? 'source-naukri' : 'source-google')}">
                        ${job.source}
                    </div>
                    <div class="job-title">${job.title}</div>
                    <div class="job-company">${job.company}</div>
                    <div class="job-meta">
                        <div class="job-meta-item">📍 ${job.location}</div>
                        <div class="job-meta-item">🕒 ${job.postedDate}</div>
                    </div>
                    <div class="job-snippet">${job.descriptionSnippet}</div>
                    <a href="${job.applyLink}" target="_blank" class="apply-btn">
                        ${job.company == 'Search Results' ? 'Search Now' : 'Apply Now'} <span style="font-size: 1.1em">→</span>
                    </a>
                </div>
            </c:forEach>
        </div>
        <div class="jobs-note">Jobs fetched live. Always verify details and company legitimacy before applying.</div>
    </div>
    </c:if>

    <a href="${pageContext.request.contextPath}/result?id=${result.id}&format=xml" class="export-link">Download as XML</a>

</body>
</html>
