package com.resumeanalyzer.ai;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.SkillGap;
import java.util.*;

public class DomainGuard {

    // PRIMARY DOMAIN SKILL MAPS
    // Key   = domain keyword to detect in targetRole
    // Value = required skills that MUST appear in resume text
    private static final Map<String, List<String>> DOMAIN_REQUIRED_SKILLS = new LinkedHashMap<>();

    static {
        DOMAIN_REQUIRED_SKILLS.put("ai", Arrays.asList(
            "tensorflow", "pytorch", "scikit", "machine learning",
            "deep learning", "nlp", "neural", "model training",
            "pandas", "numpy", "keras", "computer vision"
        ));
        DOMAIN_REQUIRED_SKILLS.put("ml", Arrays.asList(
            "tensorflow", "pytorch", "scikit", "machine learning",
            "deep learning", "model training", "pandas", "numpy"
        ));
        DOMAIN_REQUIRED_SKILLS.put("data scientist", Arrays.asList(
            "python", "pandas", "numpy", "scikit", "matplotlib",
            "machine learning", "statistics", "regression"
        ));
        DOMAIN_REQUIRED_SKILLS.put("data engineer", Arrays.asList(
            "spark", "hadoop", "kafka", "etl", "airflow",
            "data pipeline", "hive", "snowflake", "databricks"
        ));
        DOMAIN_REQUIRED_SKILLS.put("frontend", Arrays.asList(
            "react", "angular", "vue", "html", "css",
            "javascript", "typescript", "ui", "ux"
        ));
        DOMAIN_REQUIRED_SKILLS.put("android", Arrays.asList(
            "android", "kotlin", "java android", "android sdk",
            "jetpack", "gradle mobile"
        ));
        DOMAIN_REQUIRED_SKILLS.put("ios", Arrays.asList(
            "swift", "objective-c", "xcode", "ios sdk",
            "cocoapods", "uikit", "swiftui"
        ));
        DOMAIN_REQUIRED_SKILLS.put("devops", Arrays.asList(
            "kubernetes", "terraform", "ansible", "jenkins",
            "prometheus", "grafana", "helm", "infrastructure"
        ));
        DOMAIN_REQUIRED_SKILLS.put("cybersecurity", Arrays.asList(
            "penetration testing", "vulnerability", "siem",
            "firewall", "ethical hacking", "owasp", "cryptography"
        ));
        DOMAIN_REQUIRED_SKILLS.put("blockchain", Arrays.asList(
            "solidity", "ethereum", "smart contract", "web3",
            "hyperledger", "nft", "defi"
        ));
    }

    // Minimum % of domain skills found in resume to pass
    private static final double PASS_THRESHOLD = 0.40;

    // Maximum score allowed on domain mismatch
    private static final int MISMATCH_MAX_SCORE = 30;

    /**
     * Call this after Mercury 2 returns a result.
     * If domain mismatch detected → hard override score and fields.
     */
    public static AnalysisResult validate(AnalysisResult result, String targetRole, String resumeText) {
        String role = targetRole.toLowerCase();
        String resume = resumeText.toLowerCase();

        // Find which domain the target role belongs to
        List<String> requiredSkills = null;
        String detectedDomain = null;

        for (Map.Entry<String, List<String>> entry : DOMAIN_REQUIRED_SKILLS.entrySet()) {
            if (role.contains(entry.getKey())) {
                requiredSkills = entry.getValue();
                detectedDomain = entry.getKey().toUpperCase();
                break;
            }
        }

        // No domain rule found → return result unchanged
        if (requiredSkills == null) return result;

        // Count how many required skills appear in the resume
        long found = requiredSkills.stream()
            .filter(resume::contains)
            .count();

        double coverage = (double) found / requiredSkills.size();

        System.out.printf(
            "[DomainGuard] Role=%s | Domain=%s | Skills found=%d/%d | Coverage=%.0f%%\n",
            targetRole, detectedDomain,
            found, requiredSkills.size(), coverage * 100
        );

        // PASS — coverage meets threshold → return unchanged
        if (coverage >= PASS_THRESHOLD) return result;

        // FAIL — hard override all fields
        System.out.println("[DomainGuard] MISMATCH DETECTED — overriding score");

        result.setScore(Math.min(result.getScore(), MISMATCH_MAX_SCORE));
        result.setRoleFit("Mismatch");
        result.setConfidence("high");
        result.setSummary(
            "DOMAIN MISMATCH DETECTED: This resume is from a "
            + detectCurrentDomain(resume)
            + " background and does not meet the primary skill "
            + "requirements for a " + targetRole + " role. "
            + "Only " + (int)(coverage * 100) + "% of required "
            + detectedDomain + " domain skills were found."
        );

        // Build critical skill gaps for every missing domain skill
        List<SkillGap> gaps = new ArrayList<>();
        for (String skill : requiredSkills) {
            if (!resume.contains(skill)) {
                SkillGap gap = new SkillGap();
                gap.setGap(skill);
                gap.setSeverity("critical");
                gap.setFix("Complete a hands-on 30-day project using "
                    + skill + " and publish it on GitHub before applying.");
                gaps.add(gap);
                if (gaps.size() >= 5) break;
            }
        }
        result.setSkillGaps(gaps);
        result.setInterviewRisk(
            "Candidate will be screened out immediately due to absence "
            + "of core " + detectedDomain + " domain skills."
        );
        result.setMarketEdge("NONE");

        return result;
    }

    private static String detectCurrentDomain(String resume) {
        if (resume.contains("spring boot") || resume.contains("java"))
            return "Java Backend";
        if (resume.contains("react") || resume.contains("angular"))
            return "Frontend";
        if (resume.contains("android") || resume.contains("kotlin"))
            return "Android";
        if (resume.contains("aws") || resume.contains("devops"))
            return "DevOps";
        return "unrelated";
    }
}
