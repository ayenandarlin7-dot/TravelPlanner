<<<<<<< HEAD
package com.travelplanner.service;

import java.sql.SQLException;
import java.util.List;

import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.Route;

public class RouteService {

	private RouteDAO routeDAO = new RouteDAO();

	public List<Route> searchRoute(

			int startCity, int destination, Integer transportation

	) throws SQLException {

		return routeDAO.findRoutes(

				startCity, destination, transportation

		);

	}

}
=======
package com.travelplanner.service;

import java.sql.SQLException;
import java.util.List;

import com.travelplanner.dao.RouteDAO;
import com.travelplanner.model.Route;

public class RouteService {

	private RouteDAO routeDAO = new RouteDAO();

	public List<Route> searchRoute(

			int startCity, int destination, Integer transportation

	) throws SQLException {

		return routeDAO.findRoutes(

				startCity, destination, transportation

		);

	}

}
>>>>>>> 383055483b6f17e88e95db72c4b5bc0442235184
