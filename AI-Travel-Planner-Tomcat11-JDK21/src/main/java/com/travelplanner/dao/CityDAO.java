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
				SELECT city_id, city_name
				FROM cities
				ORDER BY city_name
				""";

		List<City> cities = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql);

				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {

				City city = new City();

				city.setCityId(resultSet.getInt("city_id"));

				city.setCityName(resultSet.getString("city_name"));

				cities.add(city);
			}
		}

		return cities;
	}

	public Optional<City> findById(int cityId) throws SQLException {

		String sql = """
				SELECT city_id, city_name
				FROM cities
				WHERE city_id = ?
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, cityId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					City city = new City();

					city.setCityId(resultSet.getInt("city_id"));

					city.setCityName(resultSet.getString("city_name"));

					return Optional.of(city);
				}
			}
		}

		return Optional.empty();
	}
}
