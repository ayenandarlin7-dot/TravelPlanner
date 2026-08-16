<<<<<<< HEAD
package com.travelplanner.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.travelplanner.model.User;
import com.travelplanner.service.TripService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/trip-history")
public class HistoryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final TripService tripService = new TripService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		User loggedInUser = session == null ? null : (User) session.getAttribute("loggedInUser");

		if (loggedInUser == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		try {
			request.setAttribute("trips", tripService.getTrips(loggedInUser.getUserId()));

			request.getRequestDispatcher("/tripHistory.jsp").forward(request, response);

		} catch (SQLException exception) {
			throw new ServletException("Unable to load trip history.", exception);
		}
	}
=======
package com.travelplanner.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.travelplanner.model.User;
import com.travelplanner.service.TripService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/trip-history")
public class HistoryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final TripService tripService = new TripService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		User loggedInUser = session == null ? null : (User) session.getAttribute("loggedInUser");

		if (loggedInUser == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		try {
			request.setAttribute("trips", tripService.getTrips(loggedInUser.getUserId()));

			request.getRequestDispatcher("/tripHistory.jsp").forward(request, response);

		} catch (SQLException exception) {
			throw new ServletException("Unable to load trip history.", exception);
		}
	}
>>>>>>> 383055483b6f17e88e95db72c4b5bc0442235184
}