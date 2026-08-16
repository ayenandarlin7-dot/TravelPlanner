package com.travelplanner.controller;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.travelplanner.util.RememberMeUtil;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}

		clearRememberMeCookie(request, response);

		response.sendRedirect(request.getContextPath() + "/login.jsp");
	}

	private void clearRememberMeCookie(HttpServletRequest request, HttpServletResponse response) {

		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			return;
		}

		for (Cookie cookie : cookies) {

			if (RememberMeUtil.COOKIE_NAME.equals(cookie.getName())) {

				cookie.setMaxAge(0);

				cookie.setPath(request.getContextPath().isBlank() ? "/" : request.getContextPath());

				response.addCookie(cookie);
			}
		}
	}
}
