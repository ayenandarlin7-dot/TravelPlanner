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

			tripService.deleteTrip(tripId, loggedInUser.getUserId());

			response.sendRedirect(request.getContextPath() + "/trip-history?deleted=success");

		} catch (NumberFormatException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid trip ID.");

		} catch (SQLException exception) {
			throw new ServletException("Database error while deleting trip.", exception);
		}
	}
}