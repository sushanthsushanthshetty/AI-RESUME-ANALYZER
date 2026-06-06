package com.resumeanalyzer.xml;

import com.resumeanalyzer.model.Roadmap;
import com.resumeanalyzer.model.RoadmapTask;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RoadmapXmlManager {
    private static final String ROADMAPS_DIR = getDataDir() + "/roadmaps";

    public static String getDataDir() {
        String dataDir = System.getenv("DATA_DIR");
        if (dataDir == null || dataDir.isEmpty()) {
            dataDir = System.getProperty("user.home") + "/apex-data";
        }
        return dataDir;
    }

    public static Roadmap saveRoadmap(Roadmap roadmap) {
        if (roadmap.getId() == null) {
            roadmap.setId(UUID.randomUUID().toString());
        }
        try {
            File dir = new File(ROADMAPS_DIR);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(ROADMAPS_DIR + "/" + roadmap.getUserId() + ".xml");
            Document doc = getDocument(file);
            Element root = doc.getDocumentElement();

            // Check if roadmap already exists
            NodeList list = doc.getElementsByTagName("roadmap");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                if (el.getAttribute("id").equals(roadmap.getId())) {
                    root.removeChild(el);
                    break;
                }
            }

            Element roadmapEl = doc.createElement("roadmap");
            roadmapEl.setAttribute("id", roadmap.getId());
            roadmapEl.setAttribute("analysisId", roadmap.getAnalysisId());
            roadmapEl.setAttribute("status", roadmap.getStatus());

            addChild(doc, roadmapEl, "targetRole", roadmap.getTargetRole());
            addChild(doc, roadmapEl, "createdAt", roadmap.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            addChild(doc, roadmapEl, "updatedAt", roadmap.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            addChild(doc, roadmapEl, "completionPercentage", String.valueOf(roadmap.getCompletionPercentage()));

            Element tasksEl = doc.createElement("tasks");
            for (RoadmapTask task : roadmap.getTasks()) {
                Element taskEl = doc.createElement("task");
                taskEl.setAttribute("id", task.getId());
                taskEl.setAttribute("isCompleted", String.valueOf(task.isCompleted()));
                
                addChild(doc, taskEl, "skill", task.getSkill());
                addChild(doc, taskEl, "severity", task.getSeverity());
                addChild(doc, taskEl, "description", task.getDescription());
                addChild(doc, taskEl, "action", task.getAction());
                if (task.getStartDate() != null)
                    addChild(doc, taskEl, "startDate", task.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                if (task.getTargetDate() != null)
                    addChild(doc, taskEl, "targetDate", task.getTargetDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                if (task.getCompletedDate() != null)
                    addChild(doc, taskEl, "completedDate", task.getCompletedDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                addChild(doc, taskEl, "progressPercentage", String.valueOf(task.getProgressPercentage()));

                Element milestonesEl = doc.createElement("milestones");
                for (String m : task.getMilestones()) {
                    addChild(doc, milestonesEl, "milestone", m);
                }
                taskEl.appendChild(milestonesEl);

                Element completedMilestonesEl = doc.createElement("completedMilestones");
                for (String m : task.getCompletedMilestones()) {
                    addChild(doc, completedMilestonesEl, "milestone", m);
                }
                taskEl.appendChild(completedMilestonesEl);

                tasksEl.appendChild(taskEl);
            }
            roadmapEl.appendChild(tasksEl);
            root.appendChild(roadmapEl);

            saveDocument(doc, file);
            return roadmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Roadmap getRoadmapById(String userId, String roadmapId) {
        try {
            File file = new File(ROADMAPS_DIR + "/" + userId + ".xml");
            if (!file.exists()) return null;

            Document doc = getDocument(file);
            NodeList list = doc.getElementsByTagName("roadmap");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                if (el.getAttribute("id").equals(roadmapId)) {
                    return mapElementToRoadmap(el, userId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Roadmap> getRoadmapsByUser(String userId) {
        List<Roadmap> roadmaps = new ArrayList<>();
        try {
            File file = new File(ROADMAPS_DIR + "/" + userId + ".xml");
            if (!file.exists()) return roadmaps;

            Document doc = getDocument(file);
            NodeList list = doc.getElementsByTagName("roadmap");
            for (int i = 0; i < list.getLength(); i++) {
                roadmaps.add(mapElementToRoadmap((Element) list.item(i), userId));
            }
            roadmaps.sort((r1, r2) -> r2.getUpdatedAt().compareTo(r1.getUpdatedAt()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roadmaps;
    }

    public static boolean updateRoadmap(Roadmap roadmap) {
        return saveRoadmap(roadmap) != null;
    }

    public static boolean deleteRoadmap(String userId, String roadmapId) {
        try {
            File file = new File(ROADMAPS_DIR + "/" + userId + ".xml");
            if (!file.exists()) return false;

            Document doc = getDocument(file);
            NodeList list = doc.getElementsByTagName("roadmap");
            for (int i = 0; i < list.getLength(); i++) {
                Element el = (Element) list.item(i);
                if (el.getAttribute("id").equals(roadmapId)) {
                    el.getParentNode().removeChild(el);
                    saveDocument(doc, file);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static RoadmapTask updateTaskProgress(String userId, String roadmapId, String taskId, int progressPercentage) {
        Roadmap roadmap = getRoadmapById(userId, roadmapId);
        if (roadmap == null) return null;

        for (RoadmapTask task : roadmap.getTasks()) {
            if (task.getId().equals(taskId)) {
                task.setProgressPercentage(progressPercentage);
                roadmap.setUpdatedAt(LocalDateTime.now());
                roadmap.calculateCompletionPercentage();
                updateRoadmap(roadmap);
                return task;
            }
        }
        return null;
    }

    public static RoadmapTask markTaskComplete(String userId, String roadmapId, String taskId) {
        Roadmap roadmap = getRoadmapById(userId, roadmapId);
        if (roadmap == null) return null;

        for (RoadmapTask task : roadmap.getTasks()) {
            if (task.getId().equals(taskId)) {
                task.setCompleted(true);
                task.setCompletedDate(LocalDateTime.now());
                task.setProgressPercentage(100);
                roadmap.setUpdatedAt(LocalDateTime.now());
                roadmap.calculateCompletionPercentage();
                updateRoadmap(roadmap);
                return task;
            }
        }
        return null;
    }

    private static Roadmap mapElementToRoadmap(Element el, String userId) {
        Roadmap r = new Roadmap();
        r.setId(el.getAttribute("id"));
        r.setUserId(userId);
        r.setAnalysisId(el.getAttribute("analysisId"));
        r.setStatus(el.getAttribute("status"));
        r.setTargetRole(getText(el, "targetRole"));
        r.setCreatedAt(LocalDateTime.parse(getText(el, "createdAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        r.setUpdatedAt(LocalDateTime.parse(getText(el, "updatedAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        r.setCompletionPercentage(Integer.parseInt(getText(el, "completionPercentage")));

        NodeList taskList = el.getElementsByTagName("task");
        List<RoadmapTask> tasks = new ArrayList<>();
        for (int i = 0; i < taskList.getLength(); i++) {
            Element tEl = (Element) taskList.item(i);
            RoadmapTask t = new RoadmapTask();
            t.setId(tEl.getAttribute("id"));
            t.setCompleted(Boolean.parseBoolean(tEl.getAttribute("isCompleted")));
            t.setSkill(getText(tEl, "skill"));
            t.setSeverity(getText(tEl, "severity"));
            t.setDescription(getText(tEl, "description"));
            t.setAction(getText(tEl, "action"));
            
            String sd = getText(tEl, "startDate");
            if (!sd.isEmpty()) t.setStartDate(LocalDateTime.parse(sd, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            String td = getText(tEl, "targetDate");
            if (!td.isEmpty()) t.setTargetDate(LocalDateTime.parse(td, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            String cd = getText(tEl, "completedDate");
            if (!cd.isEmpty()) t.setCompletedDate(LocalDateTime.parse(cd, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            t.setProgressPercentage(Integer.parseInt(getText(tEl, "progressPercentage")));

            t.setMilestones(getList(tEl, "milestones", "milestone"));
            t.setCompletedMilestones(getList(tEl, "completedMilestones", "milestone"));
            tasks.add(t);
        }
        r.setTasks(tasks);
        return r;
    }

    private static Document getDocument(File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (!file.exists() || file.length() == 0) {
            Document doc = builder.newDocument();
            Element root = doc.createElement("roadmaps");
            doc.appendChild(root);
            return doc;
        }
        return builder.parse(file);
    }

    private static void saveDocument(Document doc, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private static void addChild(Document doc, Element parent, String name, String value) {
        Element child = doc.createElement(name);
        child.setTextContent(value);
        parent.appendChild(child);
    }

    private static String getText(Element parent, String name) {
        NodeList nl = parent.getElementsByTagName(name);
        if (nl.getLength() > 0) return nl.item(0).getTextContent();
        return "";
    }

    private static List<String> getList(Element parent, String containerName, String itemName) {
        List<String> list = new ArrayList<>();
        NodeList containers = parent.getElementsByTagName(containerName);
        if (containers.getLength() > 0) {
            Element container = (Element) containers.item(0);
            NodeList nl = container.getElementsByTagName(itemName);
            for (int i = 0; i < nl.getLength(); i++) {
                list.add(nl.item(i).getTextContent());
            }
        }
        return list;
    }
}
