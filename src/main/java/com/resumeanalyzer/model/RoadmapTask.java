package com.resumeanalyzer.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoadmapTask {
    private String id;
    private String skill;
    private String severity;
    private String description;
    private String action;
    private LocalDateTime startDate;
    private LocalDateTime targetDate;
    private LocalDateTime completedDate;
    private boolean isCompleted;
    private int progressPercentage;
    private List<String> milestones = new ArrayList<>();
    private List<String> completedMilestones = new ArrayList<>();

    public RoadmapTask() {
        this.id = UUID.randomUUID().toString();
    }

    public RoadmapTask(String id, String skill, String severity, String description, String action, 
                       LocalDateTime startDate, LocalDateTime targetDate, LocalDateTime completedDate, 
                       boolean isCompleted, int progressPercentage, List<String> milestones, 
                       List<String> completedMilestones) {
        this.id = id;
        this.skill = skill;
        this.severity = severity;
        this.description = description;
        this.action = action;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.completedDate = completedDate;
        this.isCompleted = isCompleted;
        this.progressPercentage = progressPercentage;
        this.milestones = milestones;
        this.completedMilestones = completedMilestones;
    }

    public int getProgressPercentage() {
        if (isCompleted) return 100;
        if (milestones == null || milestones.isEmpty()) return progressPercentage;
        return (int) (((double) completedMilestones.size() / milestones.size()) * 100);
    }

    public long getRemainingDays() {
        if (targetDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDateTime.now(), targetDate);
    }

    public boolean isOverdue() {
        return !isCompleted && LocalDateTime.now().isAfter(targetDate);
    }

    public String getStatus() {
        if (isCompleted) return "Completed";
        if (isOverdue()) return "At Risk";
        return "On Track";
    }

    public void markMilestoneComplete(String milestone) {
        if (!completedMilestones.contains(milestone)) {
            completedMilestones.add(milestone);
        }
        if (completedMilestones.size() == milestones.size()) {
            isCompleted = true;
            completedDate = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSkill() { return skill; }
    public void setSkill(String skill) { this.skill = skill; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDateTime targetDate) { this.targetDate = targetDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public List<String> getMilestones() { return milestones; }
    public void setMilestones(List<String> milestones) { this.milestones = milestones; }

    public List<String> getCompletedMilestones() { return completedMilestones; }
    public void setCompletedMilestones(List<String> completedMilestones) { this.completedMilestones = completedMilestones; }
}
