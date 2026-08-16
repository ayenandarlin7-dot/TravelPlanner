package com.travelplanner.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Trip {

	private int tripId;
	private User user;
	private Route route;
	private City startingCity;
	private City destinationCity;
	private Transportation transportation;
	private List<Attraction> attractions = new ArrayList<>();
	private LocalDate travelDate;
	private LocalDate returnDate;
	private int numberOfTravellers;
	private BigDecimal budget;
	private String preference;
	private BigDecimal recommendedCost;
	private BigDecimal transportationCost;
	private BigDecimal hotelCost;
	private BigDecimal foodCost;
	private BigDecimal attractionCost;
	private BigDecimal totalEstimatedCost;
	private Hotel selectedHotel;
	private String budgetStatus;
	private LocalDateTime createdAt;

	public Trip() {
	}

	public Trip(int tripId, User user, Route route, LocalDate travelDate, LocalDate returnDate, int numberOfTravellers,
			BigDecimal budget, String preference, BigDecimal recommendedCost, LocalDateTime createdAt) {

		this.tripId = tripId;
		this.user = user;
		this.route = route;
		this.travelDate = travelDate;
		this.returnDate = returnDate;
		this.numberOfTravellers = numberOfTravellers;
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

	public List<Attraction> getAttractions() {
		return attractions;
	}

	public void setAttractions(List<Attraction> attractions) {
		this.attractions = attractions == null ? new ArrayList<>() : attractions;
	}

	public LocalDate getTravelDate() {
		return travelDate;
	}

	public void setTravelDate(LocalDate travelDate) {
		this.travelDate = travelDate;
	}

	public LocalDate getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
	}

	public int getNumberOfTravellers() {
		return numberOfTravellers;
	}

	public void setNumberOfTravellers(int numberOfTravellers) {
		this.numberOfTravellers = numberOfTravellers;
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

	public Hotel getSelectedHotel() {
		return selectedHotel;
	}

	public void setSelectedHotel(Hotel selectedHotel) {
		this.selectedHotel = selectedHotel;
	}

	public String getBudgetStatus() {
		return budgetStatus;
	}

	public void setBudgetStatus(String budgetStatus) {
		this.budgetStatus = budgetStatus;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
