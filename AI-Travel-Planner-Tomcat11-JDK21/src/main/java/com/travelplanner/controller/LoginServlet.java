package com.travelplanner.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import com.travelplanner.model.User;
import com.travelplanner.service.UserService;
import com.travelplanner.util.RememberMeUtil;

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

		try {

			Optional<User> rememberedUser = findRememberedUser(request);

			if (rememberedUser.isPresent()) {

				User user = rememberedUser.get();

				user.setPasswordHash(null);

				HttpSession session = request.getSession(true);

				session.setAttribute("loggedInUser", user);

				response.sendRedirect(request.getContextPath() + "/dashboard");

				return;
			}

		} catch (SQLException exception) {
			exception.printStackTrace();
		}

		response.sendRedirect(request.getContextPath() + "/login.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		String email = request.getParameter("email");
		String password = request.getParameter("password");

		email = email == null ? "" : email.trim().toLowerCase();

		if (email.isEmpty() || password == null || password.isBlank()) {
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

			if ("yes".equalsIgnoreCase(request.getParameter("rememberMe"))) {
				addRememberMeCookie(request, response, user.getUserId());
			}

			response.sendRedirect(request.getContextPath() + "/dashboard");

		} catch (SQLException exception) {
			exception.printStackTrace();
			request.setAttribute("errorMessage",
					"Unable to connect to the database. Check that MySQL is running and update database.properties.");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		}
	}

	private Optional<User> findRememberedUser(HttpServletRequest request) throws SQLException {

		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return Optional.empty();
		}

		for (Cookie cookie : cookies) {

			if (RememberMeUtil.COOKIE_NAME.equals(cookie.getName())) {

				Optional<Integer> userId = RememberMeUtil.getUserId(cookie.getValue());

				if (userId.isPresent()) {
					return userService.findById(userId.get());
				}
			}
		}

		return Optional.empty();
	}

	private void addRememberMeCookie(HttpServletRequest request, HttpServletResponse response, int userId) {

		Cookie cookie = new Cookie(RememberMeUtil.COOKIE_NAME, RememberMeUtil.createToken(userId));

		cookie.setHttpOnly(true);

		cookie.setSecure(request.isSecure());

		cookie.setMaxAge(RememberMeUtil.MAX_AGE_SECONDS);

		cookie.setPath(request.getContextPath().isBlank() ? "/" : request.getContextPath());

		response.addCookie(cookie);
	}
}
