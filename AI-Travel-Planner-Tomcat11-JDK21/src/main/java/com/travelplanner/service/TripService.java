package com.travelplanner.service;

import java.sql.SQLException;
import java.util.List;

import com.travelplanner.dao.TripDAO;
import com.travelplanner.model.Trip;

public class TripService {

	private TripDAO dao = new TripDAO();

	public boolean saveTrip(Trip trip) throws SQLException {

		return dao.saveTrip(trip);

	}

	public List<Trip> getTrips(int userId) throws SQLException {

		return dao.findByUserId(userId);

	}

}
