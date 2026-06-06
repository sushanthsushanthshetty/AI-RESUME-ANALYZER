<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - APEX</title>
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

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg);
            color: var(--text);
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
        }

        .login-card {
            background-color: var(--card);
            width: 400px;
            padding: 2.5rem;
            border-radius: 1rem;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
            border: 1px solid rgba(255, 255, 255, 0.1);
        }

        .logo-container {
            text-align: center;
            margin-bottom: 2rem;
        }

        .logo {
            font-size: 2.5rem;
            font-weight: 800;
            color: var(--accent);
            letter-spacing: -0.05em;
        }

        .subtitle {
            font-size: 0.875rem;
            color: var(--text-muted);
            margin-top: 0.25rem;
        }

        .form-group {
            margin-bottom: 1.25rem;
        }

        input {
            width: 100%;
            padding: 0.75rem 1rem;
            background-color: rgba(15, 23, 42, 0.5);
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 0.5rem;
            color: white;
            font-size: 1rem;
            outline: none;
        }

        input:focus {
            border-color: var(--accent);
        }

        button {
            width: 100%;
            padding: 0.75rem;
            background-color: var(--accent);
            color: white;
            border: none;
            border-radius: 0.5rem;
            font-weight: 600;
            font-size: 1rem;
            cursor: pointer;
            transition: opacity 0.2s;
            margin-top: 0.5rem;
        }

        button:hover {
            opacity: 0.9;
        }

        .alert {
            padding: 0.75rem 1rem;
            border-radius: 0.5rem;
            margin-bottom: 1.25rem;
            font-size: 0.875rem;
        }

        .alert-error {
            background-color: rgba(239, 68, 68, 0.1);
            border: 1px solid var(--danger);
            color: var(--danger);
        }

        .footer-link {
            text-align: center;
            margin-top: 1.5rem;
            font-size: 0.875rem;
            color: var(--text-muted);
        }

        .footer-link a {
            color: var(--accent);
            text-decoration: none;
            font-weight: 600;
        }
    </style>
</head>
<body>

    <div class="login-card">
        <div class="logo-container">
            <div class="logo">APEX</div>
            <div class="subtitle">Resume Intelligence</div>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form action="<%= request.getContextPath() %>/register" method="post">
            <div class="form-group">
                <input type="text" name="username" placeholder="Username" required>
            </div>
            <div class="form-group">
                <input type="email" name="email" placeholder="Email" required>
            </div>
            <div class="form-group">
                <input type="password" name="password" placeholder="Password (min 8 chars)" minlength="8" required>
            </div>
            <div class="form-group">
                <input type="password" name="confirmPassword" placeholder="Confirm Password" required>
            </div>
            <button type="submit">Create Account</button>
        </form>

        <div class="footer-link">
            Have an account? <a href="<%= request.getContextPath() %>/login">Sign in →</a>
        </div>
    </div>

</body>
</html>
