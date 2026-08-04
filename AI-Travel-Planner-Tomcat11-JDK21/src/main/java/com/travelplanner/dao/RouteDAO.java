package com.travelplanner.dao;

import com.travelplanner.model.City;
import com.travelplanner.model.Route;
import com.travelplanner.model.Transportation;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouteDAO {

	private static final String BASE_SELECT = """
			SELECT
			    r.route_id,
			    r.distance_km,
			    r.travel_time_hours,
			    r.estimated_cost,

			    sc.city_id AS start_city_id,
			    sc.city_name AS start_city_name,

			    dc.city_id AS destination_city_id,
			    dc.city_name AS destination_city_name,

			    t.transportation_id,
			    t.transport_name

			FROM routes r

			JOIN cities sc
			    ON r.starting_city_id = sc.city_id

			JOIN cities dc
			    ON r.destination_city_id = dc.city_id

			JOIN transportations t
			    ON r.transportation_id =
			       t.transportation_id
			""";

	public List<Route> findRoutes(int startingCityId, int destinationCityId, Integer transportationId)
			throws SQLException {

		StringBuilder sql = new StringBuilder(BASE_SELECT);

		sql.append("""
				WHERE r.starting_city_id = ?
				  AND r.destination_city_id = ?
				""");

		if (transportationId != null) {
			sql.append(" AND r.transportation_id = ? ");
		}

		sql.append(" ORDER BY r.estimated_cost ASC");

		List<Route> routes = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql.toString())) {

			statement.setInt(1, startingCityId);
			statement.setInt(2, destinationCityId);

			if (transportationId != null) {
				statement.setInt(3, transportationId);
			}

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {
					routes.add(mapRoute(resultSet));
				}
			}
		}

		return routes;
	}

	public Optional<Route> findById(int routeId) throws SQLException {

		String sql = BASE_SELECT + " WHERE r.route_id = ?";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, routeId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {
					return Optional.of(mapRoute(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	private Route mapRoute(ResultSet resultSet) throws SQLException {

		Route route = new Route();

		route.setRouteId(resultSet.getInt("route_id"));

		route.setDistanceKm(resultSet.getDouble("distance_km"));

		route.setTravelTimeHours(resultSet.getDouble("travel_time_hours"));

		route.setEstimatedCost(resultSet.getBigDecimal("estimated_cost"));

		City startingCity = new City(resultSet.getInt("start_city_id"), resultSet.getString("start_city_name"));

		City destinationCity = new City(resultSet.getInt("destination_city_id"),
				resultSet.getString("destination_city_name"));

		Transportation transportation = new Transportation(resultSet.getInt("transportation_id"),
				resultSet.getString("transport_name"));

		route.setStartingCity(startingCity);
		route.setDestinationCity(destinationCity);
		route.setTransportation(transportation);

		return route;
	}
}