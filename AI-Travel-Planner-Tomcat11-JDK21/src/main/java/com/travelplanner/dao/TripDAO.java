package com.travelplanner.dao;

import com.travelplanner.model.City;
import com.travelplanner.model.Hotel;
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

	private final AttractionDAO attractionDAO = new AttractionDAO();

	public boolean saveTrip(Trip trip) throws SQLException {

		String sql = """
				INSERT INTO trips
				(
				    user_id,
				    route_id,
				    starting_city_id,
				    destination_city_id,
				    transportation_id,
				    travel_date,
				    return_date,
				    number_of_travellers,
				    budget,
				    preference,
				    recommended_cost,
				    transportation_cost,
				    hotel_cost,
				    food_cost,
				    attraction_cost,
				    total_estimated_cost,
				    selected_hotel_id,
				    budget_status
				)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, trip.getUser().getUserId());

			statement.setInt(2, trip.getRoute().getRouteId());

			Integer startingCityId = resolveStartingCityId(trip);

			if (startingCityId != null) {
				statement.setInt(3, startingCityId);
			} else {
				statement.setNull(3, java.sql.Types.INTEGER);
			}

			Integer destinationCityId = resolveDestinationCityId(trip);

			if (destinationCityId != null) {
				statement.setInt(4, destinationCityId);
			} else {
				statement.setNull(4, java.sql.Types.INTEGER);
			}

			Integer transportationId = resolveTransportationId(trip);

			if (transportationId != null) {
				statement.setInt(5, transportationId);
			} else {
				statement.setNull(5, java.sql.Types.INTEGER);
			}

			statement.setDate(6, Date.valueOf(trip.getTravelDate()));

			if (trip.getReturnDate() != null) {
				statement.setDate(7, Date.valueOf(trip.getReturnDate()));
			} else {
				statement.setNull(7, java.sql.Types.DATE);
			}

			statement.setInt(8, trip.getNumberOfTravellers());

			statement.setBigDecimal(9, trip.getBudget());

			statement.setString(10, trip.getPreference());

			statement.setBigDecimal(11, trip.getRecommendedCost());

			statement.setBigDecimal(12, trip.getTransportationCost());

			statement.setBigDecimal(13, trip.getHotelCost());

			statement.setBigDecimal(14, trip.getFoodCost());

			statement.setBigDecimal(15, trip.getAttractionCost());

			statement.setBigDecimal(16, trip.getTotalEstimatedCost());

			if (trip.getSelectedHotel() != null) {
				statement.setInt(17, trip.getSelectedHotel().getHotelId());
			} else {
				statement.setNull(17, java.sql.Types.INTEGER);
			}

			statement.setString(18, trip.getBudgetStatus());

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

	private Integer resolveStartingCityId(Trip trip) {

		if (trip.getStartingCity() != null) {
			return trip.getStartingCity().getCityId();
		}

		if (trip.getRoute() != null && trip.getRoute().getStartingCity() != null) {
			return trip.getRoute().getStartingCity().getCityId();
		}

		return null;
	}

	private Integer resolveDestinationCityId(Trip trip) {

		if (trip.getDestinationCity() != null) {
			return trip.getDestinationCity().getCityId();
		}

		if (trip.getRoute() != null && trip.getRoute().getDestinationCity() != null) {
			return trip.getRoute().getDestinationCity().getCityId();
		}

		return null;
	}

	private Integer resolveTransportationId(Trip trip) {

		if (trip.getTransportation() != null) {
			return trip.getTransportation().getTransportationId();
		}

		if (trip.getRoute() != null && trip.getRoute().getTransportation() != null) {
			return trip.getRoute().getTransportation().getTransportationId();
		}

		return null;
	}

	private static final String SELECT_TRIPS_SQL = """
			SELECT
			    tr.trip_id,
			    tr.user_id,
			    tr.travel_date,
			    tr.return_date,
			    tr.number_of_travellers,
			    tr.budget,
			    tr.preference,
			    tr.recommended_cost,
			    tr.transportation_cost,
			    tr.hotel_cost,
			    tr.food_cost,
			    tr.attraction_cost,
			    tr.total_estimated_cost,
			    tr.budget_status,
			    tr.created_at,

			    r.route_id,
			    r.distance_km,
			    r.travel_time_hours,
			    r.estimated_cost,
			    r.route_info,

			    sc.city_id AS start_city_id,
			    sc.city_name AS start_city_name,
			    sc.latitude AS start_city_latitude,
			    sc.longitude AS start_city_longitude,

			    dc.city_id AS destination_city_id,
			    dc.city_name AS destination_city_name,
			    dc.latitude AS destination_city_latitude,
			    dc.longitude AS destination_city_longitude,

			    tp.transportation_id,
			    tp.transport_name,

			    h.hotel_id,
			    h.hotel_name,
			    h.category,
			    h.price_per_night,
			    h.room_capacity,
			    h.rating,
			    h.location_info

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

			LEFT JOIN hotels h
			    ON tr.selected_hotel_id = h.hotel_id

			""";

	public List<Trip> findByUserId(int userId) throws SQLException {

		return queryTrips("tr.user_id = ?", userId);
	}

	public java.util.Optional<Trip> findByIdAndUserId(int tripId, int userId) throws SQLException {

		List<Trip> trips = queryTrips("tr.trip_id = ? AND tr.user_id = ?", tripId, userId);

		return trips.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(trips.get(0));
	}

	private List<Trip> queryTrips(String whereClause, Object... params) throws SQLException {

		String sql = SELECT_TRIPS_SQL + " WHERE " + whereClause + " ORDER BY tr.created_at DESC";

		List<Trip> trips = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			for (int index = 0; index < params.length; index++) {

				statement.setObject(index + 1, params[index]);
			}

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					Trip trip = new Trip();

					trip.setTripId(resultSet.getInt("trip_id"));

					Date travelDate = resultSet.getDate("travel_date");

					if (travelDate != null) {
						trip.setTravelDate(travelDate.toLocalDate());
					}

					Date returnDate = resultSet.getDate("return_date");

					if (returnDate != null) {
						trip.setReturnDate(returnDate.toLocalDate());
					}

					trip.setNumberOfTravellers(resultSet.getInt("number_of_travellers"));

					trip.setBudget(resultSet.getBigDecimal("budget"));

					trip.setPreference(resultSet.getString("preference"));

					trip.setRecommendedCost(resultSet.getBigDecimal("recommended_cost"));

					trip.setTransportationCost(resultSet.getBigDecimal("transportation_cost"));

					trip.setHotelCost(resultSet.getBigDecimal("hotel_cost"));

					trip.setFoodCost(resultSet.getBigDecimal("food_cost"));

					trip.setAttractionCost(resultSet.getBigDecimal("attraction_cost"));

					trip.setTotalEstimatedCost(resultSet.getBigDecimal("total_estimated_cost"));

					trip.setBudgetStatus(resultSet.getString("budget_status"));

					Timestamp createdAt = resultSet.getTimestamp("created_at");

					if (createdAt != null) {
						trip.setCreatedAt(createdAt.toLocalDateTime());
					}

					Route route = new Route();

					route.setRouteId(resultSet.getInt("route_id"));

					route.setDistanceKm(resultSet.getDouble("distance_km"));

					route.setTravelTimeHours(resultSet.getDouble("travel_time_hours"));

					route.setEstimatedCost(resultSet.getBigDecimal("estimated_cost"));

					route.setRouteInfo(resultSet.getString("route_info"));

					City startingCity = new City(resultSet.getInt("start_city_id"),
							resultSet.getString("start_city_name"));

					startingCity.setLatitude(readNullableDouble(resultSet, "start_city_latitude"));

					startingCity.setLongitude(readNullableDouble(resultSet, "start_city_longitude"));

					route.setStartingCity(startingCity);

					City destinationCity = new City(resultSet.getInt("destination_city_id"),
							resultSet.getString("destination_city_name"));

					destinationCity.setLatitude(readNullableDouble(resultSet, "destination_city_latitude"));

					destinationCity.setLongitude(readNullableDouble(resultSet, "destination_city_longitude"));

					route.setDestinationCity(destinationCity);

					route.setTransportation(new Transportation(resultSet.getInt("transportation_id"),
							resultSet.getString("transport_name")));

					trip.setRoute(route);

					trip.setStartingCity(route.getStartingCity());

					trip.setDestinationCity(route.getDestinationCity());

					trip.setTransportation(route.getTransportation());

					int hotelId = resultSet.getInt("hotel_id");

					if (!resultSet.wasNull()) {

						Hotel hotel = new Hotel();

						hotel.setHotelId(hotelId);

						hotel.setCity(route.getDestinationCity());

						hotel.setHotelName(resultSet.getString("hotel_name"));

						hotel.setCategory(resultSet.getString("category"));

						hotel.setPricePerNight(resultSet.getBigDecimal("price_per_night"));

						hotel.setRoomCapacity(resultSet.getInt("room_capacity"));

						hotel.setRating(resultSet.getBigDecimal("rating"));

						hotel.setLocationInfo(resultSet.getString("location_info"));

						trip.setSelectedHotel(hotel);
					}

					/*
					 * Restore the attractions attached to this trip so the history
					 * page can list them.
					 */
					List<Integer> attractionIds = findAttractionIdsByTripId(trip.getTripId());

					if (!attractionIds.isEmpty()) {
						trip.setAttractions(attractionDAO.findByIds(attractionIds));
					}

					User user = new User();
					user.setUserId(resultSet.getInt("user_id"));
					trip.setUser(user);

					trips.add(trip);
				}
			}
		}

		return trips;
	}

	private Double readNullableDouble(ResultSet resultSet, String column) throws SQLException {

		double value = resultSet.getDouble(column);

		return resultSet.wasNull() ? null : value;
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

	public void saveTripAttractions(int tripId, List<Integer> attractionIds) throws SQLException {

		if (attractionIds == null || attractionIds.isEmpty()) {
			return;
		}

		String sql = """
				INSERT INTO trip_attractions (trip_id, attraction_id)
				VALUES (?, ?)
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			for (Integer attractionId : attractionIds) {

				statement.setInt(1, tripId);

				statement.setInt(2, attractionId);

				statement.addBatch();
			}

			statement.executeBatch();
		}
	}

	public List<Integer> findAttractionIdsByTripId(int tripId) throws SQLException {

		String sql = """
				SELECT attraction_id
				FROM trip_attractions
				WHERE trip_id = ?
				ORDER BY attraction_id
				""";

		List<Integer> attractionIds = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, tripId);

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					attractionIds.add(resultSet.getInt("attraction_id"));
				}
			}
		}

		return attractionIds;
	}
}
