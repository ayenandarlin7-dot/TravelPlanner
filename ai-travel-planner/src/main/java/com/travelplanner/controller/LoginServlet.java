package com.travelplanner.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

import com.travelplanner.model.User;
import com.travelplanner.service.UserService;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final UserService userService = new UserService();

    public LoginServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        email = email == null ? "" : email.trim().toLowerCase();

        if (email.isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userService.login(email, password);

            if (user == null) {
                request.setAttribute("errorMessage", "Invalid email or password.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            user.setPasswordHash(null);

            HttpSession session = request.getSession(true);
            session.setAttribute("loggedInUser", user);

            response.sendRedirect(request.getContextPath() + "/dashboard");

        } catch (SQLException exception) {
            exception.printStackTrace();
            request.setAttribute("errorMessage",
                    "Unable to connect to the database. Check that MySQL is running.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}