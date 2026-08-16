package com.travelplanner.dao;

import com.travelplanner.model.Attraction;
import com.travelplanner.model.City;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttractionDAO {

	private static final String BASE_SELECT = """
			SELECT
			    a.attraction_id,
			    a.attraction_name,
			    a.description,
			    a.entrance_fee,
			    a.image_path,
			    a.latitude,
			    a.longitude,

			    c.city_id,
			    c.city_name

			FROM attractions a

			JOIN cities c
			    ON a.city_id = c.city_id
			""";

	public List<Attraction> findAll() throws SQLException {

		String sql = BASE_SELECT + " ORDER BY c.city_name, a.attraction_name";

		List<Attraction> attractions = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql);

				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {

				attractions.add(mapAttraction(resultSet));
			}
		}

		return attractions;
	}

	public List<Attraction> findByCityId(int cityId) throws SQLException {

		String sql = BASE_SELECT + " WHERE a.city_id = ? ORDER BY a.attraction_name";

		List<Attraction> attractions = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, cityId);

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					attractions.add(mapAttraction(resultSet));
				}
			}
		}

		return attractions;
	}

	public Optional<Attraction> findById(int attractionId) throws SQLException {

		String sql = BASE_SELECT + " WHERE a.attraction_id = ?";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, attractionId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					return Optional.of(mapAttraction(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	public List<Attraction> findByIds(List<Integer> attractionIds) throws SQLException {

		if (attractionIds == null || attractionIds.isEmpty()) {
			return new ArrayList<>();
		}

		StringBuilder placeholders = new StringBuilder();

		for (int index = 0; index < attractionIds.size(); index++) {

			if (index > 0) {
				placeholders.append(", ");
			}

			placeholders.append("?");
		}

		String sql = BASE_SELECT + " WHERE a.attraction_id IN (" + placeholders + ") ORDER BY a.attraction_name";

		List<Attraction> attractions = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			for (int index = 0; index < attractionIds.size(); index++) {
				statement.setInt(index + 1, attractionIds.get(index));
			}

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					attractions.add(mapAttraction(resultSet));
				}
			}
		}

		return attractions;
	}

	private Attraction mapAttraction(ResultSet resultSet) throws SQLException {

		City city = new City(resultSet.getInt("city_id"), resultSet.getString("city_name"));

		Attraction attraction = new Attraction();

		attraction.setAttractionId(resultSet.getInt("attraction_id"));

		attraction.setCity(city);

		attraction.setAttractionName(resultSet.getString("attraction_name"));

		attraction.setDescription(resultSet.getString("description"));

		attraction.setEntranceFee(resultSet.getBigDecimal("entrance_fee"));

		attraction.setImagePath(resultSet.getString("image_path"));

		double latitude = resultSet.getDouble("latitude");

		if (!resultSet.wasNull()) {
			attraction.setLatitude(latitude);
		}

		double longitude = resultSet.getDouble("longitude");

		if (!resultSet.wasNull()) {
			attraction.setLongitude(longitude);
		}

		return attraction;
	}
}
