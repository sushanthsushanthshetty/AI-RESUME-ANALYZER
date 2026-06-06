package com.resumeanalyzer.servlet;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.Roadmap;
import com.resumeanalyzer.model.RoadmapTask;
import com.resumeanalyzer.model.SkillGap;
import com.resumeanalyzer.xml.ResultXmlManager;
import com.resumeanalyzer.xml.RoadmapXmlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class SaveRoadmapServlet extends HttpServlet {
    private final ResultXmlManager resultXmlManager = new ResultXmlManager();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        String analysisId = req.getParameter("analysisId");
        String targetRole = req.getParameter("targetRole");

        if (userId == null || analysisId == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        AnalysisResult analysis = resultXmlManager.getResultById(userId, analysisId);
        if (analysis == null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
            return;
        }

        if (analysis.getRoadmapId() != null) {
            resp.sendRedirect(req.getContextPath() + "/roadmap?id=" + analysis.getRoadmapId());
            return;
        }

        Roadmap roadmap = new Roadmap();
        roadmap.setUserId(userId);
        roadmap.setAnalysisId(analysisId);
        roadmap.setTargetRole(targetRole != null ? targetRole : analysis.getTargetRole());

        for (SkillGap gap : analysis.getSkillGaps()) {
            RoadmapTask task = new RoadmapTask();
            task.setSkill(gap.getGap());
            task.setSeverity(gap.getSeverity());
            task.setDescription(gap.getFix());
            task.setAction(gap.getFix());
            task.setStartDate(LocalDateTime.now());
            task.setTargetDate(LocalDateTime.now().plusDays(30));
            task.setCompleted(false);
            task.setProgressPercentage(0);
            
            // Add some default milestones
            task.getMilestones().add("Review conceptual documentation");
            task.getMilestones().add("Complete hands-on exercise");
            task.getMilestones().add("Apply to a small project");
            
            roadmap.addTask(task);
        }

        RoadmapXmlManager.saveRoadmap(roadmap);

        analysis.setRoadmapId(roadmap.getId());
        analysis.setRoadmapSavedAt(LocalDateTime.now());
        resultXmlManager.updateAnalysis(userId, analysis);

        resp.sendRedirect(req.getContextPath() + "/roadmap?id=" + roadmap.getId());
    }
}
