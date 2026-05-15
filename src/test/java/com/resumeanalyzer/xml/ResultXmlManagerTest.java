package com.resumeanalyzer.xml;

import com.resumeanalyzer.model.AnalysisResult;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ResultXmlManagerTest {
    private static File tempDir;
    private ResultXmlManager manager;
    private final String userId = "test-user-123";

    @BeforeEach
    void setUp() throws Exception {
        tempDir = new File(System.getProperty("java.io.tmpdir"), "results_test");
        tempDir.mkdirs();
        manager = new ResultXmlManager(tempDir.getAbsolutePath() + "/");
    }

    @AfterEach
    void tearDown() {
        if (tempDir != null && tempDir.exists()) {
            for (File f : tempDir.listFiles()) f.delete();
            tempDir.delete();
        }
    }

    @Test
    void testSaveAndRetrieveResults() throws InterruptedException {
        AnalysisResult r1 = new AnalysisResult();
        r1.setTargetRole("Java Developer");
        r1.setScore(85);
        manager.saveResult(userId, r1);
        
        Thread.sleep(10); // Ensure timestamp difference
        
        AnalysisResult r2 = new AnalysisResult();
        r2.setTargetRole("React Developer");
        r2.setScore(40);
        manager.saveResult(userId, r2);
        
        Thread.sleep(10);
        
        AnalysisResult r3 = new AnalysisResult();
        r3.setTargetRole("Python Developer");
        r3.setScore(60);
        manager.saveResult(userId, r3);

        List<AnalysisResult> results = manager.getResultsByUser(userId);
        assertEquals(3, results.size());
        
        // DESC order (r3, r2, r1)
        assertEquals("Python Developer", results.get(0).getTargetRole());
        assertEquals("Java Developer", results.get(2).getTargetRole());
        
        AnalysisResult latest = manager.getLatestResult(userId);
        assertEquals("Python Developer", latest.getTargetRole());
        assertEquals(60, latest.getScore());
    }
}
