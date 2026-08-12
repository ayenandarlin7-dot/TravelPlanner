package com.travelplanner.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/dashboard", "/route", "/trip", "/trip-history", "/delete-trip" })
public class AuthenticationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;

		HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession(false);

		boolean loggedIn = session != null && session.getAttribute("loggedInUser") != null;

		if (!loggedIn) {

			response.sendRedirect(request.getContextPath() + "/login.jsp");

			return;
		}

		filterChain.doFilter(request, response);
	}
}
