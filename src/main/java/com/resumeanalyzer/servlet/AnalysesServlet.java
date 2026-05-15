package com.resumeanalyzer.servlet;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.xml.ResultXmlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class AnalysesServlet extends HttpServlet {
    private final ResultXmlManager resultXmlManager = new ResultXmlManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<AnalysisResult> list = resultXmlManager.getResultsByUser(userId);
        req.setAttribute("results", list);
        req.setAttribute("username", session.getAttribute("username"));
        
        req.getRequestDispatcher("/WEB-INF/views/analyses.jsp").forward(req, resp);
    }
}
