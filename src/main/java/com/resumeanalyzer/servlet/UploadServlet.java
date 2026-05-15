package com.resumeanalyzer.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.ai.MercuryApiClient;
import com.resumeanalyzer.ai.PromptBuilder;
import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.parser.ResumeParser;
import com.resumeanalyzer.xml.ResultXmlManager;
import com.resumeanalyzer.util.ConfigManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;

@MultipartConfig(maxFileSize = 10485760) // 10MB
public class UploadServlet extends HttpServlet {
    private final ResumeParser resumeParser = new ResumeParser();
    private final ResultXmlManager resultXmlManager = new ResultXmlManager();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/dashboard");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");

        try {
            Part filePart = req.getPart("resumeFile");
            String targetRole = req.getParameter("targetRole");
            String jobDesc = req.getParameter("jobDesc");

            if (filePart == null || filePart.getSize() == 0 || isBlank(targetRole) || isBlank(jobDesc)) {
                throw new Exception("All fields and a valid resume file are required");
            }

            // 1. Parse Resume
            String resumeText = resumeParser.parse(filePart);

            // 2. Build Prompts
            String systemPrompt = PromptBuilder.getSystemPrompt();
            String userPrompt = PromptBuilder.buildUserPrompt(resumeText, targetRole, jobDesc);

            // 3. Call AI
            String apiKey = ConfigManager.get("INCEPTION_API_KEY");
            if (apiKey == null || apiKey.isBlank() || apiKey.equals("your_actual_api_key_here")) {
                throw new Exception("Server config error: INCEPTION_API_KEY not set. Please update src/main/resources/config.properties with your API key.");
            }
            String effort = (String) session.getAttribute("reasoningEffort");
            if (effort == null) effort = "medium";
            MercuryApiClient client = new MercuryApiClient(apiKey, effort);
            String rawResponse = client.sendPrompt(systemPrompt, userPrompt);
            
            System.out.println("=== RAW MERCURY 2 RESPONSE ===");
            System.out.println(rawResponse);
            System.out.println("=== END RESPONSE ===");

            // 4. Parse JSON Response
            AnalysisResult result = mapJsonToResult(rawResponse);
            result.setTargetRole(targetRole); 

            // 5. Run Domain Guard Override
            result = com.resumeanalyzer.ai.DomainGuard.validate(result, targetRole, resumeText);

            // 6. Save Result
            resultXmlManager.saveResult(userId, result);

            AnalysisResult savedResult = resultXmlManager.getLatestResult(userId);
            resp.sendRedirect(req.getContextPath() + "/result?id=" + savedResult.getId());

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            req.setAttribute("username", session.getAttribute("username"));
            req.setAttribute("results", resultXmlManager.getResultsByUser(userId));
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private AnalysisResult mapJsonToResult(String rawResponse) throws IOException {
        String json = rawResponse.trim();
        
        // ISSUE 2: Fix JSON fence stripping
        if (json.startsWith("```")) {
            json = json.replaceAll("^```[a-zA-Z]*\\n?", "");
            json = json.replaceAll("```$", "");
            json = json.trim();
        }
        if (!json.startsWith("{")) {
            int start = json.indexOf("{");
            int end   = json.lastIndexOf("}");
            if (start != -1 && end != -1) {
                json = json.substring(start, end + 1);
            }
        }

        return objectMapper.readValue(json, AnalysisResult.class);
    }
}
