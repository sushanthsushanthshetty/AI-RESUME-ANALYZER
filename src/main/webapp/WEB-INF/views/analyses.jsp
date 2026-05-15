<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.resumeanalyzer.model.AnalysisResult" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Analyses - APEX</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@latest/tabler-icons.min.css">
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
            --sidebar-w: 240px;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Inter', sans-serif; background-color: var(--bg); color: var(--text); display: flex; min-height: 100vh; }

        /* Sidebar Styles (Copy from dashboard) */
        .sidebar {
            width: var(--sidebar-w); background-color: var(--card); border-right: 1px solid rgba(255, 255, 255, 0.05);
            padding: 2rem 1.25rem; position: fixed; height: 100vh; display: flex; flex-direction: column; z-index: 100;
        }
        .sidebar-brand { margin-bottom: 2.5rem; }
        .sidebar-brand h1 { font-size: 1.5rem; font-weight: 800; color: var(--accent); letter-spacing: -0.02em; }
        .sidebar-brand p { font-size: 0.75rem; color: var(--text-muted); font-weight: 500; text-transform: uppercase; }
        .nav-item { 
            display: flex; align-items: center; gap: 0.75rem; padding: 0.75rem 1rem; color: var(--text-muted); 
            text-decoration: none; border-radius: 0.5rem; margin-bottom: 0.4rem; font-weight: 500; transition: all 0.2s; 
        }
        .nav-item.active { background-color: rgba(99, 102, 241, 0.1); color: var(--accent); }
        .nav-item:hover:not(.active) { color: var(--text); background-color: rgba(255, 255, 255, 0.05); }

        .main-content { margin-left: var(--sidebar-w); flex: 1; padding: 2rem 3rem; max-width: 1400px; }
        .page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; }
        .page-title { font-size: 1.75rem; font-weight: 800; display: flex; align-items: center; gap: 0.75rem; }
        
        .btn-back { padding: 0.6rem 1.25rem; background: rgba(255, 255, 255, 0.05); border: 1px solid rgba(255, 255, 255, 0.1); color: var(--text); border-radius: 0.5rem; text-decoration: none; font-weight: 600; font-size: 0.875rem; transition: all 0.2s; }
        .btn-back:hover { background: rgba(255, 255, 255, 0.1); }

        /* Filter Bar */
        .filter-bar { 
            background: var(--card); padding: 1.25rem; border-radius: 1rem; margin-bottom: 2rem;
            display: flex; gap: 1rem; flex-wrap: wrap; align-items: center; border: 1px solid rgba(255, 255, 255, 0.05);
        }
        .search-input { 
            flex: 1; min-width: 250px; padding: 0.75rem 1rem; background: var(--bg); 
            border: 1px solid rgba(255, 255, 255, 0.1); border-radius: 0.5rem; color: white; outline: none;
        }
        select { 
            padding: 0.75rem 1rem; background: var(--bg); border: 1px solid rgba(255, 255, 255, 0.1); 
            border-radius: 0.5rem; color: white; outline: none; cursor: pointer;
        }

        /* Results Grid */
        .results-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.5rem; }
        .result-card { 
            background: var(--card); border-radius: 1rem; border: 1px solid rgba(255, 255, 255, 0.05);
            padding: 1.5rem; transition: transform 0.2s, border-color 0.2s; position: relative;
        }
        .result-card:hover { transform: translateY(-3px); border-color: var(--accent); }
        
        .role-name { font-size: 1.125rem; font-weight: 700; margin-bottom: 0.25rem; color: white; }
        .result-date { font-size: 0.75rem; color: var(--text-muted); margin-bottom: 1rem; }
        
        .badges { display: flex; gap: 0.5rem; margin-bottom: 1rem; }
        .badge { padding: 0.25rem 0.6rem; border-radius: 999px; font-size: 0.75rem; font-weight: 700; }
        
        .score-circle { 
            position: absolute; top: 1.5rem; right: 1.5rem; width: 50px; height: 50px; 
            border-radius: 50%; border: 3px solid; display: flex; align-items: center; 
            justify-content: center; font-weight: 800; font-size: 0.875rem;
        }

        .summary-text { font-size: 0.875rem; color: var(--text-muted); line-height: 1.5; margin-bottom: 1.5rem; }
        
        .btn-view-full { 
            display: inline-flex; align-items: center; gap: 0.5rem; color: var(--accent); 
            text-decoration: none; font-weight: 700; font-size: 0.875rem;
        }
        .btn-view-full:hover { text-decoration: underline; }

        .empty-state { text-align: center; padding: 5rem 2rem; color: var(--text-muted); }
    </style>
