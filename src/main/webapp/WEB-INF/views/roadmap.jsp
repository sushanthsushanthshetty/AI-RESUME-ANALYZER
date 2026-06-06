<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.resumeanalyzer.model.Roadmap" %>
<%@ page import="com.resumeanalyzer.model.RoadmapTask" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${roadmap.targetRole} Roadmap - APEX</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@latest/tabler-icons.min.css">
    <style>
        :root {
            --bg: #0F172A;
            --card: #1E293B;
            --accent: #6366F1;
            --accent-hover: #4F46E5;
            --success: #10B981;
            --danger: #EF4444;
            --warning: #F59E0B;
            --info: #3B82F6;
            --text: #F8FAFC;
            --text-muted: #94A3B8;
            --sidebar-w: 240px;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg); color: var(--text); display: flex; min-height: 100vh; }

        /* Sidebar */
        .sidebar {
            width: var(--sidebar-w);
            background-color: var(--card);
            border-right: 1px solid rgba(255, 255, 255, 0.05);
            padding: 2rem 1.25rem;
            position: fixed;
            height: 100vh;
            display: flex;
            flex-direction: column;
            z-index: 100;
        }
        .sidebar-brand h1 { font-size: 1.5rem; font-weight: 800; color: var(--accent); letter-spacing: -0.02em; }
        .sidebar-brand p { font-size: 0.75rem; color: var(--text-muted); font-weight: 500; text-transform: uppercase; margin-bottom: 2.5rem; }
        .nav-item { 
            display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem 1rem; color: var(--text-muted); 
            text-decoration: none; border-radius: 0.5rem; margin-bottom: 0.4rem; font-weight: 500; transition: all 0.2s; 
        }
        .nav-item.active { background-color: rgba(99, 102, 241, 0.1); color: var(--accent); }
        .nav-item:hover:not(.active) { color: var(--text); background-color: rgba(255, 255, 255, 0.05); }

        .main-content { margin-left: var(--sidebar-w); flex: 1; padding: 2rem 3rem; max-width: 1200px; width: 100%; }

        .header-section { margin-bottom: 2.5rem; }
        .breadcrumb { font-size: 0.875rem; color: var(--text-muted); margin-bottom: 1rem; }
        .breadcrumb a { color: var(--accent); text-decoration: none; }
        .roadmap-title { font-size: 2rem; font-weight: 800; margin-bottom: 0.5rem; }
        .roadmap-meta { font-size: 0.9375rem; color: var(--text-muted); }

        /* Progress Card */
        .progress-card {
            background: var(--card); border-radius: 1rem; padding: 2rem;
            border: 1px solid rgba(255, 255, 255, 0.05);
            display: flex; align-items: center; gap: 3rem; margin-bottom: 2.5rem;
            background: linear-gradient(135deg, rgba(30, 41, 59, 1) 0%, rgba(30, 41, 59, 0.8) 100%);
        }
        .progress-meter { position: relative; width: 120px; height: 120px; }
        .progress-meter svg { transform: rotate(-90deg); width: 120px; height: 120px; }
        .progress-meter circle { fill: none; stroke-width: 8; stroke-linecap: round; }
        .progress-bg { stroke: rgba(255, 255, 255, 0.05); }
        .progress-fill { stroke: var(--accent); transition: stroke-dashoffset 0.5s ease; }
        .progress-text { 
            position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);
            font-size: 1.5rem; font-weight: 800; color: white;
        }
        .progress-info h3 { font-size: 1.25rem; margin-bottom: 0.5rem; }
        .progress-info p { color: var(--text-muted); font-size: 0.9375rem; }
        .stats-row { display: flex; gap: 2rem; margin-top: 1.5rem; }
        .stat-item { display: flex; flex-direction: column; }
        .stat-label { font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 0.25rem; }
        .stat-value { font-weight: 700; font-size: 1.125rem; }

        /* Task Cards */
        .tasks-grid { display: grid; gap: 1.5rem; }
        .task-card { 
            background: var(--card); border-radius: 1rem; border: 1px solid rgba(255, 255, 255, 0.05);
            padding: 1.5rem; transition: transform 0.2s;
        }
        .task-card.completed { opacity: 0.7; border-left: 4px solid var(--success); }
        .task-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1rem; }
        .skill-name { font-size: 1.25rem; font-weight: 700; }
        .badge { padding: 0.25rem 0.75rem; border-radius: 999px; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; }
        .badge.critical { background: rgba(239, 68, 68, 0.1); color: var(--danger); }
        .badge.major { background: rgba(245, 158, 11, 0.1); color: var(--warning); }
        .badge.minor { background: rgba(148, 163, 184, 0.1); color: var(--text-muted); }
        
        .task-body { margin-bottom: 1.5rem; }
        .task-description { color: var(--text-muted); font-size: 0.9375rem; line-height: 1.6; margin-bottom: 1rem; }
        .action-plan { background: rgba(255, 255, 255, 0.02); padding: 1rem; border-radius: 0.5rem; border-left: 3px solid var(--accent); font-size: 0.875rem; }
        .action-label { display: block; font-weight: 700; font-size: 0.75rem; text-transform: uppercase; color: var(--accent); margin-bottom: 0.4rem; }
        .resource-link { display: inline-flex; align-items: center; gap: 0.25rem; padding: 0.4rem 0.75rem; background: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 0.4rem; color: var(--text); font-size: 0.75rem; font-weight: 600; text-decoration: none; transition: all 0.2s; }
        .resource-link:hover { background: var(--accent); border-color: var(--accent); color: white; }

        .timeline { font-size: 0.8125rem; color: var(--text-muted); margin-top: 1rem; display: flex; gap: 1.5rem; }
        .timeline i { color: var(--accent); margin-right: 0.4rem; }

        /* Progress Bar */
        .progress-container { margin: 1.5rem 0; }
        .progress-label-row { display: flex; justify-content: space-between; font-size: 0.8125rem; font-weight: 600; margin-bottom: 0.5rem; }
        .progress-bar-bg { height: 8px; background: rgba(255, 255, 255, 0.05); border-radius: 999px; overflow: hidden; }
        .progress-bar-fill { height: 100%; background: var(--accent); transition: width 0.3s ease; }

        /* Milestones */
        .milestones-list { margin-top: 1.5rem; }
        .milestone-item { display: flex; align-items: center; gap: 0.75rem; padding: 0.5rem 0; font-size: 0.875rem; cursor: pointer; }
        .milestone-item input { width: 18px; height: 18px; cursor: pointer; accent-color: var(--success); }
        .milestone-item.checked { color: var(--text-muted); text-decoration: line-through; }

        .task-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 1.5rem; padding-top: 1.5rem; border-top: 1px solid rgba(255, 255, 255, 0.05); }
        .status-badge { display: flex; align-items: center; gap: 0.4rem; font-size: 0.8125rem; font-weight: 700; }
        .status-badge.on-track { color: var(--success); }
        .status-badge.at-risk { color: var(--warning); }
        .status-badge.completed { color: var(--success); }

        .btn-complete { 
            padding: 0.5rem 1rem; background: transparent; border: 1px solid var(--accent); 
            color: var(--accent); border-radius: 0.4rem; font-weight: 700; font-size: 0.8125rem;
            cursor: pointer; transition: all 0.2s;
        }
        .btn-complete:hover { background: var(--accent); color: white; }
        .btn-complete:disabled { opacity: 0.5; cursor: not-allowed; border-color: var(--text-muted); color: var(--text-muted); }

        /* Modal */
        .modal-overlay { 
            position: fixed; inset: 0; background: rgba(0,0,0,0.8); z-index: 1000; 
            display: none; align-items: center; justify-content: center; backdrop-filter: blur(8px);
        }
        .modal-content { 
            background: var(--card); padding: 3rem; border-radius: 1.5rem; text-align: center; max-width: 500px;
            border: 1px solid var(--accent); box-shadow: 0 0 50px rgba(99, 102, 241, 0.3);
        }
        .celebration-icon { font-size: 4rem; margin-bottom: 1.5rem; display: block; }
        .modal-title { font-size: 1.75rem; font-weight: 800; margin-bottom: 1rem; }
        .modal-text { color: var(--text-muted); margin-bottom: 2rem; }
        .modal-buttons { display: flex; gap: 1rem; }
        .btn-modal { flex: 1; padding: 1rem; border-radius: 0.5rem; font-weight: 700; cursor: pointer; border: none; }
        .btn-share { background: var(--accent); color: white; }
        .btn-dashboard { background: rgba(255,255,255,0.05); color: white; }
    </style>
