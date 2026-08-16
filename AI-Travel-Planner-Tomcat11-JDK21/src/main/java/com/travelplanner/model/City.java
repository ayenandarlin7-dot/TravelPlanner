package com.travelplanner.model;

public class City {

	private int cityId;
	private String cityName;
	private Double latitude;
	private Double longitude;

	public City() {
	}

	public City(int cityId, String cityName) {

		this.cityId = cityId;
		this.cityName = cityName;
	}

	public City(int cityId, String cityName, Double latitude, Double longitude) {

		this.cityId = cityId;
		this.cityName = cityName;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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
