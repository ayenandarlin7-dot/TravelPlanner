<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.travelplanner.model.City"%>
<%@ page import="com.travelplanner.model.Trip"%>
<%@ page import="com.travelplanner.model.User"%>
<%@ page import="com.travelplanner.util.EscapeUtil"%>
<%
	List<City> cities = (List<City>) request.getAttribute("cities");
	User loggedInUser = (User) session.getAttribute("loggedInUser");
	Integer totalTrips = (Integer) request.getAttribute("totalTrips");
	List<Trip> upcomingTrips = (List<Trip>) request.getAttribute("upcomingTrips");
	Trip recentTrip = (Trip) request.getAttribute("recentTrip");
	String errorMessage = (String) request.getAttribute("errorMessage");

	String selectedStartingCityId = request.getParameter("startingCityId");
	String selectedDestinationCityId = request.getParameter("destinationCityId");
	String selectedTravelDate = request.getParameter("travelDate");
	String selectedReturnDate = request.getParameter("returnDate");
	String selectedBudget = request.getParameter("budget");
	String selectedTravellers = request.getParameter("travellers");
	String selectedTransportationId = request.getParameter("transportationId");
	String selectedPreference = request.getParameter("preference");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Dashboard | TravelMate AI</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/dashboard.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/responsive.css">
