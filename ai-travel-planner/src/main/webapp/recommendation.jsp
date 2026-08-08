<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.travelplanner.model.Route"%>
<%@ page import="com.travelplanner.model.TravelRecommendation"%>
<%@ page import="com.travelplanner.model.City"%>
<%
List<TravelRecommendation> recommendations = (List<TravelRecommendation>) request.getAttribute("recommendations");
Route recommendedRoute = (Route) request.getAttribute("recommendedRoute");
String travelStyle = (String) request.getAttribute("travelStyle");
String activity = (String) request.getAttribute("activity");
String weather = (String) request.getAttribute("weather");
String food = (String) request.getAttribute("food");
String relaxation = (String) request.getAttribute("relaxation");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>AI Recommendation | AI Travel Planner</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="dashboard-body recommendation-page">
<header class="navbar">
    <div class="nav-brand">✈ AI Travel Planner</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/dashboard">New Trip</a>
        <a href="${pageContext.request.contextPath}/trip-history">Trip History</a>
        <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
    </div>
</header>

<main class="dashboard-container">
    <section class="hero-section result-hero">
        <span class="eyebrow">✨ PERSONALIZED RESULT</span>
        <h1>AI Recommendation <span>For You</span></h1>
        <p>We compared your destination, budget, weather, food, activities and relaxation preferences.</p>
        <div class="selected-preferences">
            <span>💚 <%= travelStyle %></span>
            <span>🎯 <%= activity %></span>
            <span>☀ <%= weather %></span>
            <span>🍜 <%= food %></span>
            <span>🧘 <%= relaxation %></span>
        </div>
    </section>

    <% if (recommendations == null || recommendations.isEmpty()) { %>
        <section class="empty-result-card">
            <div class="empty-icon">🧭</div>
            <h2>No matching trip found</h2>
            <p>Try increasing your budget, selecting any transportation, or changing one of your preferences.</p>
            <a href="${pageContext.request.contextPath}/dashboard" class="secondary-button">Change Preferences</a>
        </section>
    <% } else { 
        TravelRecommendation best = recommendations.get(0);
        City city = best.getCity();
        Route route = best.getRoute();
    %>
        <section class="ai-result-layout">
            <div class="main-recommendation">
                <div class="recommendation-card">
                    <div class="recommendation-topline">
                        <span class="ai-pill">✨ AI Recommended For You</span>
                        <div class="match-score">
                            <small>Match Score</small>
                            <strong><%= Math.round(best.getScore()) %>%</strong>
                        </div>
                    </div>

                    <div class="recommendation-content">
                        <div class="city-visual">
                            <div class="city-image-placeholder"><span>📍</span></div>
                        </div>
                        <div class="city-main-info">
                            <h2><%= city.getCityName() %></h2>
                            <p class="city-meta">📍 <%= city.getRegion() %> &nbsp; • &nbsp; ⭐ <%= city.getTourismRating() %>/5</p>
                            <p class="recommendation-reason"><strong>Why we recommend this?</strong><br><%= best.getReason() %></p>
                            <div class="tag-list">
                                <% if (city.isHistorical()) { %><span>🏛 Historical</span><% } %>
                                <% if (city.isBeach()) { %><span>🏖 Beach</span><% } %>
                                <% if (city.isMountain()) { %><span>⛰ Mountain</span><% } %>
                                <span>☀ <%= city.getWeatherType() %></span>
                                <span>🍜 <%= city.getFoodTypes() %></span>
                                <span>🧘 <%= city.getRelaxationTypes() %></span>
                            </div>
                        </div>
                    </div>

                    <div class="detail-panels">
                        <div class="detail-panel">
                            <h3>Trip Highlights</h3>
                            <ul>
                                <li>Explore <%= city.getPopularAttraction() %></li>
                                <li>Best season: <%= city.getBestSeason() %></li>
                                <li>Recommended stay: <%= city.getRecommendedDays() %> days</li>
                                <li>Activities: <%= city.getActivities() %></li>
                            </ul>
                        </div>
                        <div class="detail-panel">
                            <h3>Route & Travel Info</h3>
                            <div class="info-line"><span>From</span><strong><%= route.getStartingCity().getCityName() %></strong></div>
                            <div class="info-line"><span>To</span><strong><%= route.getDestinationCity().getCityName() %></strong></div>
                            <div class="info-line"><span>Transport</span><strong><%= route.getTransportation().getTransportName() %></strong></div>
                            <div class="info-line"><span>Travel time</span><strong><%= route.getTravelTimeHours() %> hrs</strong></div>
                            <div class="info-line"><span>Estimated cost</span><strong><%= route.getEstimatedCost() %> MMK</strong></div>
                        </div>
                    </div>

                    <div class="result-actions">
                        <form action="${pageContext.request.contextPath}/trip" method="post">
                            <input type="hidden" name="routeId" value="<%= route.getRouteId() %>">
                            <input type="hidden" name="travelDate" value="<%= request.getAttribute("travelDate") %>">
                            <input type="hidden" name="budget" value="<%= request.getAttribute("budget") %>">
                            <input type="hidden" name="preference" value="<%= request.getAttribute("preference") %>">
                            <button class="primary-button small-button" type="submit">View Trip Details</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/save-trip" method="post">
                            <input type="hidden" name="routeId" value="<%= route.getRouteId() %>">
                            <input type="hidden" name="travelDate" value="<%= request.getAttribute("travelDate") %>">
                            <input type="hidden" name="budget" value="<%= request.getAttribute("budget") %>">
                            <input type="hidden" name="preference" value="<%= request.getAttribute("preference") %>">
                            <button class="secondary-button small-button" type="submit">♡ Save Trip</button>
                        </form>
                    </div>
                </div>
            </div>

            <aside class="alternatives-panel">
                <div class="aside-heading">
                    <h2>Other Recommendations</h2>
                    <span>Top matches</span>
                </div>
                <% int limit = Math.min(recommendations.size(), 4);
                   for (int i = 1; i < limit; i++) {
                       TravelRecommendation rec = recommendations.get(i);
                       City c = rec.getCity();
                       Route r = rec.getRoute();
                %>
                <article class="alternative-card">
                    <div class="alt-image"><span>📍</span></div>
                    <div class="alt-content">
                        <div class="alt-title-row">
                            <h3><%= c.getCityName() %></h3>
                            <span class="mini-score"><%= Math.round(rec.getScore()) %>%</span>
                        </div>
                        <p>📍 <%= c.getRegion() %></p>
                        <p class="stars">★★★★★ <small><%= c.getTourismRating() %></small></p>
                        <div class="alt-tags"><span>☀ <%= c.getWeatherType() %></span><span>🍜 <%= c.getFoodTypes() %></span></div>
                        <div class="alt-bottom"><strong><%= r.getEstimatedCost() %> MMK</strong><span><%= r.getTransportation().getTransportName() %></span></div>
                    </div>
                </article>
                <% } %>
                <div class="ai-tip-box">
                    <span class="tip-icon">💡</span>
                    <div><strong>AI Tip for You</strong><p>Higher scores mean the destination matches more of your selected preferences.</p></div>
                </div>
            </aside>
        </section>
    <% } %>
</main>
</body>
</html>
