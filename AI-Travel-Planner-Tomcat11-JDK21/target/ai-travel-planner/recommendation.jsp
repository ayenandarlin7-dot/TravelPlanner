<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.nio.charset.StandardCharsets"%>
<%@ page import="com.travelplanner.model.TripPlan"%>
<%@ page import="com.travelplanner.model.Hotel"%>
<%@ page import="com.travelplanner.model.Attraction"%>
<%@ page import="com.travelplanner.model.City"%>
<%@ page import="com.travelplanner.model.BudgetSuggestion"%>

<%
	TripPlan recommendedPlan = (TripPlan) request.getAttribute("recommendedPlan");

	List<TripPlan> plans = (List<TripPlan>) request.getAttribute("plans");

	List<BudgetSuggestion> budgetSuggestions = (List<BudgetSuggestion>) request.getAttribute("budgetSuggestions");

	Integer startingCityId = (Integer) request.getAttribute("startingCityId");

	Integer destinationCityId = (Integer) request.getAttribute("destinationCityId");

	Object travelDateAttr = request.getAttribute("travelDate");

	Object returnDateAttr = request.getAttribute("returnDate");

	Object travellersAttr = request.getAttribute("numberOfTravellers");

	Object budgetAttr = request.getAttribute("budget");

	Object preferenceAttr = request.getAttribute("preference");
%>

<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="UTF-8">

<meta name="viewport"
	content="width=device-width,
                   initial-scale=1.0">

