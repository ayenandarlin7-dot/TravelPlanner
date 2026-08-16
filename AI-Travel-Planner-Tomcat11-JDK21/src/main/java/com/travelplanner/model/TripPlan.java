package com.travelplanner.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A complete trip recommendation: the selected route together with the
 * recommended hotel, food tier and attractions, plus the full cost breakdown.
 */
public class TripPlan {

	private Route route;
	private Hotel hotel;
	private FoodEstimate foodEstimate;
	private List<Attraction> attractions = new ArrayList<>();
	private int tripDays;
	private int hotelNights;
	private int roomsRequired;
	private int numberOfTravellers;
	private BigDecimal transportationCost;
	private BigDecimal hotelCost;
	private BigDecimal foodCost;
	private BigDecimal attractionCost;
	private BigDecimal totalEstimatedCost;
	private BigDecimal remainingBudget;
	private String budgetStatus;
	private double recommendationScore;

	public TripPlan() {
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public FoodEstimate getFoodEstimate() {
		return foodEstimate;
	}

	public void setFoodEstimate(FoodEstimate foodEstimate) {
		this.foodEstimate = foodEstimate;
	}

	public List<Attraction> getAttractions() {
		return attractions;
	}

	public void setAttractions(List<Attraction> attractions) {
		this.attractions = attractions == null ? new ArrayList<>() : attractions;
	}

	public int getTripDays() {
		return tripDays;
	}

	public void setTripDays(int tripDays) {
		this.tripDays = tripDays;
	}

	public int getHotelNights() {
		return hotelNights;
	}

	public void setHotelNights(int hotelNights) {
		this.hotelNights = hotelNights;
	}

	public int getRoomsRequired() {
		return roomsRequired;
	}

	public void setRoomsRequired(int roomsRequired) {
		this.roomsRequired = roomsRequired;
	}

	public int getNumberOfTravellers() {
		return numberOfTravellers;
	}

	public void setNumberOfTravellers(int numberOfTravellers) {
		this.numberOfTravellers = numberOfTravellers;
	}

	public BigDecimal getTransportationCost() {
		return transportationCost;
	}

	public void setTransportationCost(BigDecimal transportationCost) {
		this.transportationCost = transportationCost;
	}

	public BigDecimal getHotelCost() {
		return hotelCost;
	}

	public void setHotelCost(BigDecimal hotelCost) {
		this.hotelCost = hotelCost;
	}

	public BigDecimal getFoodCost() {
		return foodCost;
	}

	public void setFoodCost(BigDecimal foodCost) {
		this.foodCost = foodCost;
	}

	public BigDecimal getAttractionCost() {
		return attractionCost;
	}

	public void setAttractionCost(BigDecimal attractionCost) {
		this.attractionCost = attractionCost;
	}

	public BigDecimal getTotalEstimatedCost() {
		return totalEstimatedCost;
	}

	public void setTotalEstimatedCost(BigDecimal totalEstimatedCost) {
		this.totalEstimatedCost = totalEstimatedCost;
	}

	public BigDecimal getRemainingBudget() {
		return remainingBudget;
	}

	public void setRemainingBudget(BigDecimal remainingBudget) {
		this.remainingBudget = remainingBudget;
	}

	public String getBudgetStatus() {
		return budgetStatus;
	}

	public void setBudgetStatus(String budgetStatus) {
		this.budgetStatus = budgetStatus;
	}

	public double getRecommendationScore() {
		return recommendationScore;
	}

	public void setRecommendationScore(double recommendationScore) {
		this.recommendationScore = recommendationScore;
	}
}
