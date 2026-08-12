package com.travelplanner.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.Route;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/trip")
public class TripServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final RouteDAO routeDAO = new RouteDAO();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		try {
			int routeId = Integer.parseInt(request.getParameter("routeId"));

			LocalDate travelDate = LocalDate.parse(request.getParameter("travelDate"));

			BigDecimal budget = new BigDecimal(request.getParameter("budget"));

			String preference = request.getParameter("preference");

			Optional<Route> optionalRoute = routeDAO.findById(routeId);

			if (optionalRoute.isEmpty()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Selected route was not found.");
				return;
			}

			Route route = optionalRoute.get();

			request.setAttribute("route", route);
			request.setAttribute("travelDate", travelDate);
			request.setAttribute("budget", budget);
			request.setAttribute("preference", preference);

			request.getRequestDispatcher("/tripSummary.jsp").forward(request, response);

		} catch (NumberFormatException | DateTimeParseException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid trip information.");

		} catch (SQLException exception) {
			throw new ServletException("Database error while loading trip summary.", exception);
		}
	}
}