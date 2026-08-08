package com.travelplanner.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

	private static final Properties DB_PROPERTIES = loadProperties();

	private static Properties loadProperties() {

		Properties properties = new Properties();

		properties.setProperty("db.url",
				"jdbc:mysql://localhost:3306/travel_planner?useSSL=false&serverTimezone=Asia/Yangon&allowPublicKeyRetrieval=true");
		properties.setProperty("db.username", "root");
		properties.setProperty("db.password", "travel@123");

		try (InputStream inputStream = DatabaseConnection.class.getClassLoader()
				.getResourceAsStream("database.properties")) {

			if (inputStream != null) {
				properties.load(inputStream);
			}

		} catch (IOException exception) {
			throw new RuntimeException("Unable to load database.properties.", exception);
		}

		return properties;
	}

	private static void loadDriver() throws SQLException {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException exception) {
			throw new SQLException(
					"MySQL JDBC driver not found. Add mysql-connector-j to WEB-INF/lib and redeploy the application.",
					exception);
		}
	}

	public static Connection getConnection() throws SQLException {

		loadDriver();

		return DriverManager.getConnection(
				DB_PROPERTIES.getProperty("db.url"),
				DB_PROPERTIES.getProperty("db.username"),
				DB_PROPERTIES.getProperty("db.password"));
	}

}
