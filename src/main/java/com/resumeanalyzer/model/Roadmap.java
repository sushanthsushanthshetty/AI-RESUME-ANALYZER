package com.resumeanalyzer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Roadmap {
    private String id;
    private String userId;
    private String analysisId;
    private String targetRole;
    private List<RoadmapTask> tasks = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int completionPercentage;
    private String status; // "active" | "completed" | "archived"

    public Roadmap() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "active";
    }

    public Roadmap(String id, String userId, String analysisId, String targetRole, List<RoadmapTask> tasks, 
                   LocalDateTime createdAt, LocalDateTime updatedAt, int completionPercentage, String status) {
        this.id = id;
        this.userId = userId;
        this.analysisId = analysisId;
        this.targetRole = targetRole;
        this.tasks = tasks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completionPercentage = completionPercentage;
        this.status = status;
    }

    public void calculateCompletionPercentage() {
        if (tasks == null || tasks.isEmpty()) {
            this.completionPercentage = 0;
            return;
        }
        int completedCount = (int) tasks.stream().filter(RoadmapTask::isCompleted).count();
        this.completionPercentage = (int) (((double) completedCount / tasks.size()) * 100);
        if (this.completionPercentage == 100) {
            this.status = "completed";
        }
    }

    public int getCompletionPercentageInt() {
        return completionPercentage;
    }

    public void markTaskComplete(String taskId) {
        for (RoadmapTask task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setCompleted(true);
                task.setCompletedDate(LocalDateTime.now());
                break;
            }
        }
        calculateCompletionPercentage();
        this.updatedAt = LocalDateTime.now();
    }

    public int getCompletedTaskCount() {
        return (int) tasks.stream().filter(RoadmapTask::isCompleted).count();
    }

    public int getTotalTaskCount() {
        return tasks.size();
    }

    public void addTask(RoadmapTask task) {
        this.tasks.add(task);
        calculateCompletionPercentage();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public List<RoadmapTask> getTasks() { return tasks; }
    public void setTasks(List<RoadmapTask> tasks) { this.tasks = tasks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
