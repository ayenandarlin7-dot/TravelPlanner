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

@WebServlet("/delete-trip")
public class DeleteTripServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final TripService tripService = new TripService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
			 * The delete only removes the trip when it belongs to the
			 * authenticated user; the DAO applies both conditions in the SQL.
			 * Anything else is treated as not found so a user cannot delete
			 * another user's trip by manipulating the trip id.
			 */
			boolean deleted = tripService.deleteTrip(tripId, loggedInUser.getUserId());

			if (deleted) {
				response.sendRedirect(request.getContextPath() + "/trip-history?deleted=success");
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Trip not found or access denied.");
			}

		} catch (NumberFormatException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid trip ID.");

		} catch (SQLException exception) {
			throw new ServletException("Database error while deleting trip.", exception);
		}
	}
}