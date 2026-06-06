package com.resumeanalyzer.xml;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.SkillGap;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ResultXmlManager {
    private String baseResultsPath;

    public ResultXmlManager() {
        this.baseResultsPath = "results/";
    }

    public ResultXmlManager(String customPath) {
        this.baseResultsPath = customPath;
    }

    public void saveResult(String userId, AnalysisResult result) {
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            Document doc = getDocument(resultFile);
            Element root = doc.getDocumentElement();

            Element analysis = doc.createElement("analysis");
            String id = UUID.randomUUID().toString();
            analysis.setAttribute("id", id);
            analysis.setAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            addChild(doc, analysis, "targetRole", result.getTargetRole());
            addChild(doc, analysis, "location", result.getLocation());
            addChild(doc, analysis, "yearsOfExperience", String.valueOf(result.getYearsOfExperience()));
            addChild(doc, analysis, "score", String.valueOf(result.getScore()));
            addChild(doc, analysis, "confidence", result.getConfidence());
            addChild(doc, analysis, "roleFit", result.getRoleFit());
            addChild(doc, analysis, "summary", result.getSummary());

            Element strengths = doc.createElement("strengths");
            for (String s : result.getStrengths()) {
                addChild(doc, strengths, "strength", s);
            }
            analysis.appendChild(strengths);

            Element skillGaps = doc.createElement("skillGaps");
            for (SkillGap gap : result.getSkillGaps()) {
                Element gapElem = doc.createElement("gap");
                gapElem.setAttribute("severity", gap.getSeverity());
                addChild(doc, gapElem, "title", gap.getGap());
                addChild(doc, gapElem, "fix", gap.getFix());
                skillGaps.appendChild(gapElem);
            }
            analysis.appendChild(skillGaps);

            Element keywordMisses = doc.createElement("keywordMisses");
            for (String k : result.getKeywordMisses()) {
                addChild(doc, keywordMisses, "keyword", k);
            }
            analysis.appendChild(keywordMisses);

            Element suggestions = doc.createElement("suggestions");
            for (String s : result.getSuggestions()) {
                addChild(doc, suggestions, "suggestion", s);
            }
            analysis.appendChild(suggestions);

            addChild(doc, analysis, "interviewRisk", result.getInterviewRisk());
            addChild(doc, analysis, "marketEdge", result.getMarketEdge());
            
            if (result.getRoadmapId() != null) {
                addChild(doc, analysis, "roadmapId", result.getRoadmapId());
                addChild(doc, analysis, "roadmapSavedAt", result.getRoadmapSavedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            root.appendChild(analysis);
            saveDocument(doc, resultFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<AnalysisResult> getResultsByUser(String userId) {
        List<AnalysisResult> results = new ArrayList<>();
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            if (!resultFile.exists()) return results;

            Document doc = getDocument(resultFile);
            NodeList list = doc.getElementsByTagName("analysis");
            for (int i = 0; i < list.getLength(); i++) {
                results.add(mapElementToResult((Element) list.item(i)));
            }
            results.sort(Comparator.comparing(AnalysisResult::getTimestamp).reversed());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public AnalysisResult getLatestResult(String userId) {
        List<AnalysisResult> results = getResultsByUser(userId);
        return results.isEmpty() ? null : results.get(0);
    }

    public AnalysisResult getResultById(String userId, String analysisId) {
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            if (!resultFile.exists()) return null;

            Document doc = getDocument(resultFile);
            NodeList list = doc.getElementsByTagName("analysis");
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                if (element.getAttribute("id").equals(analysisId)) {
                    return mapElementToResult(element);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document getDocument(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (!file.exists() || file.length() == 0) {
            file.getParentFile().mkdirs();
            Document doc = builder.newDocument();
            Element root = doc.createElement("analyses");
            doc.appendChild(root);
            return doc;
        }
        return builder.parse(file);
    }

    private void saveDocument(Document doc, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
            StreamResult result = new StreamResult(fos);
            transformer.transform(source, result);
        }
    }

    public void saveRoadmap(String userId, String analysisId, String roadmapJson) {
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            Document doc = getDocument(resultFile);
            NodeList list = doc.getElementsByTagName("analysis");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                if (el.getAttribute("id").equals(analysisId)) {
                    // Remove existing roadmap if any
                    NodeList existing = el.getElementsByTagName("roadmapJson");
                    for (int j = 0; j < existing.getLength(); j++) {
                        el.removeChild(existing.item(j));
                    }
                    addChild(doc, el, "roadmapJson", roadmapJson);
                    saveDocument(doc, resultFile);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAnalysis(String userId, AnalysisResult result) {
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            Document doc = getDocument(resultFile);
            NodeList list = doc.getElementsByTagName("analysis");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                if (el.getAttribute("id").equals(result.getId())) {
                    // Update roadmap info
                    updateOrAddChild(doc, el, "roadmapId", result.getRoadmapId());
                    if (result.getRoadmapSavedAt() != null) {
                        updateOrAddChild(doc, el, "roadmapSavedAt", result.getRoadmapSavedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                    saveDocument(doc, resultFile);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOrAddChild(Document doc, Element parent, String name, String value) {
        NodeList nl = parent.getElementsByTagName(name);
        if (nl.getLength() > 0) {
            nl.item(0).setTextContent(value != null ? value : "");
        } else {
            addChild(doc, parent, name, value);
        }
    }

    public String getRoadmapJson(String userId, String analysisId) {
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            if (!resultFile.exists()) return null;

            Document doc = getDocument(resultFile);
            NodeList list = doc.getElementsByTagName("analysis");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                if (el.getAttribute("id").equals(analysisId)) {
                    return getText(el, "roadmapJson");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addChild(Document doc, Element parent, String name, String value) {
        Element child = doc.createElement(name);
        child.setTextContent(value != null ? value : "");
        parent.appendChild(child);
    }

    private AnalysisResult mapElementToResult(Element element) {
        AnalysisResult result = new AnalysisResult();
        result.setId(element.getAttribute("id"));
        result.setTimestamp(element.getAttribute("timestamp"));
        result.setTargetRole(getText(element, "targetRole"));
        result.setLocation(getText(element, "location"));
        String yoe = getText(element, "yearsOfExperience");
        result.setYearsOfExperience(yoe.isEmpty() ? 0 : Integer.parseInt(yoe));
        result.setScore(Integer.parseInt(getText(element, "score")));
        result.setConfidence(getText(element, "confidence"));
        result.setRoleFit(getText(element, "roleFit"));
        result.setSummary(getText(element, "summary"));

        result.setStrengths(getList(element, "strengths", "strength"));
        result.setKeywordMisses(getList(element, "keywordMisses", "keyword"));
        result.setSuggestions(getList(element, "suggestions", "suggestion"));

        NodeList gaps = element.getElementsByTagName("gap");
        List<SkillGap> skillGaps = new ArrayList<>();
        for (int i = 0; i < gaps.getLength(); i++) {
            Element gapElem = (Element) gaps.item(i);
            SkillGap gap = new SkillGap();
            gap.setSeverity(gapElem.getAttribute("severity"));
            gap.setGap(getText(gapElem, "title"));
            gap.setFix(getText(gapElem, "fix"));
            skillGaps.add(gap);
        }
        result.setSkillGaps(skillGaps);

        result.setInterviewRisk(getText(element, "interviewRisk"));
        result.setMarketEdge(getText(element, "marketEdge"));

        String rid = getText(element, "roadmapId");
        if (!rid.isEmpty()) {
            result.setRoadmapId(rid);
            String rsa = getText(element, "roadmapSavedAt");
            if (!rsa.isEmpty()) {
                result.setRoadmapSavedAt(LocalDateTime.parse(rsa, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
        }

        return result;
    }

    private String getText(Element parent, String name) {
        NodeList nl = parent.getElementsByTagName(name);
        if (nl.getLength() > 0) return nl.item(0).getTextContent();
        return "";
    }

    private List<String> getList(Element parent, String containerName, String itemName) {
        List<String> list = new ArrayList<>();
        Element container = (Element) parent.getElementsByTagName(containerName).item(0);
        if (container != null) {
            NodeList nl = container.getElementsByTagName(itemName);
            for (int i = 0; i < nl.getLength(); i++) {
                list.add(nl.item(i).getTextContent());
            }
        }
        return list;
    }

    public boolean deleteResult(String userId, String resultId) {
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            if (!resultFile.exists()) return false;

            Document doc = getDocument(resultFile);
            NodeList list = doc.getElementsByTagName("analysis");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                if (el.getAttribute("id").equals(resultId)) {
                    el.getParentNode().removeChild(el);
                    saveDocument(doc, resultFile);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllResults(String userId) {
        try {
            File resultFile = new File(baseResultsPath + userId + ".xml");
            if (resultFile.exists()) {
                return resultFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
