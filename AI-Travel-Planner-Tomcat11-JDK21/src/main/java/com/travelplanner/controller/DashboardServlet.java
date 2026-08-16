package com.travelplanner.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.travelplanner.dao.CityDAO;
import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.service.TripService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final CityDAO cityDAO = new CityDAO();

	private final TripService tripService = new TripService();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			HttpSession session = request.getSession(false);

			User loggedInUser = session == null ? null : (User) session.getAttribute("loggedInUser");

			String errorMessage = request.getParameter("error");

			if (errorMessage != null && !errorMessage.isBlank()) {
				request.setAttribute("errorMessage", errorMessage);
			}

			request.setAttribute("cities", cityDAO.findAll());

			if (loggedInUser != null) {

				List<Trip> trips = tripService.getTrips(loggedInUser.getUserId());

				LocalDate today = LocalDate.now();

				List<Trip> upcomingTrips = trips.stream()
						.filter(trip -> trip.getTravelDate() != null && !trip.getTravelDate().isBefore(today))
						.toList();

				request.setAttribute("totalTrips", trips.size());

				request.setAttribute("upcomingTrips", upcomingTrips);

				request.setAttribute("recentTrip", trips.isEmpty() ? null : trips.get(0));
			}

			request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

		} catch (SQLException exception) {

			throw new ServletException("Unable to load dashboard data.", exception);
		}
	}
}
