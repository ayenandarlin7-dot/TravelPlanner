<<<<<<< HEAD
package com.travelplanner.dao;

import com.travelplanner.model.City;
import com.travelplanner.model.Route;
import com.travelplanner.model.Transportation;
import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {

	public boolean saveTrip(Trip trip) throws SQLException {

		String sql = """
				INSERT INTO trips
				(
				    user_id,
				    route_id,
				    travel_date,
				    budget,
				    preference,
				    recommended_cost
				)
				VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, trip.getUser().getUserId());

			statement.setInt(2, trip.getRoute().getRouteId());

			statement.setDate(3, Date.valueOf(trip.getTravelDate()));

			statement.setBigDecimal(4, trip.getBudget());

			statement.setString(5, trip.getPreference());

			statement.setBigDecimal(6, trip.getRecommendedCost());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				return false;
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

				if (generatedKeys.next()) {
					trip.setTripId(generatedKeys.getInt(1));
				}
			}

			return true;
		}
	}

	public List<Trip> findByUserId(int userId) throws SQLException {

		String sql = """
				SELECT
				    tr.trip_id,
				    tr.travel_date,
				    tr.budget,
				    tr.preference,
				    tr.recommended_cost,
				    tr.created_at,

				    r.route_id,
				    r.distance_km,
				    r.travel_time_hours,
				    r.estimated_cost,

				    sc.city_id AS start_city_id,
				    sc.city_name AS start_city_name,

				    dc.city_id AS destination_city_id,
				    dc.city_name AS destination_city_name,

				    tp.transportation_id,
				    tp.transport_name

				FROM trips tr

				JOIN routes r
				    ON tr.route_id = r.route_id

				JOIN cities sc
				    ON r.starting_city_id = sc.city_id

				JOIN cities dc
				    ON r.destination_city_id = dc.city_id

				JOIN transportations tp
				    ON r.transportation_id =
				       tp.transportation_id

				WHERE tr.user_id = ?

				ORDER BY tr.created_at DESC
				""";

		List<Trip> trips = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					Trip trip = new Trip();

					trip.setTripId(resultSet.getInt("trip_id"));

					Date travelDate = resultSet.getDate("travel_date");

					if (travelDate != null) {
						trip.setTravelDate(travelDate.toLocalDate());
					}

					trip.setBudget(resultSet.getBigDecimal("budget"));

					trip.setPreference(resultSet.getString("preference"));

					trip.setRecommendedCost(resultSet.getBigDecimal("recommended_cost"));

					Timestamp createdAt = resultSet.getTimestamp("created_at");

					if (createdAt != null) {
						trip.setCreatedAt(createdAt.toLocalDateTime());
					}

					Route route = new Route();

					route.setRouteId(resultSet.getInt("route_id"));

					route.setDistanceKm(resultSet.getDouble("distance_km"));

					route.setTravelTimeHours(resultSet.getDouble("travel_time_hours"));

					route.setEstimatedCost(resultSet.getBigDecimal("estimated_cost"));

					route.setStartingCity(
							new City(resultSet.getInt("start_city_id"), resultSet.getString("start_city_name")));

					route.setDestinationCity(new City(resultSet.getInt("destination_city_id"),
							resultSet.getString("destination_city_name")));

					route.setTransportation(new Transportation(resultSet.getInt("transportation_id"),
							resultSet.getString("transport_name")));

					trip.setRoute(route);

					User user = new User();
					user.setUserId(userId);
					trip.setUser(user);

					trips.add(trip);
				}
			}
		}

		return trips;
	}

	public boolean deleteTrip(int tripId, int userId) throws SQLException {

		String sql = """
				DELETE FROM trips
				WHERE trip_id = ?
				  AND user_id = ?
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, tripId);
			statement.setInt(2, userId);

			return statement.executeUpdate() > 0;
		}
	}
=======
package com.travelplanner.dao;

