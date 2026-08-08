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
            r.route_id, r.distance_km, r.travel_time_hours, r.estimated_cost,
            sc.city_id AS start_city_id, sc.city_name AS start_city_name,
            dc.city_id AS destination_city_id, dc.city_name AS destination_city_name,
            dc.region, dc.popular_attraction, dc.best_season,
            dc.average_hotel_cost, dc.average_food_cost, dc.recommended_days,
            dc.weather_type, dc.city_description, dc.tourism_rating,
            dc.is_beach, dc.is_mountain, dc.is_historical, dc.family_friendly,
            dc.adventure_level, dc.activities, dc.food_types, dc.relaxation_types,
            t.transportation_id, t.transport_name
        FROM routes r
        JOIN cities sc ON r.starting_city_id = sc.city_id
        JOIN cities dc ON r.destination_city_id = dc.city_id
        JOIN transportations t ON r.transportation_id = t.transportation_id
        """;

    public List<Route> findRoutes(int startingCityId, int destinationCityId, Integer transportationId)
            throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append(" WHERE r.starting_city_id = ? AND r.destination_city_id = ? ");
        if (transportationId != null) sql.append(" AND r.transportation_id = ? ");
        sql.append(" ORDER BY r.estimated_cost ASC");
        return executeRoutes(sql.toString(), startingCityId, destinationCityId, transportationId);
    }

    /** All possible destinations from the selected starting city. */
    public List<Route> findRoutesFromCity(int startingCityId, Integer transportationId) throws SQLException {
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        sql.append(" WHERE r.starting_city_id = ? ");
        if (transportationId != null) sql.append(" AND r.transportation_id = ? ");
        sql.append(" ORDER BY r.estimated_cost ASC");
        return executeRoutesFromStart(sql.toString(), startingCityId, transportationId);
    }

    public Optional<Route> findById(int routeId) throws SQLException {
        String sql = BASE_SELECT + " WHERE r.route_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, routeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) return Optional.of(mapRoute(resultSet));
            }
        }
        return Optional.empty();
    }

    private List<Route> executeRoutes(String sql, int start, int destination, Integer transport) throws SQLException {
        List<Route> routes = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, start);
            statement.setInt(2, destination);
            if (transport != null) statement.setInt(3, transport);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) routes.add(mapRoute(rs));
            }
        }
        return routes;
    }

    private List<Route> executeRoutesFromStart(String sql, int start, Integer transport) throws SQLException {
        List<Route> routes = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, start);
            if (transport != null) statement.setInt(2, transport);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) routes.add(mapRoute(rs));
            }
        }
        return routes;
    }

    private Route mapRoute(ResultSet rs) throws SQLException {
        Route route = new Route();
        route.setRouteId(rs.getInt("route_id"));
        route.setDistanceKm(rs.getDouble("distance_km"));
        route.setTravelTimeHours(rs.getDouble("travel_time_hours"));
        route.setEstimatedCost(rs.getBigDecimal("estimated_cost"));

        City start = new City(rs.getInt("start_city_id"), rs.getString("start_city_name"));
        City destination = new City(rs.getInt("destination_city_id"), rs.getString("destination_city_name"));
        destination.setRegion(rs.getString("region"));
        destination.setPopularAttraction(rs.getString("popular_attraction"));
        destination.setBestSeason(rs.getString("best_season"));
        destination.setAverageHotelCost(rs.getBigDecimal("average_hotel_cost"));
        destination.setAverageFoodCost(rs.getBigDecimal("average_food_cost"));
        destination.setRecommendedDays(rs.getInt("recommended_days"));
        destination.setWeatherType(rs.getString("weather_type"));
        destination.setCityDescription(rs.getString("city_description"));
        destination.setTourismRating(rs.getBigDecimal("tourism_rating"));
        destination.setBeach(rs.getBoolean("is_beach"));
        destination.setMountain(rs.getBoolean("is_mountain"));
        destination.setHistorical(rs.getBoolean("is_historical"));
        destination.setFamilyFriendly(rs.getBoolean("family_friendly"));
        destination.setAdventureLevel(rs.getString("adventure_level"));
        destination.setActivities(rs.getString("activities"));
        destination.setFoodTypes(rs.getString("food_types"));
        destination.setRelaxationTypes(rs.getString("relaxation_types"));

        Transportation transportation = new Transportation(
                rs.getInt("transportation_id"), rs.getString("transport_name"));
        route.setStartingCity(start);
        route.setDestinationCity(destination);
        route.setTransportation(transportation);
        return route;
    }
}