</head>
<body>

    <div class="sidebar">
        <div class="sidebar-brand"><h1>APEX</h1><p>Resume Intelligence</p></div>
        <nav>
            <a href="<%= request.getContextPath() %>/dashboard" class="nav-item"><i class="ti ti-layout-dashboard"></i> Dashboard</a>
            <a href="<%= request.getContextPath() %>/analyses" class="nav-item active"><i class="ti ti-folder"></i> My Analyses</a>
            <a href="<%= request.getContextPath() %>/dashboard#tips" class="nav-item"><i class="ti ti-bulb"></i> Career Tips</a>
            <a href="<%= request.getContextPath() %>/dashboard#stats" class="nav-item"><i class="ti ti-chart-line"></i> My Stats</a>
        </nav>
    </div>

    <div class="main-content">
        <div class="page-header">
            <h1 class="page-title"><i class="ti ti-folder"></i> My Analyses</h1>
            <a href="<%= request.getContextPath() %>/dashboard" class="btn-back">← Back to Dashboard</a>
        </div>

        <div class="filter-bar">
            <input type="text" id="searchInput" class="search-input" placeholder="Search by role name..." onkeyup="applyFilters()">
            <select id="gradeFilter" onchange="applyFilters()">
                <option value="All">All Grades</option>
                <option value="A">Grade A</option>
                <option value="B">Grade B</option>
                <option value="C">Grade C</option>
                <option value="D">Grade D</option>
                <option value="F">Grade F</option>
            </select>
            <select id="fitFilter" onchange="applyFilters()">
                <option value="All">All Fits</option>
                <option value="Strong">Strong Fit</option>
                <option value="Moderate">Moderate Fit</option>
                <option value="Weak">Weak Fit</option>
                <option value="Mismatch">Mismatch</option>
            </select>
            <select id="sortOrder" onchange="applyFilters()">
                <option value="newest">Newest First</option>
                <option value="oldest">Oldest First</option>
                <option value="highest">Highest Score</option>
                <option value="lowest">Lowest Score</option>
            </select>
        </div>

        <%
            List<AnalysisResult> results = (List<AnalysisResult>) request.getAttribute("results");
            if (results == null || results.isEmpty()) {
        %>
            <div class="empty-state">
                <i class="ti ti-clipboard" style="font-size: 4rem; display: block; margin-bottom: 1rem;"></i>
                <p>No analyses yet — go analyze your first resume!</p>
            </div>
        <% } else { %>
            <div class="results-grid" id="resultsGrid">
                <% for (AnalysisResult res : results) { 
                    String scoreColor = res.getScore() >= 70 ? "var(--success)" : (res.getScore() >= 50 ? "var(--warning)" : "var(--danger)");
                    String fitColor = "Strong".equals(res.getRoleFit()) ? "var(--success)" : ("Moderate".equals(res.getRoleFit()) ? "var(--warning)" : "var(--danger)");
                    String summary = res.getSummary();
                    if (summary.length() > 100) summary = summary.substring(0, 97) + "...";
                %>
                <div class="result-card" 
                     data-role="<%= res.getTargetRole().toLowerCase() %>" 
                     data-grade="<%= res.getScoreGrade() %>" 
                     data-fit="<%= res.getRoleFit() %>"
                     data-score="<%= res.getScore() %>"
                     data-timestamp="<%= res.getTimestamp() %>">
                    
                    <div class="score-circle" style="border-color: <%= scoreColor %>; color: <%= scoreColor %>">
                        <%= res.getScore() %>%
                    </div>
                    
                    <h3 class="role-name"><%= res.getTargetRole() %></h3>
                    <div class="result-date"><%= res.getTimestamp().replace("T", " ") %></div>
                    
                    <div class="badges">
                        <span class="badge" style="background: rgba(255,255,255,0.05); color: white;">Grade <%= res.getScoreGrade() %></span>
                        <span class="badge" style="background: rgba(<%= fitColor.contains("success") ? "16, 185, 129" : (fitColor.contains("warning") ? "245, 158, 11" : "239, 68, 68") %>, 0.1); color: <%= fitColor %>">
                            <%= res.getRoleFit() %>
                        </span>
                    </div>
                    
                    <p class="summary-text"><%= summary %></p>
                    
                    <a href="<%= request.getContextPath() %>/result?id=<%= res.getId() %>" class="btn-view-full">
                        View Full Report <i class="ti ti-arrow-right"></i>
                    </a>
                </div>
                <% } %>
            </div>
        <% } %>
    </div>

    <script>
        function applyFilters() {
            const search = document.getElementById('searchInput').value.toLowerCase();
            const grade = document.getElementById('gradeFilter').value;
            const fit = document.getElementById('fitFilter').value;
            const sort = document.getElementById('sortOrder').value;
            
            const grid = document.getElementById('resultsGrid');
            if (!grid) return;
            const cards = Array.from(grid.getElementsByClassName('result-card'));

            cards.forEach(card => {
                const cardRole = card.getAttribute('data-role');
                const cardGrade = card.getAttribute('data-grade');
                const cardFit = card.getAttribute('data-fit');

                const matchesSearch = cardRole.includes(search);
                const matchesGrade = grade === 'All' || cardGrade === grade;
                const matchesFit = fit === 'All' || cardFit === fit;

                if (matchesSearch && matchesGrade && matchesFit) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });

            // Sort logic
            const visibleCards = cards.filter(c => c.style.display !== 'none');
            visibleCards.sort((a, b) => {
                if (sort === 'newest') return b.getAttribute('data-timestamp').localeCompare(a.getAttribute('data-timestamp'));
                if (sort === 'oldest') return a.getAttribute('data-timestamp').localeCompare(b.getAttribute('data-timestamp'));
                if (sort === 'highest') return b.getAttribute('data-score') - a.getAttribute('data-score');
                if (sort === 'lowest') return a.getAttribute('data-score') - b.getAttribute('data-score');
                return 0;
            });

            visibleCards.forEach(c => grid.appendChild(c));
        }
    </script>
</body>
</html>
