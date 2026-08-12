<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.travelplanner.model.Route"%>

<%
Route recommendedRoute = (Route) request.getAttribute("recommendedRoute");

List<Route> rankedRoutes = (List<Route>) request.getAttribute("rankedRoutes");
%>

<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="UTF-8">

<meta name="viewport"
	content="width=device-width,
                   initial-scale=1.0">

<title>Route Recommendation | AI Travel Planner</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">

</head>

<body class="dashboard-body">

	<header class="navbar">

		<div class="nav-brand">AI Travel Planner</div>

		<div class="nav-links">

			<a href="${pageContext.request.contextPath}/dashboard"> New Trip
			</a> <a href="${pageContext.request.contextPath}/trip-history"> Trip
				History </a> <a href="${pageContext.request.contextPath}/logout"
				class="logout-link"> Logout </a>

		</div>

	</header>

	<main class="dashboard-container">

		<section class="hero-section">

			<h1>Route Recommendation</h1>

			<p>Routes are ranked using cost, travel time, distance and your
				selected preference.</p>

		</section>

		<%
		if (recommendedRoute == null) {
		%>

		<section class="empty-result-card">

			<h2>No Affordable Route Found</h2>

			<p>There is no available route matching your budget and
				transportation choice.</p>

			<a href="${pageContext.request.contextPath}/dashboard"
				class="secondary-button"> Change Trip Information </a>

		</section>

		<%
		} else {
		%>

		<section class="best-route-card">

			<div class="recommended-label">Best Recommended Route</div>

			<h2>
				<%=recommendedRoute.getStartingCity().getCityName()%>

				→

				<%=recommendedRoute.getDestinationCity().getCityName()%>
			</h2>

			<div class="route-information-grid">

				<div class="information-item">

					<span>Transportation</span> <strong> <%=recommendedRoute.getTransportation().getTransportName()%>
					</strong>

				</div>

				<div class="information-item">

					<span>Estimated Cost</span> <strong> <%=recommendedRoute.getEstimatedCost()%>
						MMK
					</strong>

				</div>

				<div class="information-item">

					<span>Travel Time</span> <strong> <%=recommendedRoute.getTravelTimeHours()%>
						Hours
					</strong>

				</div>

				<div class="information-item">

					<span>Distance</span> <strong> <%=recommendedRoute.getDistanceKm()%>
						KM
					</strong>

				</div>

			</div>

			<form action="${pageContext.request.contextPath}/trip" method="post">

				<input type="hidden" name="routeId"
					value="<%=recommendedRoute.getRouteId()%>"> <input
					type="hidden" name="travelDate"
					value="<%=request.getAttribute("travelDate")%>"> <input
					type="hidden" name="budget"
					value="<%=request.getAttribute("budget")%>"> <input
					type="hidden" name="preference"
					value="<%=request.getAttribute("preference")%>">

				<button type="submit" class="primary-button">Continue to
					Trip Summary</button>

			</form>

		</section>

		<%
		if (rankedRoutes != null && rankedRoutes.size() > 1) {
		%>

		<section class="alternative-section">

			<h2>Alternative Routes</h2>

			<div class="route-list">

				<%
				for (int index = 1; index < rankedRoutes.size(); index++) {

					Route route = rankedRoutes.get(index);
				%>

				<article class="alternative-route-card">

					<h3>
						<%=route.getTransportation().getTransportName()%>
					</h3>

					<p>
						Cost: <strong> <%=route.getEstimatedCost()%> MMK
						</strong>
					</p>

					<p>
						Time:
						<%=route.getTravelTimeHours()%>
						Hours
					</p>

					<p>
						Distance:
						<%=route.getDistanceKm()%>
						KM
					</p>

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

</body>
</html>