package com.resumeanalyzer.parser;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.servlet.http.Part;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ResumeParser {

    public String parse(Part filePart) throws IOException {
        if (filePart == null || filePart.getSize() == 0) {
            throw new FileNotFoundException("Upload part is empty");
        }

        String fileName = getFileName(filePart);
        String content;

        if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
            content = parsePdf(filePart.getInputStream());
        } else if (fileName != null && fileName.toLowerCase().endsWith(".txt")) {
            content = parseTxt(filePart.getInputStream());
        } else {
            throw new IllegalArgumentException("Only .pdf and .txt supported");
        }

        return cleanText(content);
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String cd : contentDisp.split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private String parsePdf(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        try (PDDocument document = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document).trim();
            
            if (text.isEmpty()) {
                throw new IllegalArgumentException(
                    "Your PDF appears to be image-based or scanned. " +
                    "Please export your resume as a text-based PDF " +
                    "from Word, Google Docs, or Canva — or upload a .txt file instead."
                );
            }
            return text;
        }
    }

    private String parseTxt(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String cleanText(String text) {
        if (text == null) return "";
        // Remove trailing whitespace on each line, then collapse more than 2 blank lines (3+ newlines) into 2 blank lines (3 newlines)
        return text.trim()
                .replaceAll("(?m)[ \\t]+$", "")
                .replaceAll("(\\n\\s*){4,}", "\n\n\n");
    }
}
