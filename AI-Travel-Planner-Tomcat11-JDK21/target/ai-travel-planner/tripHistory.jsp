<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.travelplanner.model.Trip"%>
<%@ page import="com.travelplanner.model.Hotel"%>
<%@ page import="com.travelplanner.model.Attraction"%>

<%
    List<Trip> trips =
            (List<Trip>) request.getAttribute("trips");
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Trip History | AI Travel Planner</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="dashboard-body">

	<header class="navbar app-navbar">

		<div class="nav-brand">TravelMate AI</div>

		<div class="nav-links">

			<a href="${pageContext.request.contextPath}/dashboard"> Plan New
				Trip </a> <a href="${pageContext.request.contextPath}/logout"
				class="logout-link"> Logout </a>

		</div>

	</header>

	<main class="dashboard-container">

		<section class="hero-section">

			<h1>Saved Trip History</h1>

			<p>View and manage your previously saved trips.</p>

		</section>

		<% if ("success".equals(
            request.getParameter("saved")
    )) { %>

		<div class="alert success-alert">Trip saved successfully.</div>

		<% } %>

		<% if ("success".equals(
            request.getParameter("deleted")
    )) { %>

		<div class="alert success-alert">Trip deleted successfully.</div>

		<% } %>

		<% if (trips == null || trips.isEmpty()) { %>

		<section class="empty-result-card">

			<h2>No Saved Trips</h2>

			<p>You have not saved any trips yet.</p>

			<a href="${pageContext.request.contextPath}/dashboard"
				class="secondary-button"> Plan Your First Trip </a>

		</section>

		<% } else { %>

		<section class="history-grid">

			<% for (Trip trip : trips) { %>

			<article class="history-card">

				<div class="history-card-header">

					<h2>
						<%= trip.getRoute()
                                    .getStartingCity()
                                    .getCityName() %>

						→

						<%= trip.getRoute()
                                    .getDestinationCity()
                                    .getCityName() %>
					</h2>

					<div class="header-badges">

						<span class="transport-badge"> <%= trip.getRoute()
                                    .getTransportation()
                                    .getTransportName() %>

						</span>

						<span class="budget-badge <%= trip.getBudgetStatus() == null
                                        ? ""
                                        : trip.getBudgetStatus().toLowerCase().replace(" ", "-") %>"> <%= trip.getBudgetStatus() == null
                                        ? "-"
                                        : trip.getBudgetStatus() %>
						</span>

					</div>

				</div>

				<div class="history-details">

					<p>
						<span>Travel Date</span> <strong> <%= trip.getTravelDate() %>
						</strong>
					</p>

					<p>
						<span>Return Date</span> <strong> <%= trip.getReturnDate() == null
                                        ? "-"
                                        : trip.getReturnDate() %>
						</strong>
					</p>

					<p>
						<span>Travellers</span> <strong> <%= trip.getNumberOfTravellers() %>
						</strong>
					</p>

				<p>
					<span>Preference</span> <strong> <%= trip.getPreference() %>
					</strong>
				</p>

				<p>
					<span>Saved On</span> <strong> <%= trip.getCreatedAt() == null
                                        ? "-"
                                        : trip.getCreatedAt().toLocalDate() %>
					</strong>
				</p>

					<p>
						<span>Your Budget</span> <strong> <%= trip.getBudget() %> MMK
						</strong>
					</p>

					<p>
						<span>Distance</span> <strong> <%= trip.getRoute()
                                        .getDistanceKm() %> KM
						</strong>
					</p>

					<p>
						<span>Travel Time</span> <strong> <%= trip.getRoute()
                                        .getTravelTimeHours() %> Hours
						</strong>
					</p>

					<%
					Hotel savedHotel = trip.getSelectedHotel();
					%>

					<p>
						<span>Hotel</span> <strong> <%= savedHotel == null
                                        ? "-"
                                        : savedHotel.getHotelName() %>
						</strong>
					</p>

				</div>

				<div class="cost-breakdown">

					<div class="cost-row">
						<span>Transportation</span> <strong><%= trip.getTransportationCost() %> MMK</strong>
					</div>

					<div class="cost-row">
						<span>Hotel</span> <strong><%= trip.getHotelCost() %> MMK</strong>
					</div>

					<div class="cost-row">
						<span>Food</span> <strong><%= trip.getFoodCost() %> MMK</strong>
					</div>

					<div class="cost-row">
						<span>Attractions</span> <strong><%= trip.getAttractionCost() %> MMK</strong>
					</div>

					<div class="cost-row total-row">
						<span>Total Estimated Cost</span> <strong><%= trip.getTotalEstimatedCost() == null
                                        ? trip.getRecommendedCost()
                                        : trip.getTotalEstimatedCost() %> MMK</strong>
					</div>

				</div>

				<% if (trip.getAttractions() != null && !trip.getAttractions().isEmpty()) { %>

				<div class="selection-item">

					<span>Saved Attractions</span>

					<ul class="attraction-list">

						<% for (Attraction savedAttraction : trip.getAttractions()) { %>

						<li><span><%= savedAttraction.getAttractionName() %></span> <span
							class="fee"><%= savedAttraction.getEntranceFee() %> MMK</span></li>

						<% } %>

					</ul>

				</div>

				<% } %>

				<div class="card-actions">

					<a class="secondary-button"
						href="${pageContext.request.contextPath}/trip-details?tripId=<%= trip.getTripId() %>">
						View Details </a>

					<form action="${pageContext.request.contextPath}/delete-trip"
						method="post" onsubmit="return confirm('Delete this trip?');">

						<input type="hidden" name="tripId" value="<%= trip.getTripId() %>">

						<button type="submit" class="delete-button">Delete Trip</button>

					</form>

				</div>

			</article>

			<% } %>

		</section>

		<% } %>

	</main>

</body>
</html>