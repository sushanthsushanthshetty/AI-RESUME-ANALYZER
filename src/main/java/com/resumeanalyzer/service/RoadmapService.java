package com.resumeanalyzer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.ai.MercuryApiClient;
import com.resumeanalyzer.ai.PromptBuilder;
import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.Roadmap;
import com.resumeanalyzer.util.ConfigManager;

import java.io.IOException;

public class RoadmapService {
    private final ObjectMapper objectMapper = new ObjectMapper()
        .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public Roadmap generateRoadmap(AnalysisResult result, String effort) throws Exception {
        String apiKey = ConfigManager.get("INCEPTION_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new Exception("API Key not configured");
        }

        MercuryApiClient client = new MercuryApiClient(apiKey, effort != null ? effort : "medium");
        String systemPrompt = PromptBuilder.getRoadmapSystemPrompt();
        String userPrompt = PromptBuilder.buildRoadmapUserPrompt(result);

        String rawResponse = client.sendPrompt(systemPrompt, userPrompt);
        return parseRoadmapJson(rawResponse);
    }

    private Roadmap parseRoadmapJson(String rawResponse) throws IOException {
        String json = rawResponse.trim();
        if (json.startsWith("```")) {
            json = json.replaceAll("^```[a-zA-Z]*\\n?", "");
            json = json.replaceAll("```$", "");
            json = json.trim();
        }
        if (!json.startsWith("{")) {
            int start = json.indexOf("{");
            int end = json.lastIndexOf("}");
            if (start != -1 && end != -1) {
                json = json.substring(start, end + 1);
            }
        }
        return objectMapper.readValue(json, Roadmap.class);
    }
}