</head>
<body class="dashboard-body app-shell">

	<aside class="sidebar">
		<div class="sidebar-brand">
			<span class="brand-mark">TM</span>
			<span class="brand-name">TravelMate <span class="brand-ai">AI</span></span>
		</div>
		<nav class="sidebar-nav">
			<a href="<%= request.getContextPath() %>/dashboard" class="sidebar-link active">Dashboard</a>
			<a href="<%= request.getContextPath() %>/dashboard#plan-trip" class="sidebar-link">Plan New Trip</a>
			<a href="<%= request.getContextPath() %>/trip-history" class="sidebar-link">My Trips</a>
			<a href="<%= request.getContextPath() %>/trip-history" class="sidebar-link">Trip History</a>
			<a href="<%= request.getContextPath() %>/profile" class="sidebar-link">Profile</a>
			<a href="<%= request.getContextPath() %>/logout" class="sidebar-link sidebar-logout">Logout</a>
		</nav>
	</aside>

	<main class="app-main">

		<header class="app-topbar">
			<h1>Dashboard</h1>
			<span class="topbar-user">Welcome, <%= loggedInUser == null ? "User" : loggedInUser.getFullName() %></span>
		</header>

		<% if (errorMessage != null) { %>
		<div class="alert error-alert dashboard-alert"><%= EscapeUtil.escapeHtml(errorMessage) %></div>
		<% } %>

		<section class="stat-grid">

			<div class="stat-card">
				<span class="stat-label">Total Saved Trips</span>
				<strong class="stat-value"><%= totalTrips == null ? 0 : totalTrips %></strong>
			</div>

			<div class="stat-card">
				<span class="stat-label">Upcoming Trips</span>
				<strong class="stat-value"><%= upcomingTrips == null ? 0 : upcomingTrips.size() %></strong>
				<%
					if (upcomingTrips != null && !upcomingTrips.isEmpty()) {
				%>
				<ul class="stat-list">
					<%
						int shown = Math.min(upcomingTrips.size(), 3);
						for (int index = 0; index < shown; index++) {
							Trip trip = upcomingTrips.get(index);
					%>
					<li><%= trip.getRoute().getStartingCity().getCityName() %> &rarr; <%= trip.getRoute().getDestinationCity().getCityName() %> (<%= trip.getTravelDate() %>)</li>
					<%
						}
					%>
				</ul>
				<%
					}
				%>
			</div>

			<div class="stat-card">
				<span class="stat-label">Most Recent Trip</span>
				<%
					if (recentTrip != null) {
				%>
				<strong class="stat-value stat-value-sm"><%= recentTrip.getRoute().getStartingCity().getCityName() %> &rarr; <%= recentTrip.getRoute().getDestinationCity().getCityName() %></strong>
				<span class="stat-sub"><%= recentTrip.getTravelDate() %> &middot; <%= recentTrip.getRoute().getTransportation().getTransportName() %></span>
				<%
					} else {
				%>
				<strong class="stat-value stat-value-sm">No trips yet</strong>
				<span class="stat-sub">Plan your first trip below</span>
				<%
					}
				%>
			</div>

		</section>

		<section id="plan-trip" class="content-section">
			<div class="planner-card">
				<div class="planner-card-header">
					<h2>Plan New Trip</h2>
					<p>Tell us where you want to go and TravelMate AI will recommend the best route.</p>
				</div>

				<form action="<%= request.getContextPath() %>/route" method="post" id="tripForm" novalidate>

					<div class="form-grid">

						<div class="form-group">
							<label for="startingCityId">Starting City</label>
							<select id="startingCityId" name="startingCityId" required>
								<option value="">Select starting city</option>
								<% if (cities != null) { for (City city : cities) { %>
								<option value="<%= city.getCityId() %>" <%= String.valueOf(city.getCityId()).equals(selectedStartingCityId == null ? "" : selectedStartingCityId) ? "selected" : "" %>><%= city.getCityName() %></option>
								<% } } %>
							</select>
						</div>

						<div class="form-group">
							<label for="destinationCityId">Destination City</label>
							<select id="destinationCityId" name="destinationCityId" required>
								<option value="">Select destination</option>
								<% if (cities != null) { for (City city : cities) { %>
								<option value="<%= city.getCityId() %>" <%= String.valueOf(city.getCityId()).equals(selectedDestinationCityId == null ? "" : selectedDestinationCityId) ? "selected" : "" %>><%= city.getCityName() %></option>
								<% } } %>
							</select>
						</div>

						<div class="form-group">
							<label for="travelDate">Departure Date</label>
							<input type="date" id="travelDate" name="travelDate" required value="<%= selectedTravelDate == null ? "" : EscapeUtil.escapeHtml(selectedTravelDate) %>">
						</div>

						<div class="form-group">
							<label for="returnDate">Return Date <span class="optional-tag">(optional)</span></label>
							<input type="date" id="returnDate" name="returnDate" value="<%= selectedReturnDate == null ? "" : EscapeUtil.escapeHtml(selectedReturnDate) %>">
						</div>

						<div class="form-group">
							<label for="budget">Total Budget (MMK)</label>
							<input type="number" id="budget" name="budget" min="1" step="1000" placeholder="Example: 50000" required value="<%= selectedBudget == null ? "" : EscapeUtil.escapeHtml(selectedBudget) %>">
						</div>

						<div class="form-group">
							<label for="travellers">Number of Travellers</label>
							<input type="number" id="travellers" name="travellers" min="1" step="1" placeholder="Example: 2" value="<%= selectedTravellers == null ? "1" : EscapeUtil.escapeHtml(selectedTravellers) %>" required>
						</div>

						<div class="form-group">
							<label for="transportationId">Transportation</label>
							<select id="transportationId" name="transportationId">
								<option value="">Any Transportation</option>
								<option value="4" <%= "4".equals(selectedTransportationId) ? "selected" : "" %>>Auto</option>
								<option value="1" <%= "1".equals(selectedTransportationId) ? "selected" : "" %>>Bus</option>
								<option value="2" <%= "2".equals(selectedTransportationId) ? "selected" : "" %>>Train</option>
								<option value="3" <%= "3".equals(selectedTransportationId) ? "selected" : "" %>>Flight</option>
							</select>
						</div>

						<div class="form-group">
							<label for="preference">Preference</label>
							<select id="preference" name="preference" required>
								<option value="cheapest" <%= "cheapest".equals(selectedPreference) ? "selected" : "" %>>Cheapest</option>
								<option value="fastest" <%= "fastest".equals(selectedPreference) ? "selected" : "" %>>Fastest</option>
								<option value="shortest" <%= "shortest".equals(selectedPreference) ? "selected" : "" %>>Shortest</option>
								<option value="balanced" <%= "balanced".equals(selectedPreference) ? "selected" : "" %>>Balanced</option>
							</select>
						</div>

					</div>

					<p id="tripValidationMessage" class="validation-message" role="alert"></p>

					<button type="submit" class="primary-button">Generate My Trip</button>

				</form>
			</div>
		</section>

	</main>

	<script src="<%= request.getContextPath() %>/js/planner.js"></script>
</body>
</html>
