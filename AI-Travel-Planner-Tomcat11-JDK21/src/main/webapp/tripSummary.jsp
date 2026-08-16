<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.travelplanner.model.TripPlan"%>
<%@ page import="com.travelplanner.model.Hotel"%>
<%@ page import="com.travelplanner.model.Attraction"%>

<%
	TripPlan plan = (TripPlan) request.getAttribute("plan");
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Trip Summary | AI Travel Planner</title>

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

			<h1>Trip Summary</h1>

			<p>Review your complete plan before saving the trip.</p>

		</section>

		<% if (plan == null) { %>

		<section class="empty-result-card">

			<h2>Trip information is unavailable.</h2>

			<a href="${pageContext.request.contextPath}/dashboard"
				class="secondary-button"> Return to Dashboard </a>

		</section>

		<% } else {

			Hotel planHotel = plan.getHotel();

			String statusClass = plan.getBudgetStatus() == null ? ""
					: plan.getBudgetStatus().toLowerCase().replace(" ", "-");

			int remainingComparison = plan.getRemainingBudget() == null ? 1
					: plan.getRemainingBudget().compareTo(java.math.BigDecimal.ZERO);
		%>

		<section class="summary-card">

			<div class="summary-title">

				<h2>
					<%= plan.getRoute().getStartingCity().getCityName() %>
					→
					<%= plan.getRoute().getDestinationCity().getCityName() %>
				</h2>

				<span class="budget-badge <%= statusClass %>"> <%= plan.getBudgetStatus() %>
				</span>

			</div>

			<div class="route-information-grid">

				<div class="information-item">
					<span>Recommended Transportation</span> <strong> <%= plan.getRoute()
                                .getTransportation()
                                .getTransportName() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Travel Date</span> <strong> <%= request.getAttribute("travelDate") %>
					</strong>
				</div>

				<div class="information-item">
					<span>Return Date</span> <strong> <%= request.getAttribute("returnDate") == null
                                ? "-"
                                : request.getAttribute("returnDate") %>
					</strong>
				</div>

				<div class="information-item">
					<span>Travellers</span> <strong> <%= request.getAttribute("numberOfTravellers") == null
                                ? "-"
                                : request.getAttribute("numberOfTravellers") %>
					</strong>
				</div>

				<div class="information-item">
					<span>Trip Days</span> <strong> <%= plan.getTripDays() %> Days
					</strong>
				</div>

				<div class="information-item">
					<span>Hotel Nights</span> <strong> <%= plan.getHotelNights() %>
						Nights
					</strong>
				</div>

				<div class="information-item">
					<span>Travel Time</span> <strong> <%= plan.getRoute().getTravelTimeHours() %>
						Hours
					</strong>
				</div>

				<div class="information-item">
					<span>Distance</span> <strong> <%= plan.getRoute().getDistanceKm() %> KM
					</strong>
				</div>

				<div class="information-item information-item-wide">
					<span>Route Information</span> <strong> <%= plan.getRoute().getRouteInfo() == null
                                ? "No additional route information available."
                                : plan.getRoute().getRouteInfo() %>
					</strong>
				</div>

			</div>

			<div class="cost-breakdown">

				<div class="cost-row">
					<span>Transportation Cost</span> <strong><%= plan.getTransportationCost() %>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>Hotel Cost (<%= plan.getHotelNights() %> nights x <%= plan.getRoomsRequired() %>
						room(s))</span> <strong><%= plan.getHotelCost() %> MMK</strong>
				</div>

				<div class="cost-row">
					<span>Estimated Food Cost (<%= plan.getTripDays() %> days)</span> <strong><%= plan.getFoodCost() %>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>Attraction Cost</span> <strong><%= plan.getAttractionCost() %>
						MMK</strong>
				</div>

				<div class="cost-row total-row">
					<span>Total Estimated Cost</span> <strong><%= plan.getTotalEstimatedCost() %>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>User Budget</span> <strong><%= request.getAttribute("budget") %>
						MMK</strong>
				</div>

				<div class="cost-row <%= remainingComparison < 0 ? "total-row excess-row" : "within-budget-row" %>">
					<span><%= remainingComparison < 0 ? "Excess Amount (over budget)" : "Remaining Budget" %></span> <strong><%= remainingComparison < 0 ? "-" : "" %><%= plan.getRemainingBudget() %>
						MMK</strong>
				</div>

			</div>

			<% if (planHotel != null) { %>

			<div class="section-heading">
				<h3>Recommended Hotel</h3>
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
						<span>Rooms Required</span> <strong><%= plan.getRoomsRequired() %></strong>
					</div>

					<div class="hotel-detail-item">
						<span>Nights</span> <strong><%= plan.getHotelNights() %></strong>
					</div>

					<div class="hotel-detail-item">
						<span>Total Hotel Cost</span> <strong><%= plan.getHotelCost() %> MMK</strong>
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

			<% if (plan.getAttractions() == null || plan.getAttractions().isEmpty()) { %>

			<p class="muted-note">No attractions could be included within the
				remaining budget.</p>

			<% } else { %>

			<div class="attraction-card-grid">

				<% for (Attraction attraction : plan.getAttractions()) { %>

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
					<%= plan.getRoute().getDistanceKm() %> KM &middot; approx.
					<%= plan.getRoute().getTravelTimeHours() %> hours &middot; The
					drawn route is <strong>approximate</strong> and is not the actual
					road or railway path.
				</p>

			</div>

			<form action="${pageContext.request.contextPath}/save-trip"
				method="post">

				<input type="hidden" name="routeId"
					value="<%= plan.getRoute().getRouteId() %>"> <input
					type="hidden" name="travelDate"
					value="<%= request.getAttribute("travelDate") %>">

				<input type="hidden" name="returnDate"
					value="<%= request.getAttribute("returnDate") == null
                            ? ""
                            : request.getAttribute("returnDate") %>">

				<input type="hidden" name="travellers"
					value="<%= request.getAttribute("numberOfTravellers") == null
                            ? ""
                            : request.getAttribute("numberOfTravellers") %>">

				<input type="hidden" name="budget"
					value="<%= request.getAttribute("budget") %>"> <input
					type="hidden" name="preference"
					value="<%= request.getAttribute("preference") %>">

				<% if (planHotel != null) { %>
				<input type="hidden" name="hotelId"
					value="<%= planHotel.getHotelId() %>">
				<% } %>

				<input type="hidden" name="foodTier"
					value="<%= plan.getFoodEstimate() == null
                            ? ""
                            : plan.getFoodEstimate().getTier() %>">

				<% for (Attraction attraction : plan.getAttractions()) { %>
				<input type="hidden" name="attractionIds"
					value="<%= attraction.getAttractionId() %>">
				<% } %>

				<button type="submit" class="primary-button">Confirm &amp;
					Save Trip</button>

			</form>

		</section>

		<% } %>

	</main>

	<script>
		window.tripMapConfig = {
			startingCity: {
				name: "<%= plan == null ? ""
						: plan.getRoute().getStartingCity().getCityName() %>",
				lat: <%= plan == null || plan.getRoute().getStartingCity().getLatitude() == null
						? "null"
						: plan.getRoute().getStartingCity().getLatitude() %>,
				lng: <%= plan == null || plan.getRoute().getStartingCity().getLongitude() == null
						? "null"
						: plan.getRoute().getStartingCity().getLongitude() %>
			},
			destinationCity: {
				name: "<%= plan == null
						? ""
						: plan.getRoute().getDestinationCity().getCityName() %>",
				lat: <%= plan == null || plan.getRoute().getDestinationCity().getLatitude() == null
						? "null"
						: plan.getRoute().getDestinationCity().getLatitude() %>,
				lng: <%= plan == null || plan.getRoute().getDestinationCity().getLongitude() == null
						? "null"
						: plan.getRoute().getDestinationCity().getLongitude() %>
			},
			attractions: [
				<% if (plan != null) {
					for (int index = 0; index < plan.getAttractions().size(); index++) {

						Attraction mapAttraction = plan.getAttractions().get(index);

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
			distanceKm: <%= plan == null ? 0 : plan.getRoute().getDistanceKm() %>,
			travelTimeHours: <%= plan == null ? 0 : plan.getRoute().getTravelTimeHours() %>,
			approximateRouteLabel: true
		};
	</script>

	<script
		src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

	<script
		src="${pageContext.request.contextPath}/js/map.js"></script>

</body>
</html>
