<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.travelplanner.model.Trip"%>
<%@ page import="com.travelplanner.model.Hotel"%>
<%@ page import="com.travelplanner.model.Attraction"%>

<%
	Trip trip = (Trip) request.getAttribute("trip");
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Trip Details | AI Travel Planner</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">

<link rel="stylesheet"
	href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
</head>

<body class="dashboard-body">

	<header class="navbar app-navbar">

		<div class="nav-brand">TravelMate AI</div>

		<div class="nav-links">

			<a href="${pageContext.request.contextPath}/dashboard"> Dashboard
			</a> <a href="${pageContext.request.contextPath}/trip-history"> Trip
				History </a> <a href="${pageContext.request.contextPath}/logout"
				class="logout-link"> Logout </a>

		</div>

	</header>

	<main class="dashboard-container">

		<section class="hero-section">

			<h1>Trip Details</h1>

			<p>Complete information for your saved trip.</p>

		</section>

		<% if (trip == null) { %>

		<section class="empty-result-card">

			<h2>Trip information is unavailable.</h2>

			<a href="${pageContext.request.contextPath}/trip-history"
				class="secondary-button"> Return to Trip History </a>

		</section>

		<% } else {

			Hotel planHotel = trip.getSelectedHotel();

			String statusClass = trip.getBudgetStatus() == null ? ""
					: trip.getBudgetStatus().toLowerCase().replace(" ", "-");

			java.math.BigDecimal totalTripCost = trip.getTotalEstimatedCost() == null
					? trip.getRecommendedCost()
					: trip.getTotalEstimatedCost();

			java.math.BigDecimal remainingBudget = trip.getBudget() == null || totalTripCost == null
					? null
					: trip.getBudget().subtract(totalTripCost);

			int remainingComparison = remainingBudget == null ? 1
					: remainingBudget.compareTo(java.math.BigDecimal.ZERO);
		%>

		<section class="summary-card">

			<div class="summary-title">

				<h2>
					<%= trip.getRoute().getStartingCity().getCityName() %>
					→
					<%= trip.getRoute().getDestinationCity().getCityName() %>
				</h2>

				<span class="budget-badge <%= statusClass %>"> <%= trip.getBudgetStatus() %>
				</span>

			</div>

			<div class="route-information-grid">

				<div class="information-item">
					<span>Recommended Transportation</span> <strong> <%= trip.getRoute()
                                .getTransportation()
                                .getTransportName() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Travel Date</span> <strong> <%= trip.getTravelDate() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Return Date</span> <strong> <%= trip.getReturnDate() == null
                                ? "-"
                                : trip.getReturnDate() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Travellers</span> <strong> <%= trip.getNumberOfTravellers() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Preference</span> <strong> <%= trip.getPreference() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Saved On</span> <strong> <%= trip.getCreatedAt() == null
                                ? "-"
                                : trip.getCreatedAt().toLocalDate() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Travel Time</span> <strong> <%= trip.getRoute().getTravelTimeHours() %>
						Hours
					</strong>
				</div>

				<div class="information-item">
					<span>Distance</span> <strong> <%= trip.getRoute().getDistanceKm() %> KM
					</strong>
				</div>

				<div class="information-item information-item-wide">
					<span>Route Information</span> <strong> <%= trip.getRoute().getRouteInfo() == null
                                ? "No additional route information available."
                                : trip.getRoute().getRouteInfo() %>
					</strong>
				</div>

			</div>

			<div class="cost-breakdown">

				<div class="cost-row">
					<span>Transportation Cost</span> <strong><%= trip.getTransportationCost() %>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>Hotel Cost</span> <strong><%= trip.getHotelCost() %> MMK</strong>
				</div>

				<div class="cost-row">
					<span>Estimated Food Cost</span> <strong><%= trip.getFoodCost() %>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>Attraction Cost</span> <strong><%= trip.getAttractionCost() %>
						MMK</strong>
				</div>

				<div class="cost-row total-row">
					<span>Total Estimated Cost</span> <strong><%= trip.getTotalEstimatedCost() == null
                                ? trip.getRecommendedCost()
                                : trip.getTotalEstimatedCost() %> MMK</strong>
				</div>

				<div class="cost-row">
					<span>User Budget</span> <strong><%= trip.getBudget() %>
						MMK</strong>
				</div>

				<div class="cost-row <%= remainingComparison < 0 ? "total-row excess-row" : "within-budget-row" %>">
					<span><%= remainingComparison < 0 ? "Excess Amount (over budget)" : "Remaining Budget" %></span> <strong><%= remainingComparison < 0 ? "-" : "" %><%= remainingBudget %>
						MMK</strong>
				</div>

			</div>

			<% if (planHotel != null) { %>

			<div class="section-heading">
				<h3>Selected Hotel</h3>
			</div>

			<article class="hotel-card">

				<div class="hotel-card-header">

					<div>
						<h3><%= planHotel.getHotelName() %></h3>
						<span class="category-tag"><%= planHotel.getCategory() %></span>
					</div>

					<div class="hotel-rating">
						<span>Rating</span> <strong><%= planHotel.getRating() %> / 10</strong>
					</div>

				</div>

				<div class="hotel-details-grid">

					<div class="hotel-detail-item">
						<span>Price per Night</span> <strong><%= planHotel.getPricePerNight() %> MMK</strong>
					</div>

					<div class="hotel-detail-item">
						<span>Location</span> <strong><%= planHotel.getLocationInfo() == null
                                ? "-"
                                : planHotel.getLocationInfo() %></strong>
					</div>

				</div>

			</article>

			<% } %>

			<div class="section-heading">
				<h3>Tourist Attractions</h3>
			</div>

			<% if (trip.getAttractions() == null || trip.getAttractions().isEmpty()) { %>

			<p class="muted-note">No attractions were included in this trip.</p>

			<% } else { %>

			<div class="attraction-card-grid">

				<% for (Attraction attraction : trip.getAttractions()) { %>

				<article class="attraction-card">

					<div class="attraction-card-image">
						<% if (attraction.getImagePath() != null && !attraction.getImagePath().isBlank()) { %>
						<img
							src="${pageContext.request.contextPath}<%= attraction.getImagePath() %>"
							alt="<%= attraction.getAttractionName() %>" loading="lazy"
							onerror="this.style.display='none'">
						<% } else { %>
						<div class="attraction-image-placeholder">No Image</div>
						<% } %>
					</div>

					<div class="attraction-card-body">

						<h4><%= attraction.getAttractionName() %></h4>

						<p><%= attraction.getDescription() == null
                                ? ""
                                : attraction.getDescription() %></p>

						<span class="fee-tag"><%= attraction.getEntranceFee() %> MMK</span>

					</div>

				</article>

				<% } %>

			</div>

			<% } %>

			<div class="section-heading">
				<h3>Route Map</h3>
			</div>

			<div class="map-card">

				<div id="trip-map" class="trip-map"></div>

				<p class="map-note">
					<%= trip.getRoute().getDistanceKm() %> KM &middot; approx.
					<%= trip.getRoute().getTravelTimeHours() %> hours &middot; The
					drawn route is <strong>approximate</strong> and is not the actual
					road or railway path.
				</p>

			</div>

			<div class="card-actions">

				<a href="${pageContext.request.contextPath}/trip-history"
					class="secondary-button"> Back to Trip History </a>

				<form action="${pageContext.request.contextPath}/delete-trip"
					method="post" onsubmit="return confirm('Delete this trip?');">

					<input type="hidden" name="tripId" value="<%= trip.getTripId() %>">

					<button type="submit" class="delete-button">Delete Trip</button>

				</form>

			</div>

		</section>

		<% } %>

	</main>

	<script>
		window.tripMapConfig = {
			startingCity: {
				name: "<%= trip == null
						? ""
						: trip.getRoute().getStartingCity().getCityName() %>",
				lat: <%= trip == null || trip.getRoute().getStartingCity().getLatitude() == null
						? "null"
						: trip.getRoute().getStartingCity().getLatitude() %>,
				lng: <%= trip == null || trip.getRoute().getStartingCity().getLongitude() == null
						? "null"
						: trip.getRoute().getStartingCity().getLongitude() %>
			},
			destinationCity: {
				name: "<%= trip == null
						? ""
						: trip.getRoute().getDestinationCity().getCityName() %>",
				lat: <%= trip == null || trip.getRoute().getDestinationCity().getLatitude() == null
						? "null"
						: trip.getRoute().getDestinationCity().getLatitude() %>,
				lng: <%= trip == null || trip.getRoute().getDestinationCity().getLongitude() == null
						? "null"
						: trip.getRoute().getDestinationCity().getLongitude() %>
			},
			attractions: [
				<% if (trip != null) {
					for (int index = 0; index < trip.getAttractions().size(); index++) {

						Attraction mapAttraction = trip.getAttractions().get(index);

						if (index > 0) {
				%>,<%
				}
				%>
				{
					name: "<%= mapAttraction.getAttractionName() %>",
					lat: <%= mapAttraction.getLatitude() == null ? "null" : mapAttraction.getLatitude() %>,
					lng: <%= mapAttraction.getLongitude() == null ? "null" : mapAttraction.getLongitude() %>,
					fee: <%= mapAttraction.getEntranceFee() == null ? 0 : mapAttraction.getEntranceFee() %>
				}
				<%
				}
				}
				%>
			],
			distanceKm: <%= trip == null ? 0 : trip.getRoute().getDistanceKm() %>,
			travelTimeHours: <%= trip == null ? 0 : trip.getRoute().getTravelTimeHours() %>,
			approximateRouteLabel: true
		};
	</script>

	<script
		src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

	<script
		src="${pageContext.request.contextPath}/js/map.js"></script>

</body>
</html>
