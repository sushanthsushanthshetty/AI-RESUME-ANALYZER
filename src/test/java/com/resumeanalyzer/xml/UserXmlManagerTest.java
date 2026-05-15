package com.resumeanalyzer.xml;

import com.resumeanalyzer.model.User;
import org.junit.jupiter.api.*;
import java.io.File;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

public class UserXmlManagerTest {
    private static File tempXml;
    private UserXmlManager manager;

    @BeforeEach
    void setUp() throws Exception {
        tempXml = File.createTempFile("users_test", ".xml");
        manager = new UserXmlManager(tempXml.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempXml != null && tempXml.exists()) {
            tempXml.delete();
        }
    }

    @Test
    void testRegisterUser() {
        boolean success = manager.registerUser("testuser", "test@example.com", "password123");
        assertTrue(success, "User should be registered successfully");

        User user = manager.findByUsername("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertNotNull(user.getPasswordHash());
        assertNotEquals("password123", user.getPasswordHash());
    }

    @Test
    void testRegisterDuplicateUser() {
        manager.registerUser("testuser", "test@example.com", "password123");
        boolean success = manager.registerUser("testuser", "other@example.com", "pass");
        assertFalse(success, "Should not be able to register duplicate username");
    }

    @Test
    void testAuthenticateSuccess() {
        manager.registerUser("authuser", "auth@example.com", "secret");
        User user = manager.authenticate("authuser", "secret");
        assertNotNull(user, "Authentication should succeed with correct credentials");
        assertEquals("authuser", user.getUsername());
    }

    @Test
    void testAuthenticateFailure() {
        manager.registerUser("authuser", "auth@example.com", "secret");
        User user = manager.authenticate("authuser", "wrongpassword");
        assertNull(user, "Authentication should fail with wrong password");
        
        user = manager.authenticate("nonexistent", "secret");
        assertNull(user, "Authentication should fail for non-existent user");
    }
}
