package com.travelplanner.dao;

import com.travelplanner.model.City;
import com.travelplanner.model.Hotel;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HotelDAO {

	private static final String BASE_SELECT = """
			SELECT
			    h.hotel_id,
			    h.hotel_name,
			    h.category,
			    h.price_per_night,
			    h.room_capacity,
			    h.rating,
			    h.location_info,

			    c.city_id,
			    c.city_name

			FROM hotels h

			JOIN cities c
			    ON h.city_id = c.city_id
			""";

	public List<Hotel> findAll() throws SQLException {

		String sql = BASE_SELECT + " ORDER BY c.city_name, h.price_per_night";

		List<Hotel> hotels = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql);

				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {

				hotels.add(mapHotel(resultSet));
			}
		}

		return hotels;
	}

	public List<Hotel> findByCityId(int cityId) throws SQLException {

		String sql = BASE_SELECT + " WHERE h.city_id = ? ORDER BY h.price_per_night";

		List<Hotel> hotels = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, cityId);

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					hotels.add(mapHotel(resultSet));
				}
			}
		}

		return hotels;
	}

	public List<Hotel> findByCityIdAndCategory(int cityId, String category) throws SQLException {

		String sql = BASE_SELECT
				+ " WHERE h.city_id = ? AND h.category = ? ORDER BY h.price_per_night";

		List<Hotel> hotels = new ArrayList<>();

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, cityId);
			statement.setString(2, category);

			try (ResultSet resultSet = statement.executeQuery()) {

				while (resultSet.next()) {

					hotels.add(mapHotel(resultSet));
				}
			}
		}

		return hotels;
	}

	public Optional<Hotel> findById(int hotelId) throws SQLException {

		String sql = BASE_SELECT + " WHERE h.hotel_id = ?";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, hotelId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {

					return Optional.of(mapHotel(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	private Hotel mapHotel(ResultSet resultSet) throws SQLException {

		City city = new City(resultSet.getInt("city_id"), resultSet.getString("city_name"));

		Hotel hotel = new Hotel();

		hotel.setHotelId(resultSet.getInt("hotel_id"));

		hotel.setCity(city);

		hotel.setHotelName(resultSet.getString("hotel_name"));

		hotel.setCategory(resultSet.getString("category"));

		hotel.setPricePerNight(resultSet.getBigDecimal("price_per_night"));

		hotel.setRoomCapacity(resultSet.getInt("room_capacity"));

		hotel.setRating(resultSet.getBigDecimal("rating"));

		hotel.setLocationInfo(resultSet.getString("location_info"));

		return hotel;
	}
}
