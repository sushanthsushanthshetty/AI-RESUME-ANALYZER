package com.resumeanalyzer.xml;

import com.resumeanalyzer.model.User;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class UserXmlManager {
    private String xmlPath;

    public UserXmlManager() {
        this.xmlPath = "users.xml";
    }

    public UserXmlManager(String customPath) {
        this.xmlPath = customPath;
    }

    public boolean registerUser(String username, String email, String password) {
        if (findByUsername(username) != null) {
            return false;
        }

        try {
            Document doc = getDocument();
            Element root = doc.getDocumentElement();

            Element userElement = doc.createElement("user");

            Element id = doc.createElement("id");
            id.setTextContent(UUID.randomUUID().toString());
            userElement.appendChild(id);

            Element userNode = doc.createElement("username");
            userNode.setTextContent(username);
            userElement.appendChild(userNode);

            Element passNode = doc.createElement("passwordHash");
            passNode.setTextContent(hashPassword(password));
            userElement.appendChild(passNode);

            Element emailNode = doc.createElement("email");
            emailNode.setTextContent(email);
            userElement.appendChild(emailNode);

            Element dateNode = doc.createElement("createdAt");
            dateNode.setTextContent(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            userElement.appendChild(dateNode);

            root.appendChild(userElement);
            saveDocument(doc);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user != null) {
            String hashedInput = hashPassword(password);
            if (hashedInput.equals(user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    public User findByUsername(String username) {
        try {
            Document doc = getDocument();
            NodeList list = doc.getElementsByTagName("user");
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                String storedUsername = element.getElementsByTagName("username").item(0).getTextContent();
                if (storedUsername.equals(username)) {
                    return mapElementToUser(element);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Document getDocument() throws Exception {
        File file = new File(xmlPath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        if (!file.exists() || file.length() == 0) {
            Document doc = builder.newDocument();
            Element root = doc.createElement("users");
            doc.appendChild(root);
            saveDocument(doc);
            return doc;
        }
        return builder.parse(file);
    }

    private void saveDocument(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlPath));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String userId, String newPassword) {
        try {
            Document doc = getDocument();
            NodeList list = doc.getElementsByTagName("user");
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                String id = element.getElementsByTagName("id").item(0).getTextContent();
                if (id.equals(userId)) {
                    element.getElementsByTagName("passwordHash").item(0).setTextContent(hashPassword(newPassword));
                    saveDocument(doc);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDisplayName(String userId, String displayName) {
        try {
            Document doc = getDocument();
            NodeList list = doc.getElementsByTagName("user");
            for (int i = 0; i < list.getLength(); i++) {
                Element element = (Element) list.item(i);
                String id = element.getElementsByTagName("id").item(0).getTextContent();
                if (id.equals(userId)) {
                    element.getElementsByTagName("username").item(0).setTextContent(displayName);
                    saveDocument(doc);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private User mapElementToUser(Element element) {
        User user = new User();
        user.setId(element.getElementsByTagName("id").item(0).getTextContent());
        user.setUsername(element.getElementsByTagName("username").item(0).getTextContent());
        user.setPasswordHash(element.getElementsByTagName("passwordHash").item(0).getTextContent());
        user.setEmail(element.getElementsByTagName("email").item(0).getTextContent());
        user.setCreatedAt(element.getElementsByTagName("createdAt").item(0).getTextContent());
        return user;
    }
}
