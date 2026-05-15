package com.resumeanalyzer.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.resumeanalyzer.util.ConfigManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MercuryApiClient {
    private static final String BASE_URL = "https://api.inceptionlabs.ai/v1/chat/completions";
    private static final String MODEL = "mercury-2";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final String apiKey;
    private final String reasoningEffort;
    private final HttpClient httpClient;

    public static String getApiKeyFromEnv() {
        String key = ConfigManager.get("INCEPTION_API_KEY");
        if (key == null || key.isBlank() || key.equals("your_actual_api_key_here")) {
            throw new IllegalStateException("INCEPTION_API_KEY is missing. " +
                    "Please set the INCEPTION_API_KEY in src/main/resources/config.properties or as an environment variable.");
        }
        return key;
    }

    public MercuryApiClient(String apiKey, String reasoningEffort) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("your_actual_api_key_here")) {
            throw new IllegalStateException("INCEPTION_API_KEY is missing. " +
                    "Please set the INCEPTION_API_KEY in src/main/resources/config.properties or as an environment variable.");
        }
        this.apiKey = apiKey;
        this.reasoningEffort = reasoningEffort;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static MercuryApiClient forDeepAnalysis(String apiKey) {
        return new MercuryApiClient(apiKey, "high");
    }

    public String sendPrompt(String systemPrompt, String userPrompt) {
        try {
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("model", MODEL);
            rootNode.put("max_tokens", 16000);
            rootNode.put("temperature", 0.75);
            rootNode.put("reasoning_effort", reasoningEffort);

            ArrayNode messages = rootNode.putArray("messages");
            messages.addObject().put("role", "system").put("content", systemPrompt);
            messages.addObject().put("role", "user").put("content", userPrompt);

            String requestBody = mapper.writeValueAsString(rootNode);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(120))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String errorBody = response.body();
                if (errorBody.length() > 500) {
                    errorBody = errorBody.substring(0, 500) + "...";
                }
                throw new RuntimeException("API error: Status " + response.statusCode() + " - " + errorBody);
            }

            System.out.println("--- DEBUG: FULL API RESPONSE ---");
            System.out.println(response.body());
            System.out.println("--------------------------------");

            JsonNode responseJson = mapper.readTree(response.body());
            String content = responseJson.path("choices").get(0).path("message").path("content").asText().trim();
            
            if (content.isEmpty()) {
                throw new RuntimeException("API returned 200 OK but the message content is empty. Full response: " + response.body());
            }
            return content;

        } catch (Exception e) {
            if (e instanceof RuntimeException) throw (RuntimeException) e;
            throw new RuntimeException("Failed to send prompt to Mercury API", e);
        }
    }
}
