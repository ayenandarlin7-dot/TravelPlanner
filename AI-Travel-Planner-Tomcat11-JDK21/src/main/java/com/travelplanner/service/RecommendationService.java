package com.travelplanner.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.travelplanner.model.Route;
import com.travelplanner.model.TripPlan;

public class RecommendationService {

	/*
	 * Weights used by the balanced preference. These are shared with the
	 * route-level ranking below so every option is scored consistently.
	 */
	private static final double BALANCED_COST_WEIGHT = 0.34;
	private static final double BALANCED_TIME_WEIGHT = 0.33;
	private static final double BALANCED_DISTANCE_WEIGHT = 0.33;

	/**
	 * Ranks complete trip plans (route + hotel + food + attractions) according
	 * to the user preference.
	 *
	 * <p>
	 * Cheapest compares the TOTAL trip cost (not the transportation price only).
	 * Fastest compares transportation travel time. Shortest compares route
	 * distance. Balanced uses a normalized score across total cost, travel time
	 * and distance.
	 */
	public List<TripPlan> rankPlans(List<TripPlan> plans, String preference) {

		if (plans == null || plans.isEmpty()) {
			return new ArrayList<>();
		}

		String normalizedPreference = preference == null ? "cheapest" : preference.trim().toLowerCase(Locale.ROOT);

		List<TripPlan> rankedPlans = new ArrayList<>(plans);

		switch (normalizedPreference) {

		case "fastest":

			rankedPlans.sort(Comparator.comparingDouble((TripPlan plan) -> plan.getRoute().getTravelTimeHours())
					.thenComparing(TripPlan::getTotalEstimatedCost));

			break;

		case "shortest":

			rankedPlans.sort(Comparator.comparingDouble((TripPlan plan) -> plan.getRoute().getDistanceKm())
					.thenComparing(TripPlan::getTotalEstimatedCost));

			break;

		case "balanced":

			double maximumCost = rankedPlans.stream().map(TripPlan::getTotalEstimatedCost)
					.mapToDouble(BigDecimal::doubleValue).max().orElse(1.0);

			double maximumTime = rankedPlans.stream().mapToDouble(plan -> plan.getRoute().getTravelTimeHours()).max()
					.orElse(1.0);

			double maximumDistance = rankedPlans.stream().mapToDouble(plan -> plan.getRoute().getDistanceKm()).max()
					.orElse(1.0);

			for (TripPlan plan : rankedPlans) {

				double costScore = normalize(plan.getTotalEstimatedCost().doubleValue(), maximumCost);

				double timeScore = normalize(plan.getRoute().getTravelTimeHours(), maximumTime);

				double distanceScore = normalize(plan.getRoute().getDistanceKm(), maximumDistance);

				double finalScore = (costScore * BALANCED_COST_WEIGHT) + (timeScore * BALANCED_TIME_WEIGHT)
						+ (distanceScore * BALANCED_DISTANCE_WEIGHT);

				plan.setRecommendationScore(finalScore);
			}

			rankedPlans.sort(Comparator.comparingDouble(TripPlan::getRecommendationScore)
					.thenComparing(TripPlan::getTotalEstimatedCost));

			break;

		case "cheapest":
		default:

			rankedPlans.sort(Comparator.comparing(TripPlan::getTotalEstimatedCost)
					.thenComparingDouble(plan -> plan.getRoute().getTravelTimeHours()));

			break;
		}

		return rankedPlans;
	}

	public List<Route> rankRoutes(List<Route> routes, BigDecimal budget, String preference) {

		if (routes == null || routes.isEmpty()) {
			return new ArrayList<>();
		}

		if (budget == null || budget.signum() < 0) {
			throw new IllegalArgumentException("Budget must be zero or greater.");
		}

		String normalizedPreference = preference == null ? "cheapest" : preference.trim().toLowerCase(Locale.ROOT);

		/*
		 * Budget ထဲဝင်တဲ့ route တွေကိုပဲရွေးမယ်။
		 */
		List<Route> affordableRoutes = routes.stream()
				.filter(route -> route.getEstimatedCost() != null && route.getEstimatedCost().compareTo(budget) <= 0)
				.toList();

		if (affordableRoutes.isEmpty()) {
			return new ArrayList<>();
		}

		double maximumCost = affordableRoutes.stream().map(Route::getEstimatedCost).mapToDouble(BigDecimal::doubleValue)
				.max().orElse(1.0);

		double maximumTime = affordableRoutes.stream().mapToDouble(Route::getTravelTimeHours).max().orElse(1.0);

		double maximumDistance = affordableRoutes.stream().mapToDouble(Route::getDistanceKm).max().orElse(1.0);

		List<Route> rankedRoutes = new ArrayList<>(affordableRoutes);

		for (Route route : rankedRoutes) {

			double costScore = normalize(route.getEstimatedCost().doubleValue(), maximumCost);

			double timeScore = normalize(route.getTravelTimeHours(), maximumTime);

			double distanceScore = normalize(route.getDistanceKm(), maximumDistance);

			double finalScore;

			switch (normalizedPreference) {

			case "fastest":

				finalScore = (timeScore * 0.60) + (costScore * 0.25) + (distanceScore * 0.15);

				break;

			case "shortest":

				finalScore = (distanceScore * 0.60) + (timeScore * 0.25) + (costScore * 0.15);

				break;

			case "balanced":

				finalScore = (costScore * BALANCED_COST_WEIGHT) + (timeScore * BALANCED_TIME_WEIGHT)
						+ (distanceScore * BALANCED_DISTANCE_WEIGHT);

				break;

			case "cheapest":
			default:

				finalScore = (costScore * 0.60) + (timeScore * 0.25) + (distanceScore * 0.15);

				break;
			}

			route.setRecommendationScore(finalScore);
		}

		/*
		 * Score နည်းဆုံး route က အကောင်းဆုံး။
		 */
		rankedRoutes
				.sort(Comparator.comparingDouble(Route::getRecommendationScore).thenComparing(Route::getEstimatedCost));

		return rankedRoutes;
	}

	private double normalize(double value, double maximum) {

		if (maximum <= 0) {
			return 0;
		}

		return value / maximum;
	}
}