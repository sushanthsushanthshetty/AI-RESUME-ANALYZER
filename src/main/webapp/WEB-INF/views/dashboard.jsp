<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="com.resumeanalyzer.model.AnalysisResult" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - APEX Resume Intelligence</title>
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

        * { box-sizing: border-box; margin: 0; padding: 0; scroll-behavior: smooth; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg); color: var(--text); display: flex; min-height: 100vh; overflow-x: hidden; }

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

        .sidebar-brand { margin-bottom: 2.5rem; }
        .sidebar-brand h1 { font-size: 1.5rem; font-weight: 800; color: var(--accent); letter-spacing: -0.02em; }
        .sidebar-brand p { font-size: 0.75rem; color: var(--text-muted); font-weight: 500; text-transform: uppercase; letter-spacing: 0.05em; }

        .nav-group { flex: 1; }
        .nav-item { 
            display: flex; align-items: center; gap: 0.75rem;
            padding: 0.75rem 1rem; color: var(--text-muted); 
            text-decoration: none; border-radius: 0.5rem; 
            margin-bottom: 0.4rem; font-weight: 500; transition: all 0.2s; 
            font-size: 0.9375rem;
        }
        .nav-item i { font-size: 1.25rem; }
        .nav-item.active { background-color: rgba(99, 102, 241, 0.1); color: var(--accent); }
        .nav-item:hover:not(.active) { color: var(--text); background-color: rgba(255, 255, 255, 0.05); cursor: pointer; }

        .sidebar-footer { 
            padding-top: 1.5rem; border-top: 1px solid rgba(255, 255, 255, 0.05);
            margin-top: auto;
        }
        .user-info { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem; }
        .user-avatar { width: 32px; height: 32px; background: var(--accent); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 0.875rem; }
        .user-details { font-size: 0.8125rem; overflow: hidden; }
        .user-name { font-weight: 600; white-space: nowrap; text-overflow: ellipsis; display: block; }
        .logout-btn { 
            display: flex; align-items: center; gap: 0.5rem; 
            width: 100%; padding: 0.6rem; color: var(--danger); 
            text-decoration: none; font-size: 0.875rem; font-weight: 600;
            border-radius: 0.4rem; transition: background 0.2s;
        }
        .logout-btn:hover { background: rgba(239, 68, 68, 0.1); }

        /* Main Content */
        .main-content {
            margin-left: var(--sidebar-w);
            flex: 1;
            padding: 2rem 3rem;
            max-width: 1400px;
        }

        /* Welcome Banner */
        .welcome-banner {
            background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%);
            padding: 2rem;
            border-radius: 0.75rem;
            margin-bottom: 2rem;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
        }
        .welcome-banner h2 { font-size: 1.75rem; font-weight: 800; margin-bottom: 0.5rem; }
        .welcome-banner p { opacity: 0.9; font-size: 1rem; }

        /* Stats Row */
        .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 1.5rem; margin-bottom: 2.5rem; }
        .stat-card { 
            background: var(--card); padding: 1.5rem; border-radius: 0.75rem;
            border: 1px solid rgba(255, 255, 255, 0.05);
            display: flex; align-items: center; gap: 1.25rem;
        }
        .stat-icon { 
            width: 48px; height: 48px; border-radius: 0.5rem;
            display: flex; align-items: center; justify-content: center;
            font-size: 1.5rem;
        }
        .stat-info .stat-value { font-size: 1.5rem; font-weight: 800; display: block; }
        .stat-info .stat-label { font-size: 0.8125rem; color: var(--text-muted); font-weight: 500; }

        .section-header { font-size: 1.375rem; font-weight: 700; margin-bottom: 1.25rem; display: flex; align-items: center; gap: 0.75rem; }
        .card { background-color: var(--card); padding: 2rem; border-radius: 1rem; border: 1px solid rgba(255, 255, 255, 0.05); margin-bottom: 2.5rem; }

        /* Form Controls */
        .form-group { margin-bottom: 1.5rem; }
        label { display: block; font-size: 0.875rem; color: var(--text-muted); margin-bottom: 0.5rem; font-weight: 600; }
        .helper-text { font-size: 0.75rem; color: var(--text-muted); margin-top: 0.4rem; display: block; }
        
        input[type="text"], textarea { 
            width: 100%; padding: 0.875rem 1.125rem; 
            background-color: var(--bg); border: 1px solid rgba(255, 255, 255, 0.1); 
            border-radius: 0.5rem; color: white; outline: none; transition: border-color 0.2s;
            font-family: inherit; font-size: 0.9375rem;
        }
        input[type="text"]:focus, textarea:focus { border-color: var(--accent); }
        
        .char-counter { text-align: right; font-size: 0.75rem; color: var(--text-muted); margin-top: 0.25rem; }

        .file-label { 
            display: flex; flex-direction: column; align-items: center; justify-content: center;
            padding: 2.5rem 1.5rem; background-color: rgba(255, 255, 255, 0.02); 
            border: 2px dashed rgba(255, 255, 255, 0.1); border-radius: 0.75rem; 
            cursor: pointer; width: 100%; transition: all 0.2s; 
        }
        .file-label:hover { background-color: rgba(99, 102, 241, 0.02); border-color: var(--accent); }
        
        .btn-group { display: flex; gap: 1rem; }
        button.btn-primary { 
            flex: 1; padding: 1rem; background-color: var(--accent); 
            color: white; border: none; border-radius: 0.5rem; 
            font-weight: 700; cursor: pointer; transition: all 0.2s;
            font-size: 1rem; display: flex; align-items: center; justify-content: center; gap: 0.5rem;
        }
        button.btn-primary:hover { background-color: var(--accent-hover); transform: translateY(-1px); }
        button.btn-outline { 
            padding: 0 1.5rem; background: transparent; border: 1px solid rgba(255, 255, 255, 0.2); 
            color: var(--text); border-radius: 0.5rem; font-weight: 600; 
            cursor: pointer; transition: all 0.2s; 
        }
        button.btn-outline:hover { background: rgba(255, 255, 255, 0.05); border-color: var(--text); }

        /* Tips Grid */
        .tips-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 3rem; }
        .tip-card { 
            background: var(--card); padding: 1.5rem; border-radius: 0.75rem;
            border: 1px solid rgba(255, 255, 255, 0.05); border-left: 4px solid var(--accent);
        }
        .tip-header { display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.75rem; }
        .tip-header i { font-size: 1.25rem; color: var(--accent); }
        .tip-title { font-weight: 700; font-size: 1rem; }
        .tip-body { font-size: 0.875rem; color: var(--text-muted); line-height: 1.6; }
        .tip-details { margin-top: 1rem; padding-top: 1rem; border-top: 1px solid rgba(255, 255, 255, 0.05); display: none; }
        .tip-details ul { padding-left: 1.25rem; }
        .tip-details li { font-size: 0.8125rem; color: var(--text-muted); margin-bottom: 0.4rem; }
        .btn-tip-toggle { background: none; border: none; color: var(--accent); font-size: 0.75rem; font-weight: 700; cursor: pointer; margin-top: 0.5rem; padding: 0; }

        /* Table Search & Controls */
        .table-controls { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem; gap: 1rem; }
        .search-box { position: relative; flex: 1; max-width: 400px; }
        .search-box i { position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); color: var(--text-muted); }
        .search-box input { padding-left: 2.75rem; }
        
        .btn-delete-all { font-size: 0.75rem; color: var(--danger); background: transparent; border: 1px solid rgba(239, 68, 68, 0.3); padding: 0.4rem 0.8rem; border-radius: 0.4rem; cursor: pointer; transition: all 0.2s; }
        .btn-delete-all:hover { background: rgba(239, 68, 68, 0.1); border-color: var(--danger); }

        .table-container { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; text-align: left; min-width: 800px; }
        th { padding: 1.25rem 1rem; color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.05em; border-bottom: 1px solid rgba(255, 255, 255, 0.05); }
        td { padding: 1.25rem 1rem; font-size: 0.9375rem; border-bottom: 1px solid rgba(255, 255, 255, 0.05); }
        
        .score-pill { padding: 0.25rem 0.6rem; border-radius: 0.375rem; font-weight: 700; font-size: 0.8125rem; }
        .fit-badge { padding: 0.25rem 0.75rem; border-radius: 999px; font-size: 0.75rem; font-weight: 700; }
        
        .btn-view { display: inline-flex; align-items: center; gap: 0.4rem; padding: 0.5rem 0.875rem; background-color: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255, 255, 255, 0.1); color: var(--text); border-radius: 0.4rem; text-decoration: none; font-size: 0.8125rem; font-weight: 600; transition: all 0.2s; }
        .btn-view:hover { background-color: var(--accent); border-color: var(--accent); }

        /* Empty State */
        .empty-state { padding: 4rem 2rem; text-align: center; }
        .empty-icon { font-size: 4rem; margin-bottom: 1.5rem; display: block; }
        .empty-title { font-size: 1.25rem; font-weight: 700; margin-bottom: 0.5rem; }
        .empty-subtitle { color: var(--text-muted); font-size: 0.9375rem; margin-bottom: 1.5rem; }
        .btn-jump { display: inline-block; padding: 0.75rem 1.5rem; background: var(--accent); color: white; text-decoration: none; border-radius: 0.5rem; font-weight: 700; }

        /* Footer */
        .footer { margin-top: 5rem; padding: 2rem 0; border-top: 1px solid rgba(255, 255, 255, 0.05); text-align: center; }
        .footer-text { font-size: 0.8125rem; color: var(--text-muted); margin-bottom: 1rem; }
        .footer-links { display: flex; justify-content: center; gap: 2rem; }
        .footer-links a { font-size: 0.8125rem; color: var(--accent); text-decoration: none; font-weight: 500; }
        .footer-links a:hover { text-decoration: underline; }

        /* Loading Overlay */
        #loadingOverlay { position: fixed; inset: 0; background: rgba(15, 23, 42, 0.9); z-index: 1000; display: none; flex-direction: column; align-items: center; justify-content: center; backdrop-filter: blur(8px); }
        .spinner { width: 48px; height: 48px; border: 4px solid var(--accent); border-top-color: transparent; border-radius: 50%; animation: spin 1s linear infinite; margin-bottom: 1.5rem; }
        @keyframes spin { to { transform: rotate(360deg); } }

        .alert-error { background: rgba(239, 68, 68, 0.1); border: 1px solid var(--danger); color: var(--danger); padding: 1.25rem; border-radius: 0.75rem; margin-bottom: 1.5rem; display: flex; align-items: center; gap: 0.75rem; }
    </style>
