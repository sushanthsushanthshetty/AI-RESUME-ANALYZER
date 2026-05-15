package com.resumeanalyzer.servlet;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.SkillGap;
import com.resumeanalyzer.xml.ResultXmlManager;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UploadFlowTest {

    @Test
    void testAnalysisResultGradeLogic() {
        AnalysisResult res = new AnalysisResult();
        
        res.setScore(95);
        assertEquals("A", res.getScoreGrade());
        
        res.setScore(75);
        assertEquals("B", res.getScoreGrade());
        
        res.setScore(55);
        assertEquals("C", res.getScoreGrade());
        
        res.setScore(35);
        assertEquals("D", res.getScoreGrade());
        
        res.setScore(10);
        assertEquals("F", res.getScoreGrade());
    }

    @Test
    void testFullPersistenceFlow() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "upload_test");
        tempDir.mkdirs();
        ResultXmlManager manager = new ResultXmlManager(tempDir.getAbsolutePath() + "/");
        
        AnalysisResult result = new AnalysisResult();
        result.setScore(88);
        result.setTargetRole("AI Engineer");
        result.setSummary("Great potential.");
        result.getStrengths().add("Python expertise");
        
        SkillGap gap = new SkillGap();
        gap.setGap("Cloud Computing");
        gap.setSeverity("major");
        gap.setFix("Take AWS course");
        result.getSkillGaps().add(gap);
        
        manager.saveResult("user-456", result);
        
        AnalysisResult loaded = manager.getLatestResult("user-456");
        assertNotNull(loaded);
        assertEquals(88, loaded.getScore());
        assertEquals("B", loaded.getScoreGrade());
        assertEquals(1, loaded.getStrengths().size());
        assertEquals("Python expertise", loaded.getStrengths().get(0));
        assertEquals(1, loaded.getSkillGaps().size());
        assertEquals("Cloud Computing", loaded.getSkillGaps().get(0).getGap());
        
        // Cleanup
        for (File f : tempDir.listFiles()) f.delete();
        tempDir.delete();
    }
}