</head>
<body>
    <%
        Roadmap roadmap = (Roadmap) request.getAttribute("roadmap");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    %>

    <div class="sidebar">
        <div class="sidebar-brand">
            <h1>APEX</h1>
            <p>Resume Intelligence</p>
        </div>
        <nav class="nav-group">
            <a href="<%= request.getContextPath() %>/dashboard" class="nav-item">
                <i class="ti ti-layout-dashboard"></i> Dashboard
            </a>
            <a href="<%= request.getContextPath() %>/roadmap?id=<%= roadmap.getId() %>" class="nav-item active">
                <i class="ti ti-map"></i> My Roadmap
            </a>
            <a href="<%= request.getContextPath() %>/analyses" class="nav-item">
                <i class="ti ti-folder"></i> My Analyses
            </a>
        </nav>
    </div>

    <div class="main-content">
        <div class="header-section">
            <div class="breadcrumb">
                <a href="<%= request.getContextPath() %>/dashboard">Dashboard</a> > <span style="color: var(--text-muted)">My Roadmap</span>
            </div>
            <h1 class="roadmap-title">${roadmap.targetRole} Learning Roadmap</h1>
            <p class="roadmap-meta">Created on <%= roadmap.getCreatedAt().format(fmt) %> • Plan ID: #<%= roadmap.getId().substring(0,8) %></p>
        </div>

        <div class="progress-card">
            <div class="progress-meter">
                <svg viewBox="0 0 100 100">
                    <circle class="progress-bg" cx="50" cy="50" r="45"></circle>
                    <circle class="progress-fill" cx="50" cy="50" r="45" 
                            style="stroke-dasharray: 283; stroke-dashoffset: <%= 283 - (283 * roadmap.getCompletionPercentage() / 100) %>"></circle>
                </svg>
                <div class="progress-text" id="overall-percent">${roadmap.completionPercentage}%</div>
            </div>
            <div class="progress-info">
                <div class="stat-label">Current Mastery</div>
                <h3 id="overall-status-title"><%= roadmap.getCompletionPercentage() == 100 ? "Roadmap Completed! 🎉" : "Skills Progress" %></h3>
                <p>You've completed <span id="completed-count"><%= roadmap.getCompletedTaskCount() %></span> of <span id="total-count"><%= roadmap.getTotalTaskCount() %></span> critical skill gaps.</p>
                <div class="stats-row">
                    <div class="stat-item">
                        <span class="stat-label">Plan Status</span>
                        <span class="stat-value" id="roadmap-status-text" style="text-transform: capitalize; color: var(--accent);">${roadmap.status}</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Days Remaining</span>
                        <span class="stat-value"><%= roadmap.getTasks().get(0).getRemainingDays() %> Days</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="tasks-grid">
            <% for (RoadmapTask task : roadmap.getTasks()) { %>
                <div class="task-card <%= task.isCompleted() ? "completed" : "" %>" id="task-<%= task.getId() %>">
                    <div class="task-header">
                        <div>
                            <div class="skill-name"><%= task.getSkill() %></div>
                            <div class="timeline">
                                <span><i class="ti ti-calendar"></i> <%= task.getStartDate().format(fmt) %></span>
                                <span><i class="ti ti-target"></i> <%= task.getTargetDate().format(fmt) %></span>
                            </div>
                        </div>
                        <span class="badge <%= task.getSeverity().toLowerCase() %>"><%= task.getSeverity() %></span>
                    </div>

                    <div class="task-body">
                        <p class="task-description"><%= task.getDescription() %></p>
                        <div class="action-plan" style="margin-bottom: 1rem;">
                            <span class="action-label">30-Day Action Strategy</span>
                            <%= task.getAction() %>
                        </div>
                        <div class="learning-resources">
                            <span class="action-label">Recommended Resources & Docs</span>
                            <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                                <% String encodedSkill = java.net.URLEncoder.encode(task.getSkill(), "UTF-8"); %>
                                <a href="https://www.youtube.com/results?search_query=<%= encodedSkill %>+tutorial" target="_blank" class="resource-link"><i class="ti ti-brand-youtube"></i> YouTube</a>
                                <a href="https://www.udemy.com/courses/search/?q=<%= encodedSkill %>" target="_blank" class="resource-link"><i class="ti ti-book"></i> Udemy</a>
                                <a href="https://www.coursera.org/search?query=<%= encodedSkill %>" target="_blank" class="resource-link"><i class="ti ti-certificate"></i> Coursera</a>
                                <a href="https://www.google.com/search?q=<%= encodedSkill %>+official+documentation" target="_blank" class="resource-link"><i class="ti ti-file-text"></i> Docs</a>
                                <a href="https://github.com/topics/<%= task.getSkill().toLowerCase().replace(" ", "-") %>" target="_blank" class="resource-link"><i class="ti ti-brand-github"></i> GitHub</a>
                            </div>
                        </div>
                    </div>

                    <div class="progress-container">
                        <div class="progress-label-row">
                            <span>Task Progress</span>
                            <span class="task-percent" id="task-percent-<%= task.getId() %>"><%= task.getProgressPercentage() %>%</span>
                        </div>
                        <div class="progress-bar-bg">
                            <div class="progress-bar-fill" id="task-bar-<%= task.getId() %>" 
                                 style="width: <%= task.getProgressPercentage() %>% ; background: <%= task.getProgressPercentage() > 80 ? "var(--success)" : (task.getProgressPercentage() > 50 ? "var(--warning)" : "var(--accent)") %>"></div>
                        </div>
                    </div>

                    <div class="milestones-list">
                        <span class="action-label">Milestones</span>
                        <% for (String m : task.getMilestones()) { 
                            boolean isDone = task.getCompletedMilestones().contains(m);
                        %>
                            <div class="milestone-item <%= isDone ? "checked" : "" %>">
                                <input type="checkbox" <%= isDone ? "checked disabled" : "" %> 
                                       onclick="toggleMilestone('<%= roadmap.getId() %>', '<%= task.getId() %>', '<%= m.replace("'", "\\'") %>')">
                                <span><%= m %></span>
                            </div>
                        <% } %>
                    </div>

                    <div class="task-footer">
                        <div class="status-badge <%= task.getStatus().toLowerCase().replace(" ", "-") %>" id="status-<%= task.getId() %>">
                            <% if (task.isCompleted()) { %>
                                <i class="ti ti-circle-check"></i> Completed
                            <% } else if (task.isOverdue()) { %>
                                <i class="ti ti-alert-triangle"></i> At Risk
                            <% } else { %>
                                <i class="ti ti-clock"></i> On Track
                            <% } %>
                        </div>
                        <button class="btn-complete" id="btn-complete-<%= task.getId() %>" 
                                onclick="markTaskComplete('<%= roadmap.getId() %>', '<%= task.getId() %>')"
                                <%= task.isCompleted() ? "disabled" : "" %>>
                            <%= task.isCompleted() ? "Task Completed" : "Mark as Finished" %>
                        </button>
                    </div>
                </div>
            <% } %>
        </div>

        <div style="margin-top: 4rem; text-align: center;">
            <a href="<%= request.getContextPath() %>/dashboard" class="nav-item" style="display: inline-flex; justify-content: center; width: auto; padding: 1rem 2rem;">
                <i class="ti ti-arrow-left"></i> Back to Dashboard
            </a>
        </div>
    </div>

    <!-- Completion Modal -->
    <div id="completion-modal" class="modal-overlay">
        <div class="modal-content">
            <span class="celebration-icon">🎉</span>
            <h2 class="modal-title">Roadmap Complete!</h2>
            <p class="modal-text">Congratulations! You've mastered all the skill gaps required for the <strong>${roadmap.targetRole}</strong> role. Your growth is impressive!</p>
            <div class="modal-buttons">
                <button class="btn-modal btn-share" onclick="shareAchievement()">Share on LinkedIn</button>
                <button class="btn-modal btn-dashboard" onclick="window.location.href='dashboard'">Return to Dashboard</button>
            </div>
        </div>
    </div>

    <script src="<%= request.getContextPath() %>/assets/js/roadmap.js"></script>
</body>
</html>
