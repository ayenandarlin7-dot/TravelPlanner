package com.travelplanner.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.travelplanner.dao.TripDAO;
import com.travelplanner.model.Trip;

public class TripService {

	private final TripDAO tripDAO = new TripDAO();

	public boolean saveTrip(Trip trip) throws SQLException {
		return tripDAO.saveTrip(trip);
	}

	public void saveTripAttractions(int tripId, List<Integer> attractionIds) throws SQLException {

		tripDAO.saveTripAttractions(tripId, attractionIds);
	}

	public List<Trip> getTrips(int userId) throws SQLException {
		return tripDAO.findByUserId(userId);
	}

	public Optional<Trip> findTripByIdAndUserId(int tripId, int userId) throws SQLException {

		return tripDAO.findByIdAndUserId(tripId, userId);
	}

	public boolean deleteTrip(int tripId, int userId) throws SQLException {

		return tripDAO.deleteTrip(tripId, userId);
	}
}