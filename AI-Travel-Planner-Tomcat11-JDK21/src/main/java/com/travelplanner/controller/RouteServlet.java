package com.travelplanner.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.travelplanner.model.BudgetSuggestion;
import com.travelplanner.model.TripPlan;
import com.travelplanner.service.BudgetSuggestionService;
import com.travelplanner.service.TripPlanService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/route")
public class RouteServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final TripPlanService tripPlanService = new TripPlanService();

	private final BudgetSuggestionService budgetSuggestionService = new BudgetSuggestionService();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		try {

			int startingCityId = Integer.parseInt(request.getParameter("startingCityId"));

			int destinationCityId = Integer.parseInt(request.getParameter("destinationCityId"));

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
				forwardDashboardError(request, response, "Number of travellers must be at least one.");
				return;
			}

			if (numberOfTravellers < 1) {
				forwardDashboardError(request, response, "Number of travellers must be at least one.");
				return;
			}

			if (startingCityId == destinationCityId) {

				forwardDashboardError(request, response, "Starting city and destination cannot be the same.");

				return;
			}

			if (travelDate.isBefore(LocalDate.now())) {

				forwardDashboardError(request, response, "Departure date cannot be in the past.");

				return;
			}

			if (returnDate != null && returnDate.isBefore(travelDate)) {

				forwardDashboardError(request, response, "Return date cannot be earlier than departure date.");

				return;
			}

			BigDecimal budget;

			try {
				budget = new BigDecimal(request.getParameter("budget"));
			} catch (NumberFormatException exception) {
				forwardDashboardError(request, response, "Please enter a valid budget.");
				return;
			}

			if (budget.signum() <= 0) {

				forwardDashboardError(request, response, "Budget must be greater than zero.");

				return;
			}

			String preference = request.getParameter("preference");

			String transportationValue = request.getParameter("transportationId");

			Integer transportationId = null;

			if (transportationValue != null && !transportationValue.isBlank()) {

				try {
					transportationId = Integer.valueOf(transportationValue);
				} catch (NumberFormatException exception) {
					forwardDashboardError(request, response, "Please select a valid transportation option.");
					return;
				}
			}

			/*
			 * Optional overrides posted by the over-budget suggestion forms.
			 */
			String hotelCategory = request.getParameter("hotelCategory");

			String excludeValue = request.getParameter("excludeAttractionIds");

			List<Integer> excludeAttractionIds = null;

			if (excludeValue != null && !excludeValue.isBlank()) {

				excludeAttractionIds = new ArrayList<>();

				for (String part : excludeValue.split(",")) {

					if (!part.isBlank()) {
						excludeAttractionIds.add(Integer.valueOf(part.trim()));
					}
				}
			}

			List<TripPlan> plans = tripPlanService.buildPlans(startingCityId, destinationCityId, transportationId,
					travelDate, returnDate, numberOfTravellers, budget, preference, hotelCategory,
					excludeAttractionIds);

			TripPlan recommendedPlan = plans.isEmpty() ? null : plans.get(0);

			List<BudgetSuggestion> budgetSuggestions = budgetSuggestionService.generateSuggestions(recommendedPlan,
					budget, travelDate, returnDate);

			request.setAttribute("recommendedPlan", recommendedPlan);

			request.setAttribute("plans", plans);

			request.setAttribute("availablePlanCount", plans.size());

			request.setAttribute("budgetSuggestions", budgetSuggestions);

			request.setAttribute("startingCityId", startingCityId);

			request.setAttribute("destinationCityId", destinationCityId);

			request.setAttribute("travelDate", travelDate);

			request.setAttribute("returnDate", returnDate);

			request.setAttribute("numberOfTravellers", numberOfTravellers);

			request.setAttribute("budget", budget);

			request.setAttribute("preference", preference);

			request.getRequestDispatcher("/recommendation.jsp").forward(request, response);

		} catch (NumberFormatException | DateTimeParseException exception) {

			forwardDashboardError(request, response, "Please enter valid trip information.");

		} catch (SQLException exception) {

			throw new ServletException("Database error while searching routes.", exception);
		}
	}

	private void forwardDashboardError(HttpServletRequest request, HttpServletResponse response, String message)
			throws IOException {

		/*
		 * Redirect (not forward) so the error can be displayed on the dashboard
		 * without reusing the POST method against a GET-only servlet.
		 */
		response.sendRedirect(request.getContextPath() + "/dashboard?error="
				+ URLEncoder.encode(message, StandardCharsets.UTF_8));
	}
}
