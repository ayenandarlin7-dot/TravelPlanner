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

    private static final String SELECT = """
        SELECT city_id, city_name, region, popular_attraction, best_season,
               average_hotel_cost, average_food_cost, recommended_days,
               weather_type, city_description, tourism_rating,
               is_beach, is_mountain, is_historical, family_friendly,
               adventure_level, activities, food_types, relaxation_types
        FROM cities
        """;

    public List<City> findAll() throws SQLException {
        List<City> cities = new ArrayList<>();
        String sql = SELECT + " ORDER BY city_name";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) cities.add(mapCity(resultSet));
        }
        return cities;
    }

    public Optional<City> findById(int cityId) throws SQLException {
        String sql = SELECT + " WHERE city_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, cityId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapCity(resultSet));
            }
        }
        return Optional.empty();
    }

    private City mapCity(ResultSet rs) throws SQLException {
        City city = new City();
        city.setCityId(rs.getInt("city_id"));
        city.setCityName(rs.getString("city_name"));
        city.setRegion(rs.getString("region"));
        city.setPopularAttraction(rs.getString("popular_attraction"));
        city.setBestSeason(rs.getString("best_season"));
        city.setAverageHotelCost(rs.getBigDecimal("average_hotel_cost"));
        city.setAverageFoodCost(rs.getBigDecimal("average_food_cost"));
        city.setRecommendedDays(rs.getInt("recommended_days"));
        city.setWeatherType(rs.getString("weather_type"));
        city.setCityDescription(rs.getString("city_description"));
        city.setTourismRating(rs.getBigDecimal("tourism_rating"));
        city.setBeach(rs.getBoolean("is_beach"));
        city.setMountain(rs.getBoolean("is_mountain"));
        city.setHistorical(rs.getBoolean("is_historical"));
        city.setFamilyFriendly(rs.getBoolean("family_friendly"));
        city.setAdventureLevel(rs.getString("adventure_level"));
        city.setActivities(rs.getString("activities"));
        city.setFoodTypes(rs.getString("food_types"));
        city.setRelaxationTypes(rs.getString("relaxation_types"));
        return city;
    }
}
