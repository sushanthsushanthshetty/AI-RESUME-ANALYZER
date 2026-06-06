package com.resumeanalyzer.servlet;

import com.resumeanalyzer.model.Roadmap;
import com.resumeanalyzer.xml.RoadmapXmlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RoadmapServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        String roadmapId = req.getParameter("id");

        if (userId == null || roadmapId == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        Roadmap roadmap = RoadmapXmlManager.getRoadmapById(userId, roadmapId);
        
        if (roadmap == null) {
            req.setAttribute("error", "Roadmap not found");
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
            return;
        }

        roadmap.calculateCompletionPercentage();
        RoadmapXmlManager.updateRoadmap(roadmap);

        req.setAttribute("roadmap", roadmap);
        req.getRequestDispatcher("/WEB-INF/views/roadmap.jsp").forward(req, resp);
    }
}
