<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.travelplanner.model.Route"%>

<%
    Route route =
            (Route) request.getAttribute("route");
%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Trip Summary | AI Travel Planner</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="dashboard-body">

	<header class="navbar">

		<div class="nav-brand">AI Travel Planner</div>

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

			<p>Review your selected route before saving the trip.</p>

		</section>

		<% if (route == null) { %>

		<section class="empty-result-card">

			<h2>Trip information is unavailable.</h2>

			<a href="${pageContext.request.contextPath}/dashboard"
				class="secondary-button"> Return to Dashboard </a>

		</section>

		<% } else { %>

		<section class="summary-card">

			<div class="summary-title">

				<h2>
					<%= route.getStartingCity().getCityName() %>
					→
					<%= route.getDestinationCity().getCityName() %>
				</h2>

				<span class="summary-badge"> Ready to Save </span>

			</div>

			<div class="route-information-grid">

				<div class="information-item">
					<span>Transportation</span> <strong> <%= route.getTransportation()
                                 .getTransportName() %>
					</strong>
				</div>

				<div class="information-item">
					<span>Travel Date</span> <strong> <%= request.getAttribute("travelDate") %>
					</strong>
				</div>

				<div class="information-item">
					<span>Estimated Cost</span> <strong> <%= route.getEstimatedCost() %>
						MMK
					</strong>
				</div>

				<div class="information-item">
					<span>Your Budget</span> <strong> <%= request.getAttribute("budget") %>
						MMK
					</strong>
				</div>

				<div class="information-item">
					<span>Travel Time</span> <strong> <%= route.getTravelTimeHours() %>
						Hours
					</strong>
				</div>

				<div class="information-item">
					<span>Distance</span> <strong> <%= route.getDistanceKm() %>
						KM
					</strong>
				</div>

				<div class="information-item">
					<span>Preference</span> <strong> <%= request.getAttribute("preference") %>
					</strong>
				</div>

			</div>

			<form action="${pageContext.request.contextPath}/save-trip"
				method="post">

				<input type="hidden" name="routeId"
					value="<%= route.getRouteId() %>"> <input type="hidden"
					name="travelDate" value="<%= request.getAttribute("travelDate") %>">

				<input type="hidden" name="budget"
					value="<%= request.getAttribute("budget") %>"> <input
					type="hidden" name="preference"
					value="<%= request.getAttribute("preference") %>">

				<button type="submit" class="primary-button">Save Trip</button>

			</form>

		</section>

		<% } %>

	</main>

</body>
</html>