</head>
<body>

    <div id="loadingOverlay">
        <div class="spinner"></div>
        <div style="font-weight: 700; font-size: 1.25rem;">Mercury 2 reasoning engine engaged...</div>
        <div style="font-size: 1rem; color: var(--text-muted); margin-top: 0.5rem;">Running deep analysis protocol</div>
    </div>

    <div class="sidebar">
        <div class="sidebar-brand">
            <h1>APEX</h1>
            <p>Resume Intelligence</p>
        </div>
        
        <nav class="nav-group">
            <a href="<%= request.getContextPath() %>/dashboard" class="nav-item active">
                <i class="ti ti-layout-dashboard"></i> Dashboard
            </a>
            <a href="<%= request.getContextPath() %>/analyses" class="nav-item">
                <i class="ti ti-folder"></i> My Analyses
            </a>
            <a href="#tips" class="nav-item">
                <i class="ti ti-bulb"></i> Career Tips
            </a>
            <a href="#stats" class="nav-item">
                <i class="ti ti-chart-line"></i> My Stats
            </a>
            <a onclick="openSettings()" class="nav-item">
                <i class="ti ti-settings"></i> Settings
            </a>
        </nav>

        <div class="sidebar-footer">
            <div class="user-info">
                <div class="user-avatar"><%= ((String)session.getAttribute("username")).substring(0, 1).toUpperCase() %></div>
                <div class="user-details">
                    <span class="user-name">${username}</span>
                </div>
            </div>
            <a href="<%= request.getContextPath() %>/logout" class="logout-btn">
                <i class="ti ti-logout"></i> Sign Out
            </a>
        </div>
    </div>

    <div class="main-content">
        <!-- Welcome Banner -->
        <div class="welcome-banner">
            <h2>Welcome back, ${username} 👋</h2>
            <p>Upload your resume and let Mercury 2 find your perfect role match.</p>
        </div>

        <%
            List<AnalysisResult> results = (List<AnalysisResult>) request.getAttribute("results");
            int total = (results != null) ? results.size() : 0;
            int maxScore = 0;
            int avgScore = 0;
            int strongFits = 0;
            
            if (total > 0) {
                int sum = 0;
                for (AnalysisResult r : results) {
                    if (r.getScore() > maxScore) maxScore = r.getScore();
                    if ("Strong".equals(r.getRoleFit())) strongFits++;
                    sum += r.getScore();
                }
                avgScore = Math.round((float)sum / total);
            }
        %>

        <!-- Stats Row -->
        <div class="stats-grid" id="stats">
            <div class="stat-card" style="border-top-color: var(--accent);">
                <div class="stat-icon" style="background: rgba(99, 102, 241, 0.1); color: var(--accent);">📄</div>
                <div class="stat-info">
                    <span class="stat-value"><%= total %></span>
                    <span class="stat-label">Resumes Analyzed</span>
                </div>
            </div>
            <div class="stat-card" style="border-top-color: var(--success);">
                <div class="stat-icon" style="background: rgba(16, 185, 129, 0.1); color: var(--success);">🏆</div>
                <div class="stat-info">
                    <span class="stat-value"><%= total > 0 ? maxScore + "%" : "--" %></span>
                    <span class="stat-label">Best Score</span>
                </div>
            </div>
            <div class="stat-card" style="border-top-color: var(--warning);">
                <div class="stat-icon" style="background: rgba(245, 158, 11, 0.1); color: var(--warning);">📊</div>
                <div class="stat-info">
                    <span class="stat-value"><%= total > 0 ? avgScore + "%" : "--" %></span>
                    <span class="stat-label">Average Score</span>
                </div>
            </div>
            <div class="stat-card" style="border-top-color: var(--info);">
                <div class="stat-icon" style="background: rgba(59, 130, 246, 0.1); color: var(--info);">✅</div>
                <div class="stat-info">
                    <span class="stat-value"><%= strongFits %></span>
                    <span class="stat-label">Strong Fits</span>
                </div>
            </div>
        </div>

        <div class="section-header" id="upload-section">
            <i class="ti ti-upload" style="color: var(--accent);"></i> Analyze New Resume
        </div>
        <div class="card">
            <c:if test="${not empty error}">
                <div class="alert-error"><i class="ti ti-alert-circle"></i> ${error}</div>
            </c:if>

            <form id="uploadForm" action="<%= request.getContextPath() %>/upload" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label>Resume File (.pdf, .txt)</label>
                    <div id="dropZone" class="file-label">
                        <i class="ti ti-file-upload" style="font-size: 2.5rem; color: var(--accent); margin-bottom: 1rem;"></i>
                        <span id="fileNameDisplay">Click to choose or drag & drop</span>
                        <span class="helper-text">Supported: .pdf (text-based), .txt — Max size 10MB</span>
                        <input type="file" name="resumeFile" id="resumeFile" accept=".pdf,.txt" required style="display: none;">
                    </div>
                </div>
                <div class="form-group">
                    <label>Target Role</label>
                    <input type="text" id="targetRoleInput" name="targetRole" placeholder="e.g. Senior Backend Java Developer" required>
                    <span class="helper-text">💡 Be specific: 'Senior Backend Java Developer' not just 'Developer'</span>
                </div>
                <div class="form-group">
                    <label>Job Description</label>
                    <textarea id="jobDescTextarea" name="jobDesc" rows="5" placeholder="Paste the full job description here..." required maxlength="3000"></textarea>
                    <div class="char-counter"><span id="jd-count">0</span> / 3000 characters</div>
                </div>
                <div class="btn-group">
                    <button type="submit" class="btn-primary">
                        <i class="ti ti-bolt"></i> Analyze with Mercury 2
                    </button>
                    <button type="button" class="btn-outline" onclick="resetForm()">Clear</button>
                </div>
            </form>
        </div>

        <!-- Career Tips -->
        <div class="section-header" id="tips">
            <i class="ti ti-bulb" style="color: var(--warning);"></i> Career Tips
        </div>
        <div class="tips-grid">
            <div class="tip-card">
                <div class="tip-header"><i class="ti ti-search"></i> <span class="tip-title">Beat the ATS Robot</span></div>
                <p class="tip-body">Always mirror exact keywords from the job description in your resume. 75% of resumes are rejected before a human reads them.</p>
                <button class="btn-tip-toggle" onclick="toggleTip('tip1-details')">Read More ↓</button>
                <div id="tip1-details" class="tip-details">
                    <ul>
                        <li>Mirror job description language word-for-word</li>
                        <li>Include both acronym and full form: "AI (Artificial Intelligence)"</li>
                        <li>Add a dedicated Skills section with exact tool names</li>
                        <li>Never use tables or graphics — ATS cannot read them</li>
                        <li>Save as .docx or plain PDF, never image-based PDF</li>
                    </ul>
                </div>
            </div>
            <div class="tip-card">
                <div class="tip-header"><i class="ti ti-chart-arrows"></i> <span class="tip-title">Use Numbers Always</span></div>
                <p class="tip-body">Replace vague claims with metrics. Instead of 'improved performance', write 'reduced API response time by 35%'.</p>
                <button class="btn-tip-toggle" onclick="toggleTip('tip2-details')">Read More ↓</button>
                <div id="tip2-details" class="tip-details">
                    <ul>
                        <li>"Reduced load time by 40%" beats "improved performance"</li>
                        <li>Include scale: users, transactions, team size, revenue</li>
                        <li>Use past tense for old jobs, present for current</li>
                        <li>One metric per bullet point, max 2 lines per bullet</li>
                    </ul>
                </div>
            </div>
            <div class="tip-card">
                <div class="tip-header"><i class="ti ti-target"></i> <span class="tip-title">Apply to the Right Role</span></div>
                <p class="tip-body">A mismatched application wastes everyone's time. Use APEX to verify role fit before submitting.</p>
                <button class="btn-tip-toggle" onclick="toggleTip('tip3-details')">Read More ↓</button>
                <div id="tip3-details" class="tip-details">
                    <ul>
                        <li>Score below 60 = do not apply, upskill first</li>
                        <li>Score 60-75 = apply with a strong cover letter</li>
                        <li>Score 75-89 = strong candidate, apply confidently</li>
                        <li>Score 90+ = top candidate, negotiate salary</li>
                    </ul>
                </div>
            </div>
            <div class="tip-card">
                <div class="tip-header"><i class="ti ti-edit"></i> <span class="tip-title">Tailor Every Resume</span></div>
                <p class="tip-body">Customize your resume for each application. Use APEX suggestions to tailor each version.</p>
                <button class="btn-tip-toggle" onclick="toggleTip('tip4-details')">Read More ↓</button>
                <div id="tip4-details" class="tip-details">
                    <ul>
                        <li>Keep a master resume with everything</li>
                        <li>For each application, create a copy and trim to fit JD</li>
                        <li>Change the summary paragraph to match each role</li>
                        <li>Reorder skills to put JD-matching ones first</li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Previous Analyses -->
        <div class="section-header" id="analyses">
            <i class="ti ti-history" style="color: var(--info);"></i> My Analyses
        </div>
        <div class="card">
            <c:if test="${empty results}">
                <div class="empty-state">
                    <span class="empty-icon">📋</span>
                    <h3 class="empty-title">No analyses yet</h3>
                    <p class="empty-subtitle">Upload your first resume above to get started</p>
                    <a href="#upload-section" class="btn-jump">Analyze Now →</a>
                </div>
            </c:if>
            <c:if test="${not empty results}">
                <div class="table-controls">
                    <div class="search-box">
                        <i class="ti ti-search"></i>
                        <input type="text" id="roleSearch" placeholder="Search by role name..." onkeyup="filterTable()">
                    </div>
                    <button class="btn-delete-all" onclick="deleteAllConfirm()">Delete All</button>
                </div>
                <div class="table-container">
                    <table id="analysesTable">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Target Role</th>
                                <th>Score</th>
                                <th>Grade</th>
                                <th>Role Fit</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${results}" var="res">
                            <c:set var="scoreColor" value="${res.score >= 70 ? 'var(--success)' : (res.score >= 50 ? 'var(--warning)' : 'var(--danger)')}"/>
                            <c:set var="fitColor" value="${res.roleFit == 'Strong' ? 'var(--success)' : (res.roleFit == 'Moderate' ? 'var(--warning)' : 'var(--danger)')}"/>
                            <tr>
                                <td style="color: var(--text-muted); font-size: 0.75rem;">${res.timestamp.split('T')[0]}</td>
                                <td class="role-cell" style="font-weight: 600;">${res.targetRole}</td>
                                <td>
                                    <span class="score-pill" style="background: rgba(${scoreColor.contains('success') ? '16, 185, 129' : (scoreColor.contains('warning') ? '245, 158, 11' : '239, 68, 68')}, 0.15); color: ${scoreColor}">
                                        ${res.score}%
                                    </span>
                                </td>
                                <td style="font-weight: 700;">${res.scoreGrade}</td>
                                <td>
                                    <span class="fit-badge" style="background: rgba(${fitColor.contains('success') ? '16, 185, 129' : (fitColor.contains('warning') ? '245, 158, 11' : '239, 68, 68')}, 0.1); color: ${fitColor}">
                                        ${res.roleFit}
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/result?id=${res.id}" class="btn-view">
                                        <i class="ti ti-eye"></i> View
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>

        <!-- Footer -->
        <footer class="footer">
            <p class="footer-text">APEX Resume Intelligence • Powered by Inception Mercury 2</p>
            <div class="footer-links">
                <a href="#tips">How it works</a>
                <a href="#stats">My Stats</a>
                <a href="#upload-section">Analyze</a>
            </div>
        </footer>
    </div>

    <!-- SETTINGS MODAL -->
    <div id="settings-overlay"
         style="display:none; position:fixed; inset:0; background:rgba(0,0,0,0.7); z-index:1000; align-items:center; justify-content:center;">
      <div style="background:#1E293B; border-radius:16px; padding:32px; width:460px; max-width:90vw; border:1px solid #334155; position:relative;">

        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:24px;">
          <h2 style="color:white; margin:0">⚙ Settings</h2>
          <button onclick="closeSettings()" style="background:none; border:none; color:#94A3B8; font-size:24px; cursor:pointer;">✕</button>
        </div>

        <form action="<%= request.getContextPath() %>/settings" method="post">
          <p style="color:#6366F1; font-size:12px; font-weight:600; letter-spacing:1px; margin-bottom:12px">PROFILE</p>
          
          <label style="color:#94A3B8; font-size:13px">Display Name</label>
          <input type="text" name="displayName" value="${username}" required
                 style="width:100%; padding:10px 14px; background:#0F172A; border:1px solid #334155; border-radius:8px; color:white; margin:6px 0 16px; box-sizing:border-box"/>

          <label style="color:#94A3B8; font-size:13px">Email Address</label>
          <input type="email" name="email" value="${userEmail}"
                 style="width:100%; padding:10px 14px; background:#0F172A; border:1px solid #334155; border-radius:8px; color:white; margin:6px 0 16px; box-sizing:border-box"/>

          <hr style="border-color:#334155; margin:20px 0"/>

          <p style="color:#6366F1; font-size:12px; font-weight:600; letter-spacing:1px; margin-bottom:12px">CHANGE PASSWORD</p>

          <label style="color:#94A3B8; font-size:13px">Current Password</label>
          <input type="password" name="currentPassword" placeholder="Enter current password"
                 style="width:100%; padding:10px 14px; background:#0F172A; border:1px solid #334155; border-radius:8px; color:white; margin:6px 0 16px; box-sizing:border-box"/>

          <label style="color:#94A3B8; font-size:13px">New Password</label>
          <input type="password" name="newPassword" placeholder="Min 8 characters"
                 style="width:100%; padding:10px 14px; background:#0F172A; border:1px solid #334155; border-radius:8px; color:white; margin:6px 0 16px; box-sizing:border-box"/>

          <hr style="border-color:#334155; margin:20px 0"/>

          <p style="color:#6366F1; font-size:12px; font-weight:600; letter-spacing:1px; margin-bottom:12px">ANALYSIS PREFERENCES</p>

          <label style="color:#94A3B8; font-size:13px">AI Reasoning Speed</label>
          <select name="reasoningEffort"
                  style="width:100%; padding:10px 14px; background:#0F172A; border:1px solid #334155; border-radius:8px; color:white; margin:6px 0 16px; box-sizing:border-box">
            <option value="low" ${reasoningEffort == 'low' ? 'selected' : ''}>⚡ Fast (low reasoning)</option>
            <option value="medium" ${empty reasoningEffort || reasoningEffort == 'medium' ? 'selected' : ''}>⚖ Balanced (medium reasoning)</option>
            <option value="high" ${reasoningEffort == 'high' ? 'selected' : ''}>🧠 Deep (high reasoning)</option>
          </select>

          <div style="display:flex; gap:12px; margin-top:8px">
            <button type="submit" style="flex:1; padding:12px; background:#6366F1; color:white; border:none; border-radius:8px; cursor:pointer; font-weight:600">Save Changes</button>
            <button type="button" onclick="closeSettings()" style="flex:1; padding:12px; background:transparent; color:#94A3B8; border:1px solid #334155; border-radius:8px; cursor:pointer">Cancel</button>
          </div>
        </form>

        <c:if test="${not empty settingsMsg}">
          <div style="margin-top:16px; padding:12px; background:#10B98120; border:1px solid #10B981; border-radius:8px; color:#10B981; text-align:center">
            ${settingsMsg}
          </div>
        </c:if>
      </div>
    </div>

    <script>
        // UI Handling
        window.addEventListener('pageshow', () => {
            document.getElementById('loadingOverlay').style.display = 'none';
            if ("${openSettings}" === "true") openSettings();
        });

        function openSettings() { document.getElementById('settings-overlay').style.display = 'flex'; }
        function closeSettings() { document.getElementById('settings-overlay').style.display = 'none'; }
        document.getElementById('settings-overlay').addEventListener('click', function(e) { if (e.target === this) closeSettings(); });

        const dropZone = document.getElementById('dropZone');
        const fileInput = document.getElementById('resumeFile');
        const fileNameDisplay = document.getElementById('fileNameDisplay');
        const uploadForm = document.getElementById('uploadForm');
        const jobDescTextarea = document.getElementById('jobDescTextarea');
        const jdCount = document.getElementById('jd-count');

        // File Selection
        dropZone.addEventListener('click', (e) => { if (e.target !== fileInput) fileInput.click(); });
        fileInput.addEventListener('change', () => {
            if (fileInput.files.length > 0) {
                fileNameDisplay.innerText = "Selected: " + fileInput.files[0].name;
                fileNameDisplay.style.color = "var(--success)";
                dropZone.style.borderColor = "var(--success)";
            }
        });

        // Drag & Drop
        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eName => {
            dropZone.addEventListener(eName, e => { e.preventDefault(); e.stopPropagation(); }, false);
        });
        ['dragenter', 'dragover'].forEach(eName => {
            dropZone.addEventListener(eName, () => { dropZone.style.borderColor = "var(--accent)"; dropZone.style.background = "rgba(99, 102, 241, 0.05)"; });
        });
        ['dragleave', 'drop'].forEach(eName => {
            dropZone.addEventListener(eName, () => {
                dropZone.style.borderColor = fileInput.files.length > 0 ? "var(--success)" : "rgba(255, 255, 255, 0.1)";
                dropZone.style.background = "rgba(255, 255, 255, 0.02)";
            });
        });
        dropZone.addEventListener('drop', e => {
            fileInput.files = e.dataTransfer.files;
            if (fileInput.files.length > 0) { fileNameDisplay.innerText = "Selected: " + fileInput.files[0].name; fileNameDisplay.style.color = "var(--success)"; }
        });

        // Form Logic
        jobDescTextarea.addEventListener('input', () => { jdCount.textContent = jobDescTextarea.value.length; });
        function resetForm() {
            uploadForm.reset();
            fileNameDisplay.innerText = "Click to choose or drag & drop";
            fileNameDisplay.style.color = "var(--text)";
            dropZone.style.borderColor = "rgba(255, 255, 255, 0.1)";
            jdCount.textContent = "0";
        }
        uploadForm.addEventListener('submit', () => { document.getElementById('loadingOverlay').style.display = 'flex'; });

        // Table Search
        function filterTable() {
            const input = document.getElementById('roleSearch');
            const filter = input.value.toLowerCase();
            const table = document.getElementById('analysesTable');
            const tr = table.getElementsByTagName('tr');
            for (let i = 1; i < tr.length; i++) {
                const td = tr[i].getElementsByClassName('role-cell')[0];
                if (td) { const text = td.textContent || td.innerText; tr[i].style.display = text.toLowerCase().indexOf(filter) > -1 ? "" : "none"; }
            }
        }
        function deleteAllConfirm() { if (confirm("Delete all analyses? This cannot be undone.")) { alert("Feature coming soon! Currently, your data is persisted in XML."); } }

        function toggleTip(id) {
            const el = document.getElementById(id);
            const btn = el.previousElementSibling;
            if (el.style.display === 'none' || !el.style.display) {
                el.style.display = 'block';
                btn.textContent = 'Read Less ↑';
            } else {
                el.style.display = 'none';
                btn.textContent = 'Read More ↓';
            }
        }
    </script>
</body>
</html>
