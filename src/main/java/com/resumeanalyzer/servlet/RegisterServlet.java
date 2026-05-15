package com.resumeanalyzer.servlet;

import com.resumeanalyzer.xml.UserXmlManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    private final UserXmlManager userXmlManager = new UserXmlManager();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (isEmpty(username) || isEmpty(email) || isEmpty(password)) {
            req.setAttribute("error", "All fields are required");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (password.length() < 8) {
            req.setAttribute("error", "Password must be at least 8 characters");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            return;
        }

        if (userXmlManager.registerUser(username, email, password)) {
            resp.sendRedirect(req.getContextPath() + "/login?registered=true");
        } else {
            req.setAttribute("error", "Username already exists");
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.isBlank();
    }
}
