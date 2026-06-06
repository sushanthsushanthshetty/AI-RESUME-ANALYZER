package com.resumeanalyzer.servlet;

import com.resumeanalyzer.xml.ResultXmlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class DeleteAnalysisServlet extends HttpServlet {
    private ResultXmlManager resultXmlManager = new ResultXmlManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        String resultId = req.getParameter("id");
        String action = req.getParameter("action");

        if ("all".equals(action)) {
            resultXmlManager.deleteAllResults(userId);
        } else if (resultId != null && !resultId.trim().isEmpty()) {
            resultXmlManager.deleteResult(userId, resultId);
        }

        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }
}
