package com.travelplanner.service;

import java.sql.SQLException;
import java.util.Optional;

import com.travelplanner.dao.UserDAO;
import com.travelplanner.model.User;
import com.travelplanner.util.PasswordUtil;

public class UserService {

	private UserDAO userDAO = new UserDAO();

	public boolean register(User user) throws SQLException {

		if (userDAO.emailExists(user.getEmail())) {
			return false;
		}

		user.setPasswordHash(PasswordUtil.hashPassword(user.getPasswordHash()));

		return userDAO.createUser(user);
	}

	public User login(String email, String password) throws SQLException {

		if (password == null || password.isBlank()) {
			return null;
		}

		Optional<User> user = userDAO.findByEmail(email);

		if (user.isPresent()) {

			String hashed = PasswordUtil.hashPassword(password);

			if (hashed.equals(user.get().getPasswordHash())) {

				return user.get();
			}
		}

		return null;
	}

}
