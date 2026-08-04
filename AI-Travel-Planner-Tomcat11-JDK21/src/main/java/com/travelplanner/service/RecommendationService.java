package com.travelplanner.service;

import java.util.Comparator;
import java.util.List;

import com.travelplanner.model.Route;

public class RecommendationService {

	public Route recommend(

			List<Route> routes,

			String preference

	) {

		if (routes == null || routes.isEmpty()) {

			return null;

		}

		switch (preference) {

		case "Cheapest":

			return routes.stream()

					.min(Comparator.comparing(Route::getEstimatedCost))

					.orElse(null);

		case "Fastest":

			return routes.stream()

					.min(Comparator.comparing(Route::getTravelTimeHours))

					.orElse(null);

		case "Shortest":

			return routes.stream()

					.min(Comparator.comparing(Route::getDistanceKm))

					.orElse(null);

		default:

			return routes.get(0);

		}

	}

}