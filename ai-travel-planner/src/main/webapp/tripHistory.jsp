<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.travelplanner.model.Trip"%>

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

	<header class="navbar">

		<div class="nav-brand">AI Travel Planner</div>

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

					<span class="transport-badge"> <%= trip.getRoute()
                                    .getTransportation()
                                    .getTransportName() %>

					</span>

				</div>

				<div class="history-details">

					<p>
						<span>Travel Date</span> <strong> <%= trip.getTravelDate() %>
						</strong>
					</p>

					<p>
						<span>Preference</span> <strong> <%= trip.getPreference() %>
						</strong>
					</p>

					<p>
						<span>Estimated Cost</span> <strong> <%= trip.getRecommendedCost() %>
							MMK
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

				</div>

				<form action="${pageContext.request.contextPath}/delete-trip"
					method="post" onsubmit="return confirm('Delete this trip?');">

					<input type="hidden" name="tripId" value="<%= trip.getTripId() %>">

					<button type="submit" class="delete-button">Delete Trip</button>

				</form>

			</article>

			<% } %>

		</section>

		<% } %>

	</main>

</body>
</html>