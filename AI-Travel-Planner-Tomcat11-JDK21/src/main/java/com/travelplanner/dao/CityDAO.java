package com.travelplanner.dao;

import com.travelplanner.model.City;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CityDAO {

	public List<City> findAll() throws SQLException {

		String sql = """
				SELECT city_id, city_name, latitude, longitude
				FROM cities
				ORDER BY city_name
				""";

		List<City> cities = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql);

				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {

				cities.add(mapCity(resultSet));
			}
		}

		return cities;
	}

	public Optional<City> findById(int cityId) throws SQLException {

		String sql = """
				SELECT city_id, city_name, latitude, longitude
				FROM cities
				WHERE city_id = ?
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, cityId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					return Optional.of(mapCity(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	private City mapCity(ResultSet resultSet) throws SQLException {

		City city = new City();

		city.setCityId(resultSet.getInt("city_id"));

		city.setCityName(resultSet.getString("city_name"));

		double latitude = resultSet.getDouble("latitude");

		if (!resultSet.wasNull()) {
			city.setLatitude(latitude);
		}

		double longitude = resultSet.getDouble("longitude");

		if (!resultSet.wasNull()) {
			city.setLongitude(longitude);
		}

		return city;
	}
}
