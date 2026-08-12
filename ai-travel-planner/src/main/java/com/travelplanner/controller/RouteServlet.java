package com.travelplanner.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.Route;
import com.travelplanner.model.TravelRecommendation;
import com.travelplanner.service.RecommendationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/route")
public class RouteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RouteDAO routeDAO = new RouteDAO();
    private final RecommendationService recommendationService = new RecommendationService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        try {
            int startingCityId = Integer.parseInt(request.getParameter("startingCityId"));
            int destinationCityId = Integer.parseInt(request.getParameter("destinationCityId"));
            LocalDate travelDate = LocalDate.parse(request.getParameter("travelDate"));
            BigDecimal budget = new BigDecimal(request.getParameter("budget"));

            Integer transportationId = null;
            String transportationValue = request.getParameter("transportationId");
            if (transportationValue != null && !transportationValue.isBlank()) {
                transportationId = Integer.valueOf(transportationValue);
            }

            String travelStyle = value(request.getParameter("travelStyle"), "any");
            String activity = value(request.getParameter("activity"), "any");
            String weather = value(request.getParameter("weather"), "any");
            String food = value(request.getParameter("food"), "any");
            String relaxation = value(request.getParameter("relaxation"), "any");
            int tripDays = parseDays(request.getParameter("tripDays"));

            if (startingCityId == destinationCityId) {
                forwardDashboardError(request, response, "Starting city and destination must be different.");
                return;
            }
            if (travelDate.isBefore(LocalDate.now())) {
                forwardDashboardError(request, response, "Travel date cannot be in the past.");
                return;
            }
            if (budget.signum() < 0) {
                forwardDashboardError(request, response, "Budget cannot be negative.");
                return;
            }

            List<Route> availableRoutes = routeDAO.findRoutesFromCity(startingCityId, null);
            
            List<TravelRecommendation> recommendations = recommendationService.recommend(
                    availableRoutes, budget, travelStyle, weather, food, relaxation, activity, tripDays);

            TravelRecommendation selected = recommendations.stream()
                    .filter(r -> r.getCity().getCityId() == destinationCityId)
                    .findFirst().orElse(null);

            Route recommendedRoute = null;

            if (selected != null) {
                if (transportationId != null) {
                    boolean hasChosenTransport = true;
                    if (selected.getRoute().getTransportation() != null) {
                        hasChosenTransport = selected.getRoute().getTransportation().getTransportationId() == transportationId;
                    }
                    
                    if (!hasChosenTransport) {
                        request.setAttribute("transportWarning", "Selected transportation is not available for this destination. Showing available route details.");
                    }
                }

                recommendations.remove(selected);
                recommendations.add(0, selected);
                recommendedRoute = selected.getRoute();
            } else if (!recommendations.isEmpty()) {
                recommendedRoute = recommendations.get(0).getRoute();
            }

            List<Route> rankedRoutes = new ArrayList<>();
            for (TravelRecommendation recommendation : recommendations) {
                rankedRoutes.add(recommendation.getRoute());
            }

            request.setAttribute("recommendedRoute", recommendedRoute);
            request.setAttribute("rankedRoutes", rankedRoutes);
            request.setAttribute("recommendations", recommendations);
            request.setAttribute("selectedDestinationId", destinationCityId);
            request.setAttribute("availableRouteCount", availableRoutes.size());
            request.setAttribute("travelDate", travelDate);
            request.setAttribute("budget", budget);
            request.setAttribute("preference", travelStyle);
            request.setAttribute("travelStyle", travelStyle);
            request.setAttribute("activity", activity);
            request.setAttribute("weather", weather);
            request.setAttribute("food", food);
            request.setAttribute("relaxation", relaxation);
            request.setAttribute("tripDays", tripDays);

            request.getRequestDispatcher("/recommendation.jsp").forward(request, response);

        } catch (NumberFormatException | DateTimeParseException exception) {
            forwardDashboardError(request, response, "Please enter valid trip information.");
        } catch (SQLException exception) {
            throw new ServletException("Database error while generating recommendations.", exception);
        }
    }

    private String value(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private int parseDays(String value) {
        if (value == null || value.isBlank()) return 3;
        return switch (value) {
            case "1-2" -> 2;
            case "3-5" -> 4;
            case "6-7" -> 6;
            case "8+" -> 8;
            default -> Integer.parseInt(value);
        };
    }

    private void forwardDashboardError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", message);
        request.getRequestDispatcher("/dashboard").forward(request, response);
    }
}