package com.travelplanner.service;

import com.travelplanner.model.City;
import com.travelplanner.model.Route;
import com.travelplanner.model.TravelRecommendation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecommendationService {

    /**
     * AI-style recommendation engine. It is deterministic and explainable,
     * so it works without an external AI API while still using user data
     * and destination data to produce a personalized score.
     */
    public List<TravelRecommendation> recommend(List<Route> routes, BigDecimal budget,
            String travelStyle, String weather, String food, String relaxation,
            String activity, int tripDays) {

        if (routes == null || routes.isEmpty() || budget == null || budget.signum() < 0) {
            return new ArrayList<>();
        }

        // Keep only the cheapest route for each destination.
        Map<Integer, Route> bestRouteByCity = new HashMap<>();
        for (Route route : routes) {
            if (route.getEstimatedCost() == null) continue;
            
            int cityId = route.getDestinationCity().getCityId();
            Route current = bestRouteByCity.get(cityId);
            
            if (current == null || (route.getEstimatedCost().compareTo(budget) <= 0 && 
                (current.getEstimatedCost().compareTo(budget) > 0 || route.getEstimatedCost().compareTo(current.getEstimatedCost()) < 0))) {
                bestRouteByCity.put(cityId, route);
            } else if (current == null) {
                bestRouteByCity.put(cityId, route);
            }
        }

        List<TravelRecommendation> result = new ArrayList<>();
        for (Route route : bestRouteByCity.values()) {
            City city = route.getDestinationCity();
            double activityScore = match(activity, city.getActivities());
            double styleScore = styleMatch(travelStyle, city);
            double weatherScore = match(weather, city.getWeatherType());
            double foodScore = match(food, city.getFoodTypes());
            double relaxationScore = match(relaxation, city.getRelaxationTypes());
            double budgetScore = budgetScore(route.getEstimatedCost(), budget);
            double durationScore = durationScore(tripDays, city.getRecommendedDays());
            double ratingScore = city.getTourismRating() == null ? 0.5
                    : Math.min(1.0, city.getTourismRating().doubleValue() / 5.0);

            double finalScore =
                    activityScore * 0.20 +
                    styleScore * 0.15 +
                    weatherScore * 0.15 +
                    foodScore * 0.15 +
                    relaxationScore * 0.15 +
                    budgetScore * 0.10 +
                    durationScore * 0.05 +
                    ratingScore * 0.05;

            String reason = buildReason(city, activity, weather, food, relaxation, travelStyle);
            result.add(new TravelRecommendation(city, route, finalScore * 100.0, reason));
        }

        result.sort(Comparator.comparingDouble(TravelRecommendation::getScore).reversed());
        return result;
    }

    /** Legacy route-only ranking retained for existing code compatibility. */
    public List<Route> rankRoutes(List<Route> routes, BigDecimal budget, String preference) {
        if (routes == null || routes.isEmpty() || budget == null || budget.signum() < 0) return new ArrayList<>();
        List<Route> affordable = routes.stream()
                .filter(r -> r.getEstimatedCost() != null && r.getEstimatedCost().compareTo(budget) <= 0)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        if (affordable.isEmpty()) return affordable;
        double maxCost = affordable.stream().mapToDouble(r -> r.getEstimatedCost().doubleValue()).max().orElse(1);
        double maxTime = affordable.stream().mapToDouble(Route::getTravelTimeHours).max().orElse(1);
        double maxDistance = affordable.stream().mapToDouble(Route::getDistanceKm).max().orElse(1);
        String pref = preference == null ? "cheapest" : preference.toLowerCase(Locale.ROOT);
        for (Route r : affordable) {
            double c = r.getEstimatedCost().doubleValue() / maxCost;
            double t = r.getTravelTimeHours() / maxTime;
            double d = r.getDistanceKm() / maxDistance;
            double score = switch (pref) {
                case "fastest" -> t * .60 + c * .25 + d * .15;
                case "shortest" -> d * .60 + t * .25 + c * .15;
                default -> c * .60 + t * .25 + d * .15;
            };
            r.setRecommendationScore(score);
        }
        affordable.sort(Comparator.comparingDouble(Route::getRecommendationScore)
                .thenComparing(Route::getEstimatedCost));
        return affordable;
    }

    private double match(String selected, String values) {
        if (selected == null || selected.isBlank() || "any".equalsIgnoreCase(selected)) return 1.0;
        if (values == null || values.isBlank()) return 0.0;
        String s = selected.trim().toLowerCase(Locale.ROOT);
        String v = values.toLowerCase(Locale.ROOT);
        if (v.contains(s)) return 1.0;
        // Helpful aliases for natural user choices.
        if (s.equals("cool") && v.contains("mild")) return .8;
        if (s.equals("warm") && v.contains("hot")) return .9;
        if (s.equals("local") && v.contains("myanmar")) return 1.0;
        return .0;
    }

    private double styleMatch(String style, City city) {
        if (style == null || style.isBlank() || "any".equalsIgnoreCase(style)) return 1.0;
        String s = style.toLowerCase(Locale.ROOT);
        if (s.equals("adventure")) return "high".equalsIgnoreCase(city.getAdventureLevel()) ? 1.0 : .4;
        if (s.equals("historical") || s.equals("cultural")) return city.isHistorical() ? 1.0 : .3;
        if (s.equals("nature")) return (city.isMountain() || city.isBeach()) ? 1.0 : .3;
        if (s.equals("beach")) return city.isBeach() ? 1.0 : .2;
        if (s.equals("family")) return city.isFamilyFriendly() ? 1.0 : .4;
        return match(style, city.getActivities());
    }

    private double budgetScore(BigDecimal cost, BigDecimal budget) {
        if (budget.signum() == 0) return 0.5;
        double ratio = cost.doubleValue() / budget.doubleValue();
        if (ratio <= .5) return 1.0;
        if (ratio <= .75) return .9;
        if (ratio <= .9) return .8;
        return .65;
    }

    private double durationScore(int tripDays, int recommendedDays) {
        if (tripDays <= 0 || recommendedDays <= 0) return .7;
        int diff = Math.abs(tripDays - recommendedDays);
        return diff == 0 ? 1.0 : diff == 1 ? .85 : diff == 2 ? .7 : .5;
    }

    private String buildReason(City city, String activity, String weather, String food,
            String relaxation, String style) {
        List<String> reasons = new ArrayList<>();
        if (match(activity, city.getActivities()) >= .9) reasons.add("your activity choice");
        if (styleMatch(style, city) >= .9) reasons.add("your travel style");
        if (match(weather, city.getWeatherType()) >= .9) reasons.add("your preferred weather");
        if (match(food, city.getFoodTypes()) >= .9) reasons.add("your food preference");
        if (match(relaxation, city.getRelaxationTypes()) >= .9) reasons.add("your relaxation style");
        if (reasons.isEmpty()) return city.getCityName() + " is a balanced option within your budget.";
        return city.getCityName() + " matches " + String.join(", ", reasons) + ".";
    }
}