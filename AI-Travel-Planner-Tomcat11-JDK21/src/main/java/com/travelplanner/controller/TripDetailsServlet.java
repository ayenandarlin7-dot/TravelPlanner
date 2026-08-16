package com.travelplanner.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.service.TripService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/trip-details")
public class TripDetailsServlet extends HttpServlet {

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
			int tripId = Integer.parseInt(request.getParameter("tripId"));

			/*
			 * Ownership is enforced on the backend: the lookup only returns a
			 * trip when both the trip id and the authenticated user id match,
			 * so a user can never open another user's trip.
			 */
			Optional<Trip> trip = tripService.findTripByIdAndUserId(tripId, loggedInUser.getUserId());

			if (trip.isEmpty()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Trip not found or access denied.");
				return;
			}

			request.setAttribute("trip", trip.get());

			request.getRequestDispatcher("/tripDetails.jsp").forward(request, response);

		} catch (NumberFormatException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid trip ID.");

		} catch (SQLException exception) {
			throw new ServletException("Unable to load trip details.", exception);
		}
	}
}
