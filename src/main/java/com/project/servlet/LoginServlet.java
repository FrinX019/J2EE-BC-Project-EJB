package com.project.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String role = req.getParameter("role");

        if (username == null || username.isBlank()) {
            resp.sendRedirect("index.jsp");
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("username", username.trim());
        session.setAttribute("role", role);

        // Route to the right JSP based on role
        if ("WORKER".equals(role)) {
            resp.sendRedirect("worker.jsp");
        } else {
            resp.sendRedirect("user.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect("index.jsp");
    }
}
