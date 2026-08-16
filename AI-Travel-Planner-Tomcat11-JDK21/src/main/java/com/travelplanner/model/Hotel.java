package com.travelplanner.model;

import java.math.BigDecimal;

public class Hotel {

	private int hotelId;
	private City city;
	private String hotelName;
	private String category;
	private BigDecimal pricePerNight;
	private int roomCapacity;
	private BigDecimal rating;
	private String locationInfo;

	public Hotel() {
	}

	public Hotel(int hotelId, City city, String hotelName, String category, BigDecimal pricePerNight, int roomCapacity,
			BigDecimal rating, String locationInfo) {

		this.hotelId = hotelId;
		this.city = city;
		this.hotelName = hotelName;
		this.category = category;
		this.pricePerNight = pricePerNight;
		this.roomCapacity = roomCapacity;
		this.rating = rating;
		this.locationInfo = locationInfo;
	}

	public int getHotelId() {
		return hotelId;
	}

	public void setHotelId(int hotelId) {
		this.hotelId = hotelId;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public BigDecimal getPricePerNight() {
		return pricePerNight;
	}

	public void setPricePerNight(BigDecimal pricePerNight) {
		this.pricePerNight = pricePerNight;
	}

	public int getRoomCapacity() {
		return roomCapacity;
	}

	public void setRoomCapacity(int roomCapacity) {
		this.roomCapacity = roomCapacity;
	}

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

	public String getLocationInfo() {
		return locationInfo;
	}

	public void setLocationInfo(String locationInfo) {
		this.locationInfo = locationInfo;
	}
}
