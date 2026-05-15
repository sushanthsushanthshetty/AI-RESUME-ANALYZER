package com.resumeanalyzer.servlet;

import com.resumeanalyzer.model.User;
import com.resumeanalyzer.xml.ResultXmlManager;
import com.resumeanalyzer.xml.UserXmlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SettingsServlet extends HttpServlet {
    private final UserXmlManager userXmlManager = new UserXmlManager();
    private final ResultXmlManager resultXmlManager = new ResultXmlManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        String currentUsername = (String) session.getAttribute("username");

        String displayName = req.getParameter("displayName");
        String email = req.getParameter("email");
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String reasoningEffort = req.getParameter("reasoningEffort");

        try {
            // Password Change Logic
            if (currentPassword != null && !currentPassword.isBlank() && newPassword != null && !newPassword.isBlank()) {
                User user = userXmlManager.authenticate(currentUsername, currentPassword);
                if (user == null) {
                    req.setAttribute("settingsMsg", "Current password is wrong");
                } else {
                    userXmlManager.updatePassword(userId, newPassword);
                }
            }

            // Display Name Update
            if (displayName != null && !displayName.isBlank()) {
                userXmlManager.updateDisplayName(userId, displayName);
                session.setAttribute("username", displayName);
            }

            // Reasoning Effort Preference
            if (reasoningEffort != null && !reasoningEffort.isBlank()) {
                session.setAttribute("reasoningEffort", reasoningEffort);
            }

            if (req.getAttribute("settingsMsg") == null) {
                req.setAttribute("settingsMsg", "Settings saved successfully!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("settingsMsg", "Error saving settings: " + e.getMessage());
        }

        req.setAttribute("openSettings", "true");
        req.setAttribute("username", session.getAttribute("username"));
        req.setAttribute("results", resultXmlManager.getResultsByUser(userId));
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
