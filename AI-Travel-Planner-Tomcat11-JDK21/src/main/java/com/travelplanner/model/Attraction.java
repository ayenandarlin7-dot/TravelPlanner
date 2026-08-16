package com.travelplanner.model;

import java.math.BigDecimal;

public class Attraction {

	private int attractionId;
	private City city;
	private String attractionName;
	private String description;
	private BigDecimal entranceFee;
	private String imagePath;
	private Double latitude;
	private Double longitude;

	public Attraction() {
	}

	public Attraction(int attractionId, City city, String attractionName, String description, BigDecimal entranceFee,
			String imagePath, Double latitude, Double longitude) {

		this.attractionId = attractionId;
		this.city = city;
		this.attractionName = attractionName;
		this.description = description;
		this.entranceFee = entranceFee;
		this.imagePath = imagePath;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getAttractionId() {
		return attractionId;
	}

	public void setAttractionId(int attractionId) {
		this.attractionId = attractionId;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getAttractionName() {
		return attractionName;
	}

	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getEntranceFee() {
		return entranceFee;
	}

	public void setEntranceFee(BigDecimal entranceFee) {
		this.entranceFee = entranceFee;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
