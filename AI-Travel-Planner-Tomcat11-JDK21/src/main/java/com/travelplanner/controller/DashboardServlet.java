package com.travelplanner.controller;

import java.io.IOException;
import java.sql.SQLException;

import com.travelplanner.dao.CityDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final CityDAO cityDAO = new CityDAO();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			request.setAttribute("cities", cityDAO.findAll());

			request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

		} catch (SQLException exception) {

			throw new ServletException("Unable to load cities.", exception);
		}
	}
}