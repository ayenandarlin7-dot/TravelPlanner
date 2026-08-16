<<<<<<< HEAD
package com.travelplanner.service;

import java.sql.SQLException;
import java.util.List;

import com.travelplanner.dao.TripDAO;
import com.travelplanner.model.Trip;

public class TripService {

	private final TripDAO tripDAO = new TripDAO();

	public boolean saveTrip(Trip trip) throws SQLException {
		return tripDAO.saveTrip(trip);
	}

	public List<Trip> getTrips(int userId) throws SQLException {
		return tripDAO.findByUserId(userId);
	}

	public boolean deleteTrip(int tripId, int userId) throws SQLException {

		return tripDAO.deleteTrip(tripId, userId);
	}
=======
package com.travelplanner.service;

import java.sql.SQLException;
import java.util.List;

import com.travelplanner.dao.TripDAO;
import com.travelplanner.model.Trip;

public class TripService {

	private final TripDAO tripDAO = new TripDAO();

	public boolean saveTrip(Trip trip) throws SQLException {
		return tripDAO.saveTrip(trip);
	}

	public List<Trip> getTrips(int userId) throws SQLException {
		return tripDAO.findByUserId(userId);
	}

	public boolean deleteTrip(int tripId, int userId) throws SQLException {

		return tripDAO.deleteTrip(tripId, userId);
	}
>>>>>>> 383055483b6f17e88e95db72c4b5bc0442235184
}