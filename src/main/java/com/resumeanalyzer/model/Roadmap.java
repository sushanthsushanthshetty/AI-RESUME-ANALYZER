package com.resumeanalyzer.model;

import java.util.List;

public class Roadmap {
    private String roadmapId;
    private String targetRole;
    private String totalDuration;
    private int totalHours;
    private String estimatedCost;
    private List<SkillEntry> skills;
    private List<TimelineItem> timeline;
    private MotivationalMetrics motivationalMetrics;
    private List<String> riskMitigation;
    private List<String> successChecklist;

    // Getters and Setters
    public String getRoadmapId() { return roadmapId; }
    public void setRoadmapId(String roadmapId) { this.roadmapId = roadmapId; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getTotalDuration() { return totalDuration; }
    public void setTotalDuration(String totalDuration) { this.totalDuration = totalDuration; }

    public int getTotalHours() { return totalHours; }
    public void setTotalHours(int totalHours) { this.totalHours = totalHours; }

    public String getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(String estimatedCost) { this.estimatedCost = estimatedCost; }

    public List<SkillEntry> getSkills() { return skills; }
    public void setSkills(List<SkillEntry> skills) { this.skills = skills; }

    public List<TimelineItem> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineItem> timeline) { this.timeline = timeline; }

    public MotivationalMetrics getMotivationalMetrics() { return motivationalMetrics; }
    public void setMotivationalMetrics(MotivationalMetrics motivationalMetrics) { this.motivationalMetrics = motivationalMetrics; }

    public List<String> getRiskMitigation() { return riskMitigation; }
    public void setRiskMitigation(List<String> riskMitigation) { this.riskMitigation = riskMitigation; }

    public List<String> getSuccessChecklist() { return successChecklist; }
    public void setSuccessChecklist(List<String> successChecklist) { this.successChecklist = successChecklist; }

    public static class SkillEntry {
        private String skillName;
        private String category;
        private int currentLevel;
        private int targetLevel;
        private int importanceScore;
        private List<Phase> phases;

        // Getters and Setters
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public int getCurrentLevel() { return currentLevel; }
        public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }

        public int getTargetLevel() { return targetLevel; }
        public void setTargetLevel(int targetLevel) { this.targetLevel = targetLevel; }

        public int getImportanceScore() { return importanceScore; }
        public void setImportanceScore(int importanceScore) { this.importanceScore = importanceScore; }

        public List<Phase> getPhases() { return phases; }
        public void setPhases(List<Phase> phases) { this.phases = phases; }
    }

    public static class Phase {
        private String phaseName;
        private String duration;
        private int hours;
        private List<Resource> resources;
        private String milestone;

        // Getters and Setters
        public String getPhaseName() { return phaseName; }
        public void setPhaseName(String phaseName) { this.phaseName = phaseName; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }

        public int getHours() { return hours; }
        public void setHours(int hours) { this.hours = hours; }

        public List<Resource> getResources() { return resources; }
        public void setResources(List<Resource> resources) { this.resources = resources; }

        public String getMilestone() { return milestone; }
        public void setMilestone(String milestone) { this.milestone = milestone; }
    }

    public static class Resource {
        private String name;
        private String type;
        private String price;
        private String link;
        private String duration;
        private String rating;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }

        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }

        public String getRating() { return rating; }
        public void setRating(String rating) { this.rating = rating; }
    }

    public static class TimelineItem {
        private int week;
        private String focus;
        private List<String> tasks;
        private String expectedOutcome;

        // Getters and Setters
        public int getWeek() { return week; }
        public void setWeek(int week) { this.week = week; }

        public String getFocus() { return focus; }
        public void setFocus(String focus) { this.focus = focus; }

        public List<String> getTasks() { return tasks; }
        public void setTasks(List<String> tasks) { this.tasks = tasks; }

        public String getExpectedOutcome() { return expectedOutcome; }
        public void setExpectedOutcome(String expectedOutcome) { this.expectedOutcome = expectedOutcome; }
    }

    public static class MotivationalMetrics {
        private String scoreImprovement;
        private String successProbability;
        private String salaryGrowth;
        private String completionRate;

        // Getters and Setters
        public String getScoreImprovement() { return scoreImprovement; }
        public void setScoreImprovement(String scoreImprovement) { this.scoreImprovement = scoreImprovement; }

        public String getSuccessProbability() { return successProbability; }
        public void setSuccessProbability(String successProbability) { this.successProbability = successProbability; }

        public String getSalaryGrowth() { return salaryGrowth; }
        public void setSalaryGrowth(String salaryGrowth) { this.salaryGrowth = salaryGrowth; }

        public String getCompletionRate() { return completionRate; }
        public void setCompletionRate(String completionRate) { this.completionRate = completionRate; }
    }
}
