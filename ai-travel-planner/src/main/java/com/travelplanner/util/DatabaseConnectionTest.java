package com.travelplanner.util;

import java.sql.Connection;

public class DatabaseConnectionTest {

	public static void main(String[] args) {

		try (Connection connection = DatabaseConnection.getConnection()) {

			if (connection != null && !connection.isClosed()) {

				System.out.println("Database connected successfully!");

				System.out.println("Database: " + connection.getCatalog());
			}

		} catch (Exception exception) {

			System.out.println("Database connection failed!");

			exception.printStackTrace();
		}
	}
}