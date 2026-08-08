package com.travelplanner.dao;

import com.travelplanner.model.User;
import com.travelplanner.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

public class UserDAO {

	public boolean createUser(User user) throws SQLException {

		String sql = """
				INSERT INTO users
				(full_name, email, password_hash)
				VALUES (?, ?, ?)
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			statement.setString(1, user.getFullName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getPasswordHash());

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				return false;
			}

			try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

				if (generatedKeys.next()) {
					user.setUserId(generatedKeys.getInt(1));
				}
			}

			return true;
		}
	}

	public Optional<User> findByEmail(String email) throws SQLException {

		String sql = """
				SELECT
				    user_id,
				    full_name,
				    email,
				    password_hash,
				    created_at
				FROM users
				WHERE email = ?
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, email);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {
					return Optional.of(mapUser(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	public Optional<User> findById(int userId) throws SQLException {

		String sql = """
				SELECT
				    user_id,
				    full_name,
				    email,
				    password_hash,
				    created_at
				FROM users
				WHERE user_id = ?
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, userId);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {
					return Optional.of(mapUser(resultSet));
				}
			}
		}

		return Optional.empty();
	}

	public boolean emailExists(String email) throws SQLException {

		String sql = """
				SELECT COUNT(*)
				FROM users
				WHERE email = ?
				""";

		try (Connection connection = DatabaseConnection.getConnection();

				PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setString(1, email);

			try (ResultSet resultSet = statement.executeQuery()) {

				if (resultSet.next()) {
					return resultSet.getInt(1) > 0;
				}
			}
		}

		return false;
	}

	private User mapUser(ResultSet resultSet) throws SQLException {

		User user = new User();

		user.setUserId(resultSet.getInt("user_id"));

		user.setFullName(resultSet.getString("full_name"));

		user.setEmail(resultSet.getString("email"));

		user.setPasswordHash(resultSet.getString("password_hash"));

		Timestamp createdAt = resultSet.getTimestamp("created_at");

		if (createdAt != null) {
			user.setCreatedAt(createdAt.toLocalDateTime());
		}

		return user;
	}
}