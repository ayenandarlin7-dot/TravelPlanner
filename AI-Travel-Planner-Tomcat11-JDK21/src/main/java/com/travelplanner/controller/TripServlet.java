package com.travelplanner.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.Route;
import com.travelplanner.model.TripPlan;
import com.travelplanner.service.TripPlanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/trip")
public class TripServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final RouteDAO routeDAO = new RouteDAO();

	private final TripPlanService tripPlanService = new TripPlanService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		try {
			int routeId = Integer.parseInt(request.getParameter("routeId"));

			LocalDate travelDate = LocalDate.parse(request.getParameter("travelDate"));

			String returnDateValue = request.getParameter("returnDate");

			LocalDate returnDate = null;

			if (returnDateValue != null && !returnDateValue.isBlank()) {
				returnDate = LocalDate.parse(returnDateValue);
			}

			int numberOfTravellers;

			try {
				numberOfTravellers = Integer.parseInt(request.getParameter("travellers"));
			} catch (NumberFormatException exception) {
				numberOfTravellers = 1;
			}

			BigDecimal budget = new BigDecimal(request.getParameter("budget"));

			String preference = request.getParameter("preference");

			Optional<Route> optionalRoute = routeDAO.findById(routeId);

			if (optionalRoute.isEmpty()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Selected route was not found.");
				return;
			}

			Route route = optionalRoute.get();

			String hotelIdValue = request.getParameter("hotelId");

			Integer hotelId = null;

			if (hotelIdValue != null && !hotelIdValue.isBlank()) {

				hotelId = Integer.valueOf(hotelIdValue);
			}

			String foodTier = request.getParameter("foodTier");

			String[] attractionIdValues = request.getParameterValues("attractionIds");

			List<Integer> attractionIds = new ArrayList<>();

			if (attractionIdValues != null) {

				for (String attractionIdValue : attractionIdValues) {

					if (attractionIdValue != null && !attractionIdValue.isBlank()) {
						attractionIds.add(Integer.valueOf(attractionIdValue));
					}
				}
			}

			/*
			 * Rebuild the plan with the exact selections made on the
			 * recommendation page so the summary always matches.
			 */
			TripPlan plan = tripPlanService.buildPlan(route, travelDate, returnDate, numberOfTravellers, budget,
					preference, hotelId, foodTier, attractionIds);

			request.setAttribute("plan", plan);
			request.setAttribute("travelDate", travelDate);
			request.setAttribute("returnDate", returnDate);
			request.setAttribute("numberOfTravellers", numberOfTravellers);
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
