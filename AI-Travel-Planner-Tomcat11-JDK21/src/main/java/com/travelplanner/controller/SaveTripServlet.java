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
import com.travelplanner.model.Trip;
import com.travelplanner.model.TripPlan;
import com.travelplanner.model.User;
import com.travelplanner.service.TripPlanService;
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

	private final TripPlanService tripPlanService = new TripPlanService();

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
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Route not found.");
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
			 * Rebuild the plan from the hidden fields submitted by the summary
			 * page, so the saved trip stores the exact cost breakdown the user
			 * reviewed.
			 */
			TripPlan plan = tripPlanService.buildPlan(route, travelDate, returnDate, numberOfTravellers, budget,
					preference, hotelId, foodTier, attractionIds);

			Trip trip = new Trip();

			trip.setUser(loggedInUser);
			trip.setRoute(route);
			trip.setStartingCity(route.getStartingCity());
			trip.setDestinationCity(route.getDestinationCity());
			trip.setTransportation(route.getTransportation());
			trip.setTravelDate(travelDate);
			trip.setReturnDate(returnDate);
			trip.setNumberOfTravellers(numberOfTravellers);
			trip.setBudget(budget);
			trip.setPreference(preference);
			trip.setRecommendedCost(plan.getTotalEstimatedCost());
			trip.setTransportationCost(plan.getTransportationCost());
			trip.setHotelCost(plan.getHotelCost());
			trip.setFoodCost(plan.getFoodCost());
			trip.setAttractionCost(plan.getAttractionCost());
			trip.setTotalEstimatedCost(plan.getTotalEstimatedCost());
			trip.setSelectedHotel(plan.getHotel());
			trip.setBudgetStatus(plan.getBudgetStatus());
			trip.setAttractions(plan.getAttractions());

			boolean saved = tripService.saveTrip(trip);

			if (saved) {

				tripService.saveTripAttractions(trip.getTripId(), attractionIds);

				response.sendRedirect(request.getContextPath() + "/trip-history?saved=success");

			} else {
				throw new ServletException("Trip could not be saved.");
			}

		} catch (NumberFormatException | DateTimeParseException exception) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid trip information.");

		} catch (SQLException exception) {
			throw new ServletException("Database error while saving trip.", exception);
		}
	}
}
