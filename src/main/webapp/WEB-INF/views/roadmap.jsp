<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.resumeanalyzer.model.Roadmap" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Personalized Roadmap - APEX</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700;800&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg: #0B0F1A;
            --card: #161E2E;
            --accent: #818CF8;
            --accent-glow: rgba(129, 140, 248, 0.3);
            --success: #10B981;
            --warning: #F59E0B;
            --danger: #EF4444;
            --text: #F8FAFC;
            --text-muted: #94A3B8;
            --border: rgba(255, 255, 255, 0.08);
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg); color: var(--text); padding: 3rem 5rem; max-width: 1300px; margin: 0 auto; line-height: 1.6; }

        .back-link { display: inline-block; margin-bottom: 2rem; color: var(--text-muted); text-decoration: none; font-size: 0.875rem; transition: color 0.2s; }
        .back-link:hover { color: var(--accent); }

        .header { margin-bottom: 3rem; display: flex; justify-content: space-between; align-items: flex-end; }
        .title-group h1 { font-size: 3rem; font-weight: 800; letter-spacing: -0.02em; margin-bottom: 0.5rem; background: linear-gradient(135deg, #fff 0%, #94A3B8 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }
        .subtitle { color: var(--text-muted); font-size: 1.125rem; }

        .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem; margin-bottom: 3rem; }
        .stat-card { background: var(--card); padding: 1.5rem; border-radius: 1rem; border: 1px solid var(--border); }
        .stat-label { font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.5rem; font-weight: 600; }
        .stat-value { font-size: 1.5rem; font-weight: 700; color: var(--accent); }

        .section-title { font-size: 1.5rem; font-weight: 700; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.75rem; }

        /* Skills Section */
        .skills-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(400px, 1fr)); gap: 2rem; margin-bottom: 4rem; }
        .skill-card { background: var(--card); border-radius: 1.5rem; border: 1px solid var(--border); overflow: hidden; display: flex; flex-direction: column; }
        .skill-header { padding: 1.5rem; background: rgba(255, 255, 255, 0.02); border-bottom: 1px solid var(--border); display: flex; justify-content: space-between; align-items: center; }
        .skill-name { font-size: 1.25rem; font-weight: 700; }
        .category-badge { padding: 0.25rem 0.75rem; border-radius: 999px; font-size: 0.7rem; font-weight: 700; text-transform: uppercase; }
        .cat-critical { background: rgba(239, 68, 68, 0.1); color: var(--danger); border: 1px solid rgba(239, 68, 68, 0.2); }
        .cat-major { background: rgba(245, 158, 11, 0.1); color: var(--warning); border: 1px solid rgba(245, 158, 11, 0.2); }
        .cat-minor { background: rgba(16, 185, 129, 0.1); color: var(--success); border: 1px solid rgba(16, 185, 129, 0.2); }

        .skill-body { padding: 1.5rem; flex: 1; }
        .level-bar-container { margin-bottom: 1.5rem; }
        .level-label { display: flex; justify-content: space-between; font-size: 0.8125rem; margin-bottom: 0.5rem; color: var(--text-muted); }
        .level-bar { height: 8px; background: rgba(255, 255, 255, 0.05); border-radius: 4px; overflow: hidden; position: relative; }
        .level-fill { height: 100%; background: var(--accent); border-radius: 4px; }
        .level-target { position: absolute; top: 0; width: 4px; height: 100%; background: white; opacity: 0.5; box-shadow: 0 0 10px white; }

        .phase-item { margin-bottom: 1.5rem; padding-left: 1.5rem; border-left: 2px solid var(--border); position: relative; }
        .phase-item::before { content: ''; position: absolute; left: -5px; top: 0; width: 8px; height: 8px; border-radius: 50%; background: var(--border); }
        .phase-item.active::before { background: var(--accent); box-shadow: 0 0 10px var(--accent-glow); }
        .phase-name { font-weight: 700; font-size: 0.9375rem; margin-bottom: 0.25rem; }
        .phase-meta { font-size: 0.75rem; color: var(--text-muted); margin-bottom: 0.75rem; }
        
        .resource-list { display: flex; flex-direction: column; gap: 0.5rem; }
        .resource-link { 
            display: flex; justify-content: space-between; align-items: center; 
            padding: 0.75rem; background: rgba(255, 255, 255, 0.03); border-radius: 0.75rem;
            text-decoration: none; color: var(--text); font-size: 0.8125rem; transition: all 0.2s;
            border: 1px solid transparent;
        }
        .resource-link:hover { background: rgba(255, 255, 255, 0.06); border-color: var(--accent); transform: translateX(4px); }
        .res-type { font-size: 0.625rem; padding: 0.15rem 0.4rem; border-radius: 0.25rem; background: rgba(255, 255, 255, 0.1); margin-left: 0.5rem; }

        /* Timeline Section */
        .timeline-container { background: var(--card); border-radius: 1.5rem; border: 1px solid var(--border); padding: 2.5rem; margin-bottom: 4rem; }
        .timeline-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 2rem; }
        .timeline-week { border-top: 2px solid var(--border); padding-top: 1.5rem; }
        .week-num { font-size: 0.75rem; font-weight: 800; color: var(--accent); margin-bottom: 0.5rem; text-transform: uppercase; }
        .week-focus { font-weight: 700; margin-bottom: 1rem; }
        .week-tasks { list-style: none; }
        .week-tasks li { font-size: 0.8125rem; color: var(--text-muted); margin-bottom: 0.5rem; display: flex; gap: 0.5rem; }
        .week-tasks li::before { content: '→'; color: var(--accent); }

        /* Bottom Grid */
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 3rem; }
        .list-card { background: var(--card); border-radius: 1.5rem; border: 1px solid var(--border); padding: 2rem; }
        .list-card ul { list-style: none; }
        .list-card li { margin-bottom: 1rem; padding-left: 1.75rem; position: relative; font-size: 0.9375rem; }
        .list-card li::before { position: absolute; left: 0; }
        .risk-list li::before { content: '⚠'; color: var(--warning); }
        .success-list li::before { content: '✓'; color: var(--success); font-weight: 800; }

        @media (max-width: 1000px) {
            body { padding: 2rem; }
            .stats-grid { grid-template-columns: 1fr 1fr; }
            .grid-2 { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>



    <a href="${pageContext.request.contextPath}/result?id=${analysisId}" class="back-link">← Back to Analysis Report</a>

    <div class="header">
        <div class="title-group">
            <h1>Expert Skill Roadmap</h1>
            <div class="subtitle">Personalized 12-week mastery path for <strong>${roadmap.targetRole}</strong></div>
        </div>
        <div style="text-align: right;">
            <div style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; margin-bottom: 0.25rem;">Roadmap ID</div>
            <div style="font-family: monospace; font-size: 0.875rem;">${roadmap.roadmapId}</div>
        </div>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-label">Total Duration</div>
            <div class="stat-value">${roadmap.totalDuration}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Total Learning Hours</div>
            <div class="stat-value">${roadmap.totalHours} hrs</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Estimated Cost</div>
            <div class="stat-value">${roadmap.estimatedCost}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">Success Probability</div>
            <div class="stat-value" style="color: var(--success);">${roadmap.motivationalMetrics.successProbability}</div>
        </div>
    </div>

    <div class="section-title">🎯 Skill-Gap Analysis</div>
    <div class="skills-grid">
        <c:forEach items="${roadmap.skills}" var="skill">
            <c:set var="catClass" value="cat-${skill.category.toLowerCase()}"/>
            <div class="skill-card">
                <div class="skill-header">
                    <span class="skill-name">${skill.skillName}</span>
                    <span class="category-badge ${catClass}">${skill.category}</span>
                </div>
                <div class="skill-body">
                    <div class="level-bar-container">
                        <div class="level-label">
                            <span>Proficiency Level</span>
                            <span>${skill.currentLevel} → ${skill.targetLevel}</span>
                        </div>
                        <div class="level-bar">
                            <div class="level-fill" style="width: ${(skill.currentLevel / 5.0) * 100}%"></div>
                            <div class="level-target" style="left: ${(skill.targetLevel / 5.0) * 100}%"></div>
                        </div>
                    </div>
 
                    <c:forEach items="${skill.phases}" var="phase">
                        <div class="phase-item active">
                            <div class="phase-name">${phase.phaseName}</div>
                            <div class="phase-meta">⏱ ${phase.duration} | 📚 ${phase.hours} hours</div>
                            <div class="resource-list">
                                <c:forEach items="${phase.resources}" var="res">
                                    <a href="${res.link}" target="_blank" class="resource-link">
                                        <span>${res.name} <span class="res-type">${res.type}</span></span>
                                        <span style="color: var(--text-muted); font-size: 0.7rem;">${res.type == 'Free' ? 'FREE' : res.price}</span>
                                    </a>
                                </c:forEach>
                            </div>
                            <div style="margin-top: 0.75rem; font-size: 0.75rem; color: var(--success); font-weight: 600;">
                                🏁 Milestone: ${phase.milestone}
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:forEach>
    </div>

    <div class="section-title">📅 12-Week Master Timeline</div>
    <div class="timeline-container">
        <div class="timeline-grid">
            <c:forEach items="${roadmap.timeline}" var="item">
                <div class="timeline-week">
                    <div class="week-num">Week ${item.week}</div>
                    <div class="week-focus">${item.focus}</div>
                    <ul class="week-tasks">
                        <c:forEach items="${item.tasks}" var="task">
                            <li>${task}</li>
                        </c:forEach>
                    </ul>
                    <div style="margin-top: 1rem; font-size: 0.75rem; color: var(--text-muted);">
                        <strong>Outcome:</strong> ${item.expectedOutcome}
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="grid-2">
        <div class="list-card">
            <div class="section-title" style="color: var(--warning);">🛑 Risk Mitigation</div>
            <ul class="risk-list">
                <c:forEach items="${roadmap.riskMitigation}" var="risk">
                    <li>${risk}</li>
                </c:forEach>
            </ul>
        </div>
        <div class="list-card">
            <div class="section-title" style="color: var(--success);">🏆 Success Checklist</div>
            <ul class="success-list">
                <c:forEach items="${roadmap.successChecklist}" var="item">
                    <li>${item}</li>
                </c:forEach>
            </ul>
        </div>
    </div>

    <div class="stat-card" style="text-align: center; background: linear-gradient(135deg, var(--card) 0%, rgba(129, 140, 248, 0.05) 100%);">
        <div class="stat-label">Estimated ROI</div>
        <div style="font-size: 1.5rem; font-weight: 800; margin-bottom: 0.5rem;">
            Score Improvement: <span style="color: var(--success);">${roadmap.motivationalMetrics.scoreImprovement}</span>
        </div>
        <div style="font-size: 1.125rem; color: var(--text-muted);">
            Expected Salary: <span style="color: white;">${roadmap.motivationalMetrics.salaryGrowth}</span>
        </div>
    </div>

    <div style="margin-top: 4rem; text-align: center; color: var(--text-muted); font-size: 0.75rem;">
        This roadmap is AI-generated based on your unique profile. Timelines are estimates based on standard learning curves.
    </div>

</body>
</html>
