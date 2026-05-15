package com.resumeanalyzer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class AnalysisResult {
    private String id;
    private String timestamp;
    private String targetRole;

    @JsonProperty("years_of_experience")
    private int yearsOfExperience;

    @JsonProperty("location")
    private String location;

    @JsonProperty("score")
    private int score;

    @JsonProperty("confidence")
    private String confidence;

    @JsonProperty("role_fit")
    private String roleFit;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("strengths")
    private List<String> strengths = new ArrayList<>();

    @JsonProperty("skill_gaps")
    private List<SkillGap> skillGaps = new ArrayList<>();

    @JsonProperty("keyword_misses")
    private List<String> keywordMisses = new ArrayList<>();

    @JsonProperty("suggestions")
    private List<String> suggestions = new ArrayList<>();

    @JsonProperty("interview_risk")
    private String interviewRisk;

    @JsonProperty("market_edge")
    private String marketEdge;

    public String getScoreGrade() {
        if (score >= 90) return "A";
        if (score >= 70) return "B";
        if (score >= 50) return "C";
        if (score >= 30) return "D";
        return "F";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    public String getRoleFit() { return roleFit; }
    public void setRoleFit(String roleFit) { this.roleFit = roleFit; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<SkillGap> getSkillGaps() { return skillGaps; }
    public void setSkillGaps(List<SkillGap> skillGaps) { this.skillGaps = skillGaps; }

    public List<String> getKeywordMisses() { return keywordMisses; }
    public void setKeywordMisses(List<String> keywordMisses) { this.keywordMisses = keywordMisses; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public String getInterviewRisk() { return interviewRisk; }
    public void setInterviewRisk(String interviewRisk) { this.interviewRisk = interviewRisk; }

    public String getMarketEdge() { return marketEdge; }
    public void setMarketEdge(String marketEdge) { this.marketEdge = marketEdge; }
}
