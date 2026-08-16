package com.travelplanner.dao;

import com.travelplanner.model.City;
import com.travelplanner.model.FoodEstimate;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FoodEstimateDAO {

	private static final String BASE_SELECT = """
			SELECT
			    fe.food_estimate_id,
			    fe.tier,
			    fe.daily_cost_per_person,

			    c.city_id,
			    c.city_name

			FROM food_estimates fe

			JOIN cities c
			    ON fe.city_id = c.city_id
			""";

	public List<FoodEstimate> findByCityId(int cityId) throws SQLException {

		String sql = BASE_SELECT + " WHERE fe.city_id = ? ORDER BY fe.daily_cost_per_person";

		List<FoodEstimate> estimates = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, cityId);

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					estimates.add(mapFoodEstimate(resultSet));
				}
			}
		}

		return estimates;
	}

	public Optional<FoodEstimate> findById(int foodEstimateId) throws SQLException {

		String sql = BASE_SELECT + " WHERE fe.food_estimate_id = ?";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, foodEstimateId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					return Optional.of(mapFoodEstimate(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	public Optional<FoodEstimate> findByCityIdAndTier(int cityId, String tier) throws SQLException {

		String sql = BASE_SELECT + " WHERE fe.city_id = ? AND fe.tier = ?";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, cityId);
			statement.setString(2, tier);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					return Optional.of(mapFoodEstimate(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	private FoodEstimate mapFoodEstimate(ResultSet resultSet) throws SQLException {

		City city = new City(resultSet.getInt("city_id"), resultSet.getString("city_name"));

		FoodEstimate estimate = new FoodEstimate();

		estimate.setFoodEstimateId(resultSet.getInt("food_estimate_id"));

		estimate.setCity(city);

		estimate.setTier(resultSet.getString("tier"));

		estimate.setDailyCostPerPerson(resultSet.getBigDecimal("daily_cost_per_person"));

		return estimate;
	}
}
