package com.resumeanalyzer.model;

public class JobListing {
    private String title;
    private String company;
    private String location;
    private String postedDate;
    private String descriptionSnippet;
    private String source;
    private String applyLink;

    public JobListing() {}

    public JobListing(String title, String company, String location, String postedDate, String descriptionSnippet, String source, String applyLink) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.postedDate = postedDate;
        this.descriptionSnippet = descriptionSnippet;
        this.source = source;
        this.applyLink = applyLink;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPostedDate() { return postedDate; }
    public void setPostedDate(String postedDate) { this.postedDate = postedDate; }

    public String getDescriptionSnippet() { return descriptionSnippet; }
    public void setDescriptionSnippet(String descriptionSnippet) { this.descriptionSnippet = descriptionSnippet; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getApplyLink() { return applyLink; }
    public void setApplyLink(String applyLink) { this.applyLink = applyLink; }
}
