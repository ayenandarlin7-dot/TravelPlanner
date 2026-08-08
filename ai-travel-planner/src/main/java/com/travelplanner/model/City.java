package com.travelplanner.model;

import java.math.BigDecimal;

public class City {

    private int cityId;
    private String cityName;
    private String region;
    private String popularAttraction;
    private String bestSeason;
    private BigDecimal averageHotelCost;
    private BigDecimal averageFoodCost;
    private int recommendedDays;
    private String weatherType;
    private String cityDescription;
    private BigDecimal tourismRating;
    private boolean beach;
    private boolean mountain;
    private boolean historical;
    private boolean familyFriendly;
    private String adventureLevel;
    private String activities;
    private String foodTypes;
    private String relaxationTypes;

    public City() {}

    public City(int cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public int getCityId() { return cityId; }
    public void setCityId(int cityId) { this.cityId = cityId; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getPopularAttraction() { return popularAttraction; }
    public void setPopularAttraction(String popularAttraction) { this.popularAttraction = popularAttraction; }
    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }
    public BigDecimal getAverageHotelCost() { return averageHotelCost; }
    public void setAverageHotelCost(BigDecimal averageHotelCost) { this.averageHotelCost = averageHotelCost; }
    public BigDecimal getAverageFoodCost() { return averageFoodCost; }
    public void setAverageFoodCost(BigDecimal averageFoodCost) { this.averageFoodCost = averageFoodCost; }
    public int getRecommendedDays() { return recommendedDays; }
    public void setRecommendedDays(int recommendedDays) { this.recommendedDays = recommendedDays; }
    public String getWeatherType() { return weatherType; }
    public void setWeatherType(String weatherType) { this.weatherType = weatherType; }
    public String getCityDescription() { return cityDescription; }
    public void setCityDescription(String cityDescription) { this.cityDescription = cityDescription; }
    public BigDecimal getTourismRating() { return tourismRating; }
    public void setTourismRating(BigDecimal tourismRating) { this.tourismRating = tourismRating; }
    public boolean isBeach() { return beach; }
    public void setBeach(boolean beach) { this.beach = beach; }
    public boolean isMountain() { return mountain; }
    public void setMountain(boolean mountain) { this.mountain = mountain; }
    public boolean isHistorical() { return historical; }
    public void setHistorical(boolean historical) { this.historical = historical; }
    public boolean isFamilyFriendly() { return familyFriendly; }
    public void setFamilyFriendly(boolean familyFriendly) { this.familyFriendly = familyFriendly; }
    public String getAdventureLevel() { return adventureLevel; }
    public void setAdventureLevel(String adventureLevel) { this.adventureLevel = adventureLevel; }
    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }
    public String getFoodTypes() { return foodTypes; }
    public void setFoodTypes(String foodTypes) { this.foodTypes = foodTypes; }
    public String getRelaxationTypes() { return relaxationTypes; }
    public void setRelaxationTypes(String relaxationTypes) { this.relaxationTypes = relaxationTypes; }
}
