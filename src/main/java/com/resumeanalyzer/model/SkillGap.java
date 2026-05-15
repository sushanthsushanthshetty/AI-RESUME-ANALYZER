package com.resumeanalyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillGap {

    @JsonProperty("gap")
    private String gap;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("fix")
    private String fix;

    public SkillGap() {}

    public String getGap()      { return gap; }
    public String getSeverity() { return severity; }
    public String getFix()      { return fix; }

    public void setGap(String gap)          { this.gap = gap; }
    public void setSeverity(String severity){ this.severity = severity; }
    public void setFix(String fix)          { this.fix = fix; }
}
