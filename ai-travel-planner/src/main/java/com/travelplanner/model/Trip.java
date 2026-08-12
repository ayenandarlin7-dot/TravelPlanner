package com.travelplanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Trip {

	private int tripId;
	private User user;
	private Route route;
	private LocalDate travelDate;
	private BigDecimal budget;
	private String preference;
	private BigDecimal recommendedCost;
	private LocalDateTime createdAt;

	public Trip() {
	}

	public Trip(int tripId, User user, Route route, LocalDate travelDate, BigDecimal budget, String preference,
			BigDecimal recommendedCost, LocalDateTime createdAt) {

		this.tripId = tripId;
		this.user = user;
		this.route = route;
		this.travelDate = travelDate;
		this.budget = budget;
		this.preference = preference;
		this.recommendedCost = recommendedCost;
		this.createdAt = createdAt;
	}

	public int getTripId() {
		return tripId;
	}

	public void setTripId(int tripId) {
		this.tripId = tripId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public LocalDate getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(LocalDate travelDate) {
		this.travelDate = travelDate;
	}

	public BigDecimal getBudget() {
		return budget;
	}

	public void setBudget(BigDecimal budget) {
		this.budget = budget;
	}

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

	public BigDecimal getRecommendedCost() {
		return recommendedCost;
	}

	public void setRecommendedCost(BigDecimal recommendedCost) {
		this.recommendedCost = recommendedCost;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
