package com.travelplanner.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.Route;
import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.service.TripService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/save-trip")
public class SaveTripServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final RouteDAO routeDAO = new RouteDAO();

	private final TripService tripService = new TripService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);

		User loggedInUser = session == null ? null : (User) session.getAttribute("loggedInUser");

		if (loggedInUser == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		try {
			int routeId = Integer.parseInt(request.getParameter("routeId"));

			LocalDate travelDate = LocalDate.parse(request.getParameter("travelDate"));

			BigDecimal budget = new BigDecimal(request.getParameter("budget"));

			String preference = request.getParameter("preference");

			Optional<Route> optionalRoute = routeDAO.findById(routeId);

			if (optionalRoute.isEmpty()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found.");
				return;
			}

			Route route = optionalRoute.get();

			Trip trip = new Trip();

			trip.setUser(loggedInUser);
			trip.setRoute(route);
			trip.setTravelDate(travelDate);
			trip.setBudget(budget);
			trip.setPreference(preference);
			trip.setRecommendedCost(route.getEstimatedCost());

			boolean saved = tripService.saveTrip(trip);

			if (saved) {
				response.sendRedirect(request.getContextPath() + "/trip-history?saved=success");

			} else {
				throw new ServletException("Trip could not be saved.");
			}

		} catch (IllegalArgumentException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid trip information.");

		} catch (SQLException exception) {
			throw new ServletException("Database error while saving trip.", exception);
		}
	}
}