package com.resumeanalyzer.service;

import com.resumeanalyzer.model.JobListing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JobScraperService {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

    public List<JobListing> fetchJobs(String role, String location, int experience) {
        List<JobListing> jobs = new ArrayList<>();
        
        // Sanitize inputs
        if (role == null || role.isBlank()) role = "Software Engineer";
        if (location == null || location.isBlank()) location = "Remote";
        
        System.out.println("[JobScraper] Searching for: " + role + " in " + location);

        try {
            // 1. Try LinkedIn
            jobs.addAll(scrapeLinkedIn(role, location));
            
            // 2. Try Naukri if we need more
            if (jobs.size() < 4) {
                jobs.addAll(scrapeNaukri(role, location));
            }
        } catch (Exception e) {
            System.err.println("Job scraping process failed: " + e.getMessage());
        }
        
        // 3. Always add Fallback / Manual Search Links if list is empty
        if (jobs.isEmpty()) {
            jobs.add(createFallbackLink("LinkedIn Search", "Click to see live results on LinkedIn", 
                String.format("https://www.linkedin.com/jobs/search/?keywords=%s&location=%s", 
                encode(role), encode(location)), "LinkedIn"));
                
            jobs.add(createFallbackLink("Naukri Search", "Click to see live results on Naukri", 
                String.format("https://www.naukri.com/jobs-%s-in-%s", 
                role.toLowerCase().replaceAll(" ", "-"), location.toLowerCase().replaceAll(" ", "-")), "Naukri"));
                
            jobs.add(createFallbackLink("Google Jobs", "Search across multiple platforms via Google", 
                String.format("https://www.google.com/search?q=jobs+for+%s+in+%s", 
                encode(role), encode(location)), "Google"));
        }
        
        return jobs.size() > 8 ? jobs.subList(0, 8) : jobs;
    }

    private JobListing createFallbackLink(String title, String snippet, String link, String source) {
        return new JobListing(title, "Search Results", "Multiple Locations", "Live", snippet, source, link);
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private List<JobListing> scrapeLinkedIn(String role, String location) {
        List<JobListing> results = new ArrayList<>();
        try {
            String url = String.format("https://www.linkedin.com/jobs/search/?keywords=%s&location=%s", encode(role), encode(location));

            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(8000)
                    .get();

            // LinkedIn's public search page often uses these classes for non-logged in users
            Elements cards = doc.select(".jobs-search__results-list li, .base-search-card");
            for (Element card : cards) {
                if (results.size() >= 4) break;

                String title = card.select(".base-search-card__title, .job-search-card__title").text();
                String company = card.select(".base-search-card__subtitle, .job-search-card__subtitle").text();
                String loc = card.select(".job-search-card__location, .base-search-card__metadata").text();
                String link = card.select("a").attr("href");

                if (!title.isEmpty() && !link.isEmpty()) {
                    results.add(new JobListing(title, company, loc, "Just Now", "View details on LinkedIn", "LinkedIn", link));
                }
            }
        } catch (Exception e) {
            System.err.println("LinkedIn scrape failed (this is common due to bot protection): " + e.getMessage());
        }
        return results;
    }

    private List<JobListing> scrapeNaukri(String role, String location) {
        List<JobListing> results = new ArrayList<>();
        try {
            String slugRole = role.toLowerCase().replaceAll("[^a-z0-9]", "-");
            String slugLoc = location.toLowerCase().replaceAll("[^a-z0-9]", "-");
            String url = String.format("https://www.naukri.com/jobs-%s-in-%s", slugRole, slugLoc);

            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(8000)
                    .get();

            Elements cards = doc.select("article.jobTuple, .cust-job-tuple");
            for (Element card : cards) {
                if (results.size() >= 4) break;

                String title = card.select("a.title, .title").text();
                String company = card.select(".subTitle, .company").text();
                String loc = card.select(".location").text();
                String link = card.select("a.title, a").attr("href");

                if (!title.isEmpty() && !link.isEmpty()) {
                    results.add(new JobListing(title, company, loc, "Recently", "View details on Naukri", "Naukri", link));
                }
            }
        } catch (Exception e) {
            System.err.println("Naukri scrape failed: " + e.getMessage());
        }
        return results;
    }
}
