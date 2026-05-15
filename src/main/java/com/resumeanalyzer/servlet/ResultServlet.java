package com.resumeanalyzer.servlet;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.xml.ResultXmlManager;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResultServlet extends HttpServlet {
    private final ResultXmlManager resultXmlManager = new ResultXmlManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        String id = req.getParameter("id");

        AnalysisResult result;
        if (id != null && !id.isBlank()) {
            result = resultXmlManager.getResultById(userId, id);
        } else {
            result = resultXmlManager.getLatestResult(userId);
        }

        // Fetch Recommended Jobs
        if (result != null) {
            com.resumeanalyzer.service.JobScraperService jobService = new com.resumeanalyzer.service.JobScraperService();
            List<com.resumeanalyzer.model.JobListing> recommendedJobs = jobService.fetchJobs(
                result.getTargetRole(), 
                result.getLocation(), 
                result.getYearsOfExperience()
            );
            req.setAttribute("recommendedJobs", recommendedJobs);
        }

        String format = req.getParameter("format");
        if ("xml".equals(format)) {
            try {
                resp.setContentType("application/xml");
                resp.setCharacterEncoding("UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"analysis-" + id + ".xml\"");
                
                File xmlFile = new File("src/main/resources/results/" + userId + ".xml");
                if (xmlFile.exists()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(xmlFile);
                    
                    NodeList list = doc.getElementsByTagName("analysis");
                    for (int i = 0; i < list.getLength(); i++) {
                        Element el = (Element) list.item(i);
                        if (el.getAttribute("id").equals(id)) {
                            TransformerFactory tf = TransformerFactory.newInstance();
                            Transformer t = tf.newTransformer();
                            t.setOutputProperty(OutputKeys.INDENT, "yes");
                            t.transform(new DOMSource(el), new StreamResult(resp.getWriter()));
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        req.setAttribute("result", result);
        req.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(req, resp);
    }
}
