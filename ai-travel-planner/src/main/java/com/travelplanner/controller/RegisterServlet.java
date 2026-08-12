package com.travelplanner.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

import com.travelplanner.model.User;
import com.travelplanner.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final UserService userService = new UserService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.getRequestDispatcher("/register.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String fullName = clean(request.getParameter("fullName"));

		String email = clean(request.getParameter("email")).toLowerCase(Locale.ROOT);

		String password = request.getParameter("password");

		String confirmPassword = request.getParameter("confirmPassword");

		if (fullName.isEmpty() || email.isEmpty() || password == null || password.isBlank() || confirmPassword == null
				|| confirmPassword.isBlank()) {

			forwardError(request, response, "All fields are required.");
			return;
		}

		if (!isValidEmail(email)) {
			forwardError(request, response, "Please enter a valid email address.");
			return;
		}

		if (password.length() < 6) {
			forwardError(request, response, "Password must contain at least 6 characters.");
			return;
		}

		if (!password.equals(confirmPassword)) {
			forwardError(request, response, "Passwords do not match.");
			return;
		}

		User user = new User();
		user.setFullName(fullName);
		user.setEmail(email);

		/*
		 * UserService.register() ထဲမှာ password ကို SHA-256 hash
		 * ပြောင်းပေးမှာဖြစ်ပါတယ်။
		 */
		user.setPasswordHash(password);

		try {
			boolean registered = userService.register(user);

			if (registered) {
				response.sendRedirect(request.getContextPath() + "/login.jsp?register=success");
			} else {
				forwardError(request, response, "This email address is already registered.");
			}

		} catch (SQLException exception) {
			exception.printStackTrace();
			forwardError(request, response,
					"Unable to connect to the database. Check that MySQL is running and update database.properties.");
		}
	}

	private String clean(String value) {
		return value == null ? "" : value.trim();
	}

	private boolean isValidEmail(String email) {
		return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	}

	private void forwardError(HttpServletRequest request, HttpServletResponse response, String message)
			throws ServletException, IOException {

		request.setAttribute("errorMessage", message);

		request.getRequestDispatcher("/register.jsp").forward(request, response);
	}
}