package com.resumeanalyzer.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.model.Roadmap;
import com.resumeanalyzer.model.RoadmapTask;
import com.resumeanalyzer.xml.RoadmapXmlManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateTaskProgressServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        
        String roadmapId = req.getParameter("roadmapId");
        String taskId = req.getParameter("taskId");
        String action = req.getParameter("action");
        String progressStr = req.getParameter("progressPercentage");
        String milestone = req.getParameter("milestone");

        resp.setContentType("application/json");
        Map<String, Object> response = new HashMap<>();

        if (userId == null || roadmapId == null || taskId == null) {
            response.put("success", false);
            response.put("message", "Invalid request parameters");
            resp.getWriter().write(objectMapper.writeValueAsString(response));
            return;
        }

        RoadmapTask updatedTask = null;

        if ("complete".equals(action)) {
            updatedTask = RoadmapXmlManager.markTaskComplete(userId, roadmapId, taskId);
        } else if ("progress".equals(action)) {
            int progress = progressStr != null ? Integer.parseInt(progressStr) : 0;
            updatedTask = RoadmapXmlManager.updateTaskProgress(userId, roadmapId, taskId, progress);
        } else if ("milestone".equals(action) && milestone != null) {
            Roadmap roadmap = RoadmapXmlManager.getRoadmapById(userId, roadmapId);
            if (roadmap != null) {
                for (RoadmapTask t : roadmap.getTasks()) {
                    if (t.getId().equals(taskId)) {
                        t.markMilestoneComplete(milestone);
                        roadmap.calculateCompletionPercentage();
                        RoadmapXmlManager.updateRoadmap(roadmap);
                        updatedTask = t;
                        break;
                    }
                }
            }
        }

        if (updatedTask != null) {
            Roadmap roadmap = RoadmapXmlManager.getRoadmapById(userId, roadmapId);
            response.put("success", true);
            response.put("taskId", updatedTask.getId());
            response.put("progressPercentage", updatedTask.getProgressPercentage());
            response.put("isCompleted", updatedTask.isCompleted());
            response.put("completionPercentage", roadmap.getCompletionPercentage());
            response.put("status", updatedTask.getStatus());
            response.put("completedCount", roadmap.getCompletedTaskCount());
        } else {
            response.put("success", false);
            response.put("message", "Task or Roadmap not found");
        }

        resp.getWriter().write(objectMapper.writeValueAsString(response));
    }
}
