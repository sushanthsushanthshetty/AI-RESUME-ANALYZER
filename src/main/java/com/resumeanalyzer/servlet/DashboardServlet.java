package com.resumeanalyzer.servlet;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.xml.ResultXmlManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class DashboardServlet extends HttpServlet {
    private final ResultXmlManager resultXmlManager = new ResultXmlManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        List<AnalysisResult> results = resultXmlManager.getResultsByUser(userId);
        req.setAttribute("results", results);
        req.setAttribute("username", username);

        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}
