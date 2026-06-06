package com.resumeanalyzer.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class SessionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        String uri = httpRequest.getRequestURI();

        boolean protectedRoute = uri.contains("/dashboard") || uri.contains("/upload") || uri.contains("/result") || uri.contains("/settings") || uri.contains("/analyses") || uri.contains("/roadmap") || uri.contains("/save-roadmap") || uri.contains("/update-task-progress");

        if (protectedRoute && (session == null || session.getAttribute("userId") == null)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
}