<title>Recommended Trip Plan | AI Travel Planner</title>

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

			<h1>Recommended Trip Plan</h1>

			<p>Your route, hotel, food tier and attractions with the full cost
				breakdown.</p>

		</section>

		<%
		if (recommendedPlan == null) {
		%>

		<section class="empty-result-card">

			<h2>No Plan Found</h2>

			<p>There is no available plan matching your trip information and
				transportation choice.</p>

			<a href="${pageContext.request.contextPath}/dashboard"
				class="secondary-button"> Change Trip Information </a>

		</section>

		<%
		} else {

			Hotel planHotel = recommendedPlan.getHotel();

			String statusClass = recommendedPlan.getBudgetStatus() == null ? ""
					: recommendedPlan.getBudgetStatus().toLowerCase().replace(" ", "-");
		%>

		<section class="best-route-card">

			<div class="recommended-label">Best Recommended Plan</div>

			<div class="plan-title-row">

				<h2>
					<%=recommendedPlan.getRoute().getStartingCity().getCityName()%>
					→
					<%=recommendedPlan.getRoute().getDestinationCity().getCityName()%>
				</h2>

				<span class="budget-badge <%=statusClass%>"> <%=recommendedPlan.getBudgetStatus()%>
				</span>

			</div>

			<div class="route-information-grid">

				<div class="information-item">

					<span>Recommended Transportation</span> <strong> <%=recommendedPlan.getRoute().getTransportation().getTransportName()%>
					</strong>

				</div>

				<div class="information-item">

					<span>Preference</span> <strong> <%=preferenceAttr == null ? "-" : preferenceAttr%>
					</strong>

				</div>

				<div class="information-item">

					<span>Trip Days</span> <strong> <%=recommendedPlan.getTripDays()%>
						Days
					</strong>

				</div>

				<div class="information-item">

					<span>Hotel Nights</span> <strong> <%=recommendedPlan.getHotelNights()%>
						Nights
					</strong>

				</div>

				<div class="information-item">

					<span>Travel Time</span> <strong> <%=recommendedPlan.getRoute().getTravelTimeHours()%>
						Hours
					</strong>

				</div>

				<div class="information-item">

					<span>Distance</span> <strong> <%=recommendedPlan.getRoute().getDistanceKm()%>
						KM
					</strong>

				</div>

				<div class="information-item information-item-wide">

					<span>Route Information</span> <strong> <%=recommendedPlan.getRoute().getRouteInfo() == null
                            ? "No additional route information available."
                            : recommendedPlan.getRoute().getRouteInfo()%>
					</strong>

				</div>

			</div>

			<div class="cost-breakdown">

				<div class="cost-row">
					<span>Transportation Cost</span> <strong><%=recommendedPlan.getTransportationCost()%>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>Hotel Cost (<%=recommendedPlan.getHotelNights()%> nights x <%=recommendedPlan.getRoomsRequired()%>
						room(s))</span> <strong><%=recommendedPlan.getHotelCost()%>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>Estimated Food Cost (<%=recommendedPlan.getTripDays()%> days)</span> <strong><%=recommendedPlan.getFoodCost()%>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>Attraction Cost</span> <strong><%=recommendedPlan.getAttractionCost()%>
						MMK</strong>
				</div>

				<div class="cost-row total-row">
					<span>Total Estimated Cost</span> <strong><%=recommendedPlan.getTotalEstimatedCost()%>
						MMK</strong>
				</div>

				<div class="cost-row">
					<span>User Budget</span> <strong><%=budgetAttr%> MMK</strong>
				</div>

				<%
				int remainingComparison = recommendedPlan.getRemainingBudget() == null ? 1
						: recommendedPlan.getRemainingBudget().compareTo(java.math.BigDecimal.ZERO);
				%>

				<div class="cost-row <%=remainingComparison < 0 ? "total-row excess-row" : "within-budget-row"%>">
					<span><%=remainingComparison < 0 ? "Excess Amount (over budget)" : "Remaining Budget"%></span> <strong><%=remainingComparison < 0 ? "-" : ""%><%=recommendedPlan.getRemainingBudget()%>
						MMK</strong>
				</div>

			</div>

			<%
			if (planHotel != null) {
			%>

			<div class="section-heading">

				<h3>Recommended Hotel</h3>

			</div>

			<article class="hotel-card">

				<div class="hotel-card-header">

					<div>

						<h3><%=planHotel.getHotelName()%></h3>

						<span class="category-tag"><%=planHotel.getCategory()%></span>

					</div>

					<div class="hotel-rating">

						<span>Rating</span> <strong><%=planHotel.getRating()%>
							/ 10</strong>

					</div>

				</div>

				<div class="hotel-details-grid">

					<div class="hotel-detail-item">
						<span>Price per Night</span> <strong><%=planHotel.getPricePerNight()%>
							MMK</strong>
					</div>

					<div class="hotel-detail-item">
						<span>Rooms Required</span> <strong><%=recommendedPlan.getRoomsRequired()%></strong>
					</div>

					<div class="hotel-detail-item">
						<span>Nights</span> <strong><%=recommendedPlan.getHotelNights()%></strong>
					</div>

					<div class="hotel-detail-item">
						<span>Total Hotel Cost</span> <strong><%=recommendedPlan.getHotelCost()%>
							MMK</strong>
					</div>

					<div class="hotel-detail-item">
						<span>Location</span> <strong><%=planHotel.getLocationInfo() == null
                                ? "-"
                                : planHotel.getLocationInfo()%></strong>
					</div>

				</div>

			</article>

			<%
			} else {
			%>

			<div class="section-heading">

				<h3>Recommended Hotel</h3>

			</div>

			<p class="muted-note">No overnight stay required for this trip.</p>

			<%
			}
			%>

			<div class="section-heading">

				<h3>Tourist Attractions</h3>

			</div>

			<%
			if (recommendedPlan.getAttractions() == null || recommendedPlan.getAttractions().isEmpty()) {
			%>

			<p class="muted-note">No attractions could be included within the
				remaining budget.</p>

			<%
			} else {
			%>

			<div class="attraction-card-grid">

				<%
				for (Attraction attraction : recommendedPlan.getAttractions()) {
				%>

				<article class="attraction-card">

					<div class="attraction-card-image">
						<%
						if (attraction.getImagePath() != null && !attraction.getImagePath().isBlank()) {
						%>
						<img
							src="${pageContext.request.contextPath}<%=attraction.getImagePath()%>"
							alt="<%=attraction.getAttractionName()%>" loading="lazy"
							onerror="this.style.display='none'">
						<%
						} else {
						%>
						<div class="attraction-image-placeholder">No Image</div>
						<%
						}
						%>
					</div>

					<div class="attraction-card-body">

						<h4><%=attraction.getAttractionName()%></h4>

						<p><%=attraction.getDescription() == null
                                ? ""
                                : attraction.getDescription()%></p>

						<span class="fee-tag"><%=attraction.getEntranceFee()%>
							MMK</span>

					</div>

				</article>

				<%
				}
				%>

			</div>

			<%
			}
			%>

			<div class="section-heading">

				<h3>Route Map</h3>

			</div>

			<div class="map-card">

				<div id="trip-map" class="trip-map"></div>

				<p class="map-note">
					<%=recommendedPlan.getRoute().getDistanceKm()%>
					KM &middot; approx.
					<%=recommendedPlan.getRoute().getTravelTimeHours()%>
					hours &middot; The drawn route is <strong>approximate</strong> and
					is not the actual road or railway path.
				</p>

			</div>

			<div class="plan-actions">

				<form action="${pageContext.request.contextPath}/trip" method="post">

					<input type="hidden" name="routeId"
						value="<%=recommendedPlan.getRoute().getRouteId()%>"> <input
						type="hidden" name="travelDate" value="<%=travelDateAttr%>"> <input
						type="hidden" name="returnDate"
						value="<%=returnDateAttr == null ? "" : returnDateAttr%>"> <input
						type="hidden" name="travellers"
						value="<%=travellersAttr == null ? "" : travellersAttr%>"> <input
						type="hidden" name="budget" value="<%=budgetAttr%>"> <input
						type="hidden" name="preference"
						value="<%=preferenceAttr == null ? "" : preferenceAttr%>">

					<%
					if (planHotel != null) {
					%>
					<input type="hidden" name="hotelId"
						value="<%=planHotel.getHotelId()%>">
					<%
					}
					%>

					<input type="hidden" name="foodTier"
						value="<%=recommendedPlan.getFoodEstimate() == null
                                ? ""
                                : recommendedPlan.getFoodEstimate().getTier()%>">

					<%
					for (Attraction planAttraction : recommendedPlan.getAttractions()) {
					%>
					<input type="hidden" name="attractionIds"
						value="<%=planAttraction.getAttractionId()%>">
					<%
					}
					%>

					<button type="submit" class="primary-button">Save Trip</button>

				</form>

				<%
				String modifyUrl = request.getContextPath() + "/dashboard#plan-trip" + "?startingCityId="
						+ recommendedPlan.getRoute().getStartingCity().getCityId() + "&destinationCityId="
						+ recommendedPlan.getRoute().getDestinationCity().getCityId() + "&travelDate="
						+ (travelDateAttr == null ? "" : URLEncoder.encode(String.valueOf(travelDateAttr), StandardCharsets.UTF_8))
						+ "&returnDate="
						+ (returnDateAttr == null ? ""
								: URLEncoder.encode(String.valueOf(returnDateAttr), StandardCharsets.UTF_8))
						+ "&travellers=" + (travellersAttr == null ? "" : travellersAttr) + "&budget=" + budgetAttr
						+ "&transportationId=" + recommendedPlan.getRoute().getTransportation().getTransportationId()
						+ "&preference=" + (preferenceAttr == null ? "" : preferenceAttr);
				%>

				<a href="<%=modifyUrl%>" class="secondary-button">Modify Plan</a>

				<a href="${pageContext.request.contextPath}/dashboard#plan-trip"
					class="secondary-button secondary-button-outline">Create Another
					Plan</a>

			</div>

		</section>

		<%
		if (budgetSuggestions != null && !budgetSuggestions.isEmpty()) {
		%>

		<section class="suggestion-section">

			<div class="section-heading">

				<h2>Over-Budget Advice</h2>

				<p>Your estimated cost exceeds your budget. Here are reliable
					ways to bring it down.</p>

			</div>

			<div class="suggestion-grid">

				<%
				for (BudgetSuggestion suggestion : budgetSuggestions) {

					String applyType = suggestion.getApplyType();

					String applyParamName = "transportationId";

					if ("hotel".equals(applyType)) {
						applyParamName = "hotelCategory";
					} else if ("duration".equals(applyType)) {
						applyParamName = "returnDate";
					} else if ("attractions".equals(applyType)) {
						applyParamName = "excludeAttractionIds";
					}
				%>

				<article class="suggestion-card">

					<h3><%=suggestion.getTitle()%></h3>

					<p class="suggestion-description"><%=suggestion.getDescription()%></p>

					<p class="suggestion-savings">
						Possible Savings: <strong><%=suggestion.getPotentialSavings()%>
							MMK</strong>
					</p>

					<form action="${pageContext.request.contextPath}/route"
						method="post">

						<input type="hidden" name="startingCityId"
							value="<%=recommendedPlan.getRoute().getStartingCity().getCityId()%>"> <input
							type="hidden" name="destinationCityId"
							value="<%=recommendedPlan.getRoute().getDestinationCity().getCityId()%>"> <input
							type="hidden" name="travelDate" value="<%=travelDateAttr%>">

						<%
						if (!"duration".equals(applyType)) {
						%>
						<input type="hidden" name="returnDate"
							value="<%=returnDateAttr == null ? "" : returnDateAttr%>">
						<%
						}
						%>

						<input type="hidden" name="travellers"
							value="<%=travellersAttr == null ? "" : travellersAttr%>"> <input
							type="hidden" name="budget" value="<%=budgetAttr%>"> <input
							type="hidden" name="preference"
							value="<%=preferenceAttr == null ? "" : preferenceAttr%>">

						<%
						if (!"transportation".equals(applyType)) {
						%>
						<input type="hidden" name="transportationId"
							value="<%=recommendedPlan.getRoute().getTransportation().getTransportationId()%>">
						<%
						}
						%>

						<input type="hidden" name="<%=applyParamName%>"
							value="<%=suggestion.getApplyValue()%>">

						<button type="submit" class="apply-button">Apply
							Suggestion</button>

					</form>

				</article>

				<%
				}
				%>

			</div>

		</section>

		<%
		}
		%>

		<%
		if (plans != null && plans.size() > 1) {
		%>

		<section class="alternative-section">

			<h2>Alternative Plans</h2>

			<div class="route-list">

				<%
				for (int index = 1; index < plans.size(); index++) {

					TripPlan alternativePlan = plans.get(index);
				%>

				<article class="alternative-route-card">

					<h3>
						<%=alternativePlan.getRoute().getTransportation().getTransportName()%>
					</h3>

					<p>
						Total Cost: <strong> <%=alternativePlan.getTotalEstimatedCost()%>
							MMK
						</strong>
					</p>

					<p>
						Time:
						<%=alternativePlan.getRoute().getTravelTimeHours()%>
						Hours
					</p>

					<p>
						Distance:
						<%=alternativePlan.getRoute().getDistanceKm()%>
						KM
					</p>

					<%
					if (alternativePlan.getHotel() != null) {
					%>
					<p>
						Hotel: <strong><%=alternativePlan.getHotel().getHotelName()%></strong>
					</p>
					<%
					}
					%>

					<p>
						Food: <strong><%=alternativePlan.getFoodEstimate() == null
                                ? "-"
                                : alternativePlan.getFoodEstimate().getTier()%></strong>
					</p>

					<form
						action="${pageContext.request.contextPath}/trip"
						method="post">

						<input type="hidden" name="routeId"
							value="<%=alternativePlan.getRoute().getRouteId()%>"> <input
							type="hidden" name="travelDate" value="<%=travelDateAttr%>"> <input
							type="hidden" name="returnDate"
							value="<%=returnDateAttr == null ? "" : returnDateAttr%>"> <input
							type="hidden" name="travellers"
							value="<%=travellersAttr == null ? "" : travellersAttr%>"> <input
							type="hidden" name="budget" value="<%=budgetAttr%>"> <input
							type="hidden" name="preference"
							value="<%=preferenceAttr == null ? "" : preferenceAttr%>">

						<%
						if (alternativePlan.getHotel() != null) {
						%>
						<input type="hidden" name="hotelId"
							value="<%=alternativePlan.getHotel().getHotelId()%>">
						<%
						}
						%>

						<input type="hidden" name="foodTier"
							value="<%=alternativePlan.getFoodEstimate() == null
                                    ? ""
                                    : alternativePlan.getFoodEstimate().getTier()%>">

						<%
						for (Attraction alternativeAttraction : alternativePlan.getAttractions()) {
						%>
						<input type="hidden" name="attractionIds"
							value="<%=alternativeAttraction.getAttractionId()%>">
						<%
						}
						%>

						<button type="submit" class="secondary-button">Select
							This Plan</button>

					</form>

				</article>

				<%
				}
				%>

			</div>

		</section>

		<%
		}
		%>

		<%
		}
		%>

	</main>

	<script>
		window.tripMapConfig = {
			startingCity: {
				name: "<%=recommendedPlan == null ? ""
						: recommendedPlan.getRoute().getStartingCity().getCityName()%>",
				lat: <%=recommendedPlan == null || recommendedPlan.getRoute().getStartingCity().getLatitude() == null
						? "null"
						: recommendedPlan.getRoute().getStartingCity().getLatitude()%>,
				lng: <%=recommendedPlan == null || recommendedPlan.getRoute().getStartingCity().getLongitude() == null
						? "null"
						: recommendedPlan.getRoute().getStartingCity().getLongitude()%>
			},
			destinationCity: {
				name: "<%=recommendedPlan == null ? ""
						: recommendedPlan.getRoute().getDestinationCity().getCityName()%>",
				lat: <%=recommendedPlan == null
						|| recommendedPlan.getRoute().getDestinationCity().getLatitude() == null
								? "null"
								: recommendedPlan.getRoute().getDestinationCity().getLatitude()%>,
				lng: <%=recommendedPlan == null
						|| recommendedPlan.getRoute().getDestinationCity().getLongitude() == null
								? "null"
								: recommendedPlan.getRoute().getDestinationCity().getLongitude()%>
			},
			attractions: [
				<%
				if (recommendedPlan != null) {
					for (int index = 0; index < recommendedPlan.getAttractions().size(); index++) {

						Attraction mapAttraction = recommendedPlan.getAttractions().get(index);

						if (index > 0) {
				%>,<%
				}
				%>
				{
					name: "<%=mapAttraction.getAttractionName()%>",
					lat: <%=mapAttraction.getLatitude() == null ? "null" : mapAttraction.getLatitude()%>,
					lng: <%=mapAttraction.getLongitude() == null ? "null" : mapAttraction.getLongitude()%>,
					fee: <%=mapAttraction.getEntranceFee() == null ? 0 : mapAttraction.getEntranceFee()%>
				}
				<%
				}
				}
				%>
			],
			distanceKm: <%=recommendedPlan == null ? 0 : recommendedPlan.getRoute().getDistanceKm()%>,
			travelTimeHours: <%=recommendedPlan == null ? 0 : recommendedPlan.getRoute().getTravelTimeHours()%>,
			approximateRouteLabel: true
		};
	</script>

	<script
		src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

	<script
		src="${pageContext.request.contextPath}/js/map.js"></script>

</body>
</html>
