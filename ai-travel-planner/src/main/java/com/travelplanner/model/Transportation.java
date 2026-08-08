package com.travelplanner.model;

public class Transportation {

	private int transportationId;
	private String transportName;

	public Transportation() {
	}

	public Transportation(int transportationId, String transportName) {

		this.transportationId = transportationId;
		this.transportName = transportName;
	}

	public int getTransportationId() {
		return transportationId;
	}

	public void setTransportationId(int transportationId) {
		this.transportationId = transportationId;
	}

	public String getTransportName() {
		return transportName;
	}

	public void setTransportName(String transportName) {
		this.transportName = transportName;
	}
}
