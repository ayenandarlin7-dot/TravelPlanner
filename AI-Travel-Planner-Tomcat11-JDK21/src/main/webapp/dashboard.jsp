<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.travelplanner.model.City"%>
<%@ page import="com.travelplanner.model.User"%>

<%
    List<City> cities =
            (List<City>) request.getAttribute("cities");

    User loggedInUser =
            (User) session.getAttribute("loggedInUser");
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Dashboard | AI Travel Planner</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="dashboard-body">

	<header class="navbar">

		<div class="nav-brand">AI Travel Planner</div>

		<div class="nav-links">

			<span> Welcome, <strong> <%= loggedInUser == null
                        ? "User"
                        : loggedInUser.getFullName() %>
			</strong>
			</span> <a href="${pageContext.request.contextPath}/trip-history"> Trip
				History </a> <a href="${pageContext.request.contextPath}/logout"
				class="logout-link"> Logout </a>

		</div>

	</header>

	<main class="dashboard-container">

		<section class="hero-section">

			<h1>Plan Your Next Journey</h1>

			<p>Select your cities, budget, transportation and preferred
				travel option.</p>

		</section>

		<% if (request.getAttribute("errorMessage") != null) { %>

		<div class="alert error-alert">
			<%= request.getAttribute("errorMessage") %>
		</div>

		<% } %>

		<section class="planner-card">

			<form action="${pageContext.request.contextPath}/route" method="post"
				id="tripForm">

				<div class="form-grid">

					<div class="form-group">

						<label for="startingCityId"> Starting City </label> <select
							id="startingCityId" name="startingCityId" required>

							<option value="">Select starting city</option>

							<% if (cities != null) {
                            for (City city : cities) { %>

							<option value="<%= city.getCityId() %>">
								<%= city.getCityName() %>
							</option>

							<%  }
                           } %>

						</select>

					</div>

					<div class="form-group">

						<label for="destinationCityId"> Destination </label> <select
							id="destinationCityId" name="destinationCityId" required>

							<option value="">Select destination</option>

							<% if (cities != null) {
                            for (City city : cities) { %>

							<option value="<%= city.getCityId() %>">
								<%= city.getCityName() %>
							</option>

							<%  }
                           } %>

						</select>

					</div>

					<div class="form-group">

						<label for="travelDate"> Travel Date </label> <input type="date"
							id="travelDate" name="travelDate" required>

					</div>

					<div class="form-group">

						<label for="budget"> Budget (MMK) </label> <input type="number"
							id="budget" name="budget" min="0" step="1000"
							placeholder="Example: 50000" required>

					</div>

					<div class="form-group">

						<label for="transportationId"> Transportation </label> <select
							id="transportationId" name="transportationId">

							<option value="">Any Transportation</option>

							<option value="1">Bus</option>
							<option value="2">Train</option>
							<option value="3">Flight</option>

						</select>

					</div>

					<div class="form-group">

						<label for="preference"> Preference </label> <select
							id="preference" name="preference" required>

							<option value="Cheapest">Cheapest</option>

							<option value="Fastest">Fastest</option>

							<option value="Shortest">Shortest</option>

						</select>

					</div>

				</div>

				<p id="tripValidationMessage" class="validation-message"></p>

				<button type="submit" class="primary-button">Find
					Recommended Route</button>

			</form>

		</section>

	</main>

	<script src="${pageContext.request.contextPath}/js/script.js"></script>

</body>
</html>