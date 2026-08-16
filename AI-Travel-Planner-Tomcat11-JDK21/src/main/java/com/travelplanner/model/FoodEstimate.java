package com.travelplanner.model;

import java.math.BigDecimal;

public class FoodEstimate {

	private int foodEstimateId;
	private City city;
	private String tier;
	private BigDecimal dailyCostPerPerson;

	public FoodEstimate() {
	}

	public FoodEstimate(int foodEstimateId, City city, String tier, BigDecimal dailyCostPerPerson) {

		this.foodEstimateId = foodEstimateId;
		this.city = city;
		this.tier = tier;
		this.dailyCostPerPerson = dailyCostPerPerson;
	}

	public int getFoodEstimateId() {
		return foodEstimateId;
	}

	public void setFoodEstimateId(int foodEstimateId) {
		this.foodEstimateId = foodEstimateId;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public BigDecimal getDailyCostPerPerson() {
		return dailyCostPerPerson;
	}

	public void setDailyCostPerPerson(BigDecimal dailyCostPerPerson) {
		this.dailyCostPerPerson = dailyCostPerPerson;
	}
}
