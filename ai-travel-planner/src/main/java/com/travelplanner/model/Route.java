package com.travelplanner.model;

import java.math.BigDecimal;

public class Route {

	private int routeId;
	private City startingCity;
	private City destinationCity;
	private Transportation transportation;
	private double distanceKm;
	private double travelTimeHours;
	private BigDecimal estimatedCost;
	private double recommendationScore;

	public Route() {
	}

	public Route(int routeId, City startingCity, City destinationCity, Transportation transportation, double distanceKm,
			double travelTimeHours, BigDecimal estimatedCost) {

		this.routeId = routeId;
		this.startingCity = startingCity;
		this.destinationCity = destinationCity;
		this.transportation = transportation;
		this.distanceKm = distanceKm;
		this.travelTimeHours = travelTimeHours;
		this.estimatedCost = estimatedCost;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}

	public City getStartingCity() {
		return startingCity;
	}

	public void setStartingCity(City startingCity) {
		this.startingCity = startingCity;
	}

	public City getDestinationCity() {
		return destinationCity;
	}

	public void setDestinationCity(City destinationCity) {
		this.destinationCity = destinationCity;
	}

	public Transportation getTransportation() {
		return transportation;
	}

	public void setTransportation(Transportation transportation) {
		this.transportation = transportation;
	}

	public double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}

	public double getTravelTimeHours() {
		return travelTimeHours;
	}

	public void setTravelTimeHours(double travelTimeHours) {
		this.travelTimeHours = travelTimeHours;
	}

	public BigDecimal getEstimatedCost() {
		return estimatedCost;
	}

	public void setEstimatedCost(BigDecimal estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

	public double getRecommendationScore() {
		return recommendationScore;
	}

	public void setRecommendationScore(double recommendationScore) {
		this.recommendationScore = recommendationScore;
	}
}