import com.travelplanner.model.City;
import com.travelplanner.model.Route;
import com.travelplanner.model.Transportation;
import com.travelplanner.model.Trip;
import com.travelplanner.model.User;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {

	public boolean saveTrip(Trip trip) throws SQLException {

		String sql = """
				INSERT INTO trips
				(
				    user_id,
				    route_id,
				    travel_date,
				    budget,
				    preference,
				    recommended_cost
				)
				VALUES (?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, trip.getUser().getUserId());

			statement.setInt(2, trip.getRoute().getRouteId());

			statement.setDate(3, Date.valueOf(trip.getTravelDate()));

			statement.setBigDecimal(4, trip.getBudget());

			statement.setString(5, trip.getPreference());

			statement.setBigDecimal(6, trip.getRecommendedCost());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				return false;
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

				if (generatedKeys.next()) {
					trip.setTripId(generatedKeys.getInt(1));
				}
			}

			return true;
		}
	}

	public List<Trip> findByUserId(int userId) throws SQLException {

		String sql = """
				SELECT
				    tr.trip_id,
				    tr.travel_date,
				    tr.budget,
				    tr.preference,
				    tr.recommended_cost,
				    tr.created_at,

				    r.route_id,
				    r.distance_km,
				    r.travel_time_hours,
				    r.estimated_cost,

				    sc.city_id AS start_city_id,
				    sc.city_name AS start_city_name,

				    dc.city_id AS destination_city_id,
				    dc.city_name AS destination_city_name,

				    tp.transportation_id,
				    tp.transport_name

				FROM trips tr

				JOIN routes r
				    ON tr.route_id = r.route_id

				JOIN cities sc
				    ON r.starting_city_id = sc.city_id

				JOIN cities dc
				    ON r.destination_city_id = dc.city_id

				JOIN transportations tp
				    ON r.transportation_id =
				       tp.transportation_id

				WHERE tr.user_id = ?

				ORDER BY tr.created_at DESC
				""";

		List<Trip> trips = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					Trip trip = new Trip();

					trip.setTripId(resultSet.getInt("trip_id"));

					Date travelDate = resultSet.getDate("travel_date");

					if (travelDate != null) {
						trip.setTravelDate(travelDate.toLocalDate());
					}

					trip.setBudget(resultSet.getBigDecimal("budget"));

					trip.setPreference(resultSet.getString("preference"));

					trip.setRecommendedCost(resultSet.getBigDecimal("recommended_cost"));

					Timestamp createdAt = resultSet.getTimestamp("created_at");

					if (createdAt != null) {
						trip.setCreatedAt(createdAt.toLocalDateTime());
					}

					Route route = new Route();

					route.setRouteId(resultSet.getInt("route_id"));

					route.setDistanceKm(resultSet.getDouble("distance_km"));

					route.setTravelTimeHours(resultSet.getDouble("travel_time_hours"));

					route.setEstimatedCost(resultSet.getBigDecimal("estimated_cost"));

					route.setStartingCity(
							new City(resultSet.getInt("start_city_id"), resultSet.getString("start_city_name")));

					route.setDestinationCity(new City(resultSet.getInt("destination_city_id"),
							resultSet.getString("destination_city_name")));

					route.setTransportation(new Transportation(resultSet.getInt("transportation_id"),
							resultSet.getString("transport_name")));

					trip.setRoute(route);

					User user = new User();
					user.setUserId(userId);
					trip.setUser(user);

					trips.add(trip);
				}
			}
		}

		return trips;
	}

	public boolean deleteTrip(int tripId, int userId) throws SQLException {

		String sql = """
				DELETE FROM trips
				WHERE trip_id = ?
				  AND user_id = ?
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, tripId);
			statement.setInt(2, userId);

			return statement.executeUpdate() > 0;
		}
	}
>>>>>>> 383055483b6f17e88e95db72c4b5bc0442235184
}