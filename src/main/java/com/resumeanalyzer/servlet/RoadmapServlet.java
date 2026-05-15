package com.resumeanalyzer.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.Roadmap;
import com.resumeanalyzer.service.RoadmapService;
import com.resumeanalyzer.xml.ResultXmlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RoadmapServlet extends HttpServlet {
    private final ResultXmlManager resultXmlManager = new ResultXmlManager();
    private final RoadmapService roadmapService = new RoadmapService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        String analysisId = req.getParameter("id");

        if (userId == null || analysisId == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        try {
            // 1. Check if roadmap already exists in XML
            String existingJson = resultXmlManager.getRoadmapJson(userId, analysisId);
            Roadmap roadmap;

            if (existingJson != null && !existingJson.isBlank()) {
                roadmap = objectMapper.readValue(existingJson, Roadmap.class);
            } else {
                // 2. Generate new roadmap
                AnalysisResult analysis = resultXmlManager.getResultById(userId, analysisId);
                if (analysis == null) {
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                    return;
                }
                
                String effort = (String) session.getAttribute("reasoningEffort");
                roadmap = roadmapService.generateRoadmap(analysis, effort);
                
                // 3. Save to XML
                resultXmlManager.saveRoadmap(userId, analysisId, objectMapper.writeValueAsString(roadmap));
            }

            req.setAttribute("roadmap", roadmap);
            req.setAttribute("analysisId", analysisId);
            req.getRequestDispatcher("/WEB-INF/views/roadmap.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to generate roadmap: " + e.getMessage());
            req.getRequestDispatcher("/result?id=" + analysisId).forward(req, resp);
        }
    }
}
