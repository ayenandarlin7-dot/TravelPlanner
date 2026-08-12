<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="com.travelplanner.model.City"%>
<%@ page import="com.travelplanner.model.User"%>
<%
List<City> cities = (List<City>) request.getAttribute("cities");
User loggedInUser = (User) session.getAttribute("loggedInUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>AI Travel Planner</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="dashboard-body">
<header class="navbar">
    <div class="nav-brand">✈ AI Travel Planner</div>
    <div class="nav-links">
        <span>Welcome, <strong><%= loggedInUser == null ? "Traveler" : loggedInUser.getFullName() %></strong></span>
        <a href="${pageContext.request.contextPath}/trip-history">Trip History</a>
        <a href="${pageContext.request.contextPath}/logout" class="logout-link">Logout</a>
    </div>
</header>

<main class="dashboard-container planner-page">
    <section class="hero-section planner-hero">
        <span class="eyebrow">✨ PERSONALIZED TRAVEL PLANNER</span>
        <h1>Tell us what you love.<br><span>We'll find your perfect trip.</span></h1>
        <p>Choose a destination and share your travel preferences. Our recommendation engine compares your choices with destination data.</p>
    </section>

    <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="alert error-alert"><%= request.getAttribute("errorMessage") %></div>
    <% } %>

    <section class="planner-card ai-form-card">
        <div class="section-heading">
            <div><span class="step-badge">01</span><h2>Your trip</h2></div>
            <span class="hint">Basic trip information</span>
        </div>

        <form action="${pageContext.request.contextPath}/route" method="post" id="tripForm">
            <div class="form-grid three-cols">
                <div class="form-group">
                    <label for="startingCityId">Starting City</label>
                    <select id="startingCityId" name="startingCityId" required>
                        <option value="">Select starting city</option>
                        <% if (cities != null) for (City city : cities) { %>
                            <option value="<%= city.getCityId() %>"><%= city.getCityName() %></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="destinationCityId">Where do you want to go?</label>
                    <select id="destinationCityId" name="destinationCityId" required>
                        <option value="">Select destination</option>
                        <% if (cities != null) for (City city : cities) { %>
                            <option value="<%= city.getCityId() %>"><%= city.getCityName() %></option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label for="travelDate">Travel Date</label>
                    <input type="date" id="travelDate" name="travelDate" required>
                </div>
                <div class="form-group">
                    <label for="budget">Budget (MMK)</label>
                    <input type="number" id="budget" name="budget" min="0" step="1000" placeholder="Example: 50000" required>
                </div>
                <div class="form-group">
                    <label for="transportationId">Transportation</label>
                    <select id="transportationId" name="transportationId">
                        <option value="">Any Transportation</option>
                        <option value="1">🚌 Bus</option>
                        <option value="2">🚆 Train</option>
                        <option value="3">✈ Flight</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="tripDays">Trip Duration</label>
                    <select id="tripDays" name="tripDays">
                        <option value="1-2">1 - 2 Days</option>
                        <option value="3-5" selected>3 - 5 Days</option>
                        <option value="6-7">6 - 7 Days</option>
                        <option value="8+">8+ Days</option>
                    </select>
                </div>
            </div>

            <div class="preference-section">
                <div class="section-heading preference-heading">
                    <div><span class="step-badge">02</span><h2>What do you like?</h2></div>
                    <span class="hint">These choices control your match score</span>
                </div>

                <div class="preference-grid">
                    <div class="preference-box">
                        <label>Travel Style</label>
                        <div class="choice-row">
                            <label class="choice"><input type="radio" name="travelStyle" value="relaxing" checked><span>🌴 Relaxing</span></label>
                            <label class="choice"><input type="radio" name="travelStyle" value="adventure"><span>🥾 Adventure</span></label>
                            <label class="choice"><input type="radio" name="travelStyle" value="cultural"><span>🏛 Cultural</span></label>
                            <label class="choice"><input type="radio" name="travelStyle" value="nature"><span>🌿 Nature</span></label>
                            <label class="choice"><input type="radio" name="travelStyle" value="family"><span>👨‍👩‍👧 Family</span></label>
                        </div>
                    </div>

                    <div class="preference-box">
                        <label>Activity You Enjoy</label>
                        <div class="choice-row">
                            <label class="choice"><input type="radio" name="activity" value="sightseeing"><span>📷 Sightseeing</span></label>
                            <label class="choice"><input type="radio" name="activity" value="adventure"><span>⛰ Adventure</span></label>
                            <label class="choice"><input type="radio" name="activity" value="beach"><span>🏖 Beach</span></label>
                            <label class="choice"><input type="radio" name="activity" value="culture" checked><span>🏛 Culture</span></label>
                            <label class="choice"><input type="radio" name="activity" value="nature"><span>🌱 Nature</span></label>
                            <label class="choice"><input type="radio" name="activity" value="shopping"><span>🛍 Shopping</span></label>
                        </div>
                    </div>

                    <div class="preference-box">
                        <label>Preferred Weather</label>
                        <div class="choice-row compact">
                            <label class="choice"><input type="radio" name="weather" value="cool"><span>❄ Cool</span></label>
                            <label class="choice"><input type="radio" name="weather" value="warm" checked><span>☀ Warm</span></label>
                            <label class="choice"><input type="radio" name="weather" value="rainy"><span>🌧 Rainy</span></label>
                            <label class="choice"><input type="radio" name="weather" value="any"><span>🌈 Any</span></label>
                        </div>
                    </div>

                    <div class="preference-box">
                        <label>Food You Like</label>
                        <div class="choice-row compact">
                            <label class="choice"><input type="radio" name="food" value="local" checked><span>🍜 Local Food</span></label>
                            <label class="choice"><input type="radio" name="food" value="seafood"><span>🦐 Seafood</span></label>
                            <label class="choice"><input type="radio" name="food" value="vegetarian"><span>🥗 Vegetarian</span></label>
                            <label class="choice"><input type="radio" name="food" value="spicy"><span>🌶 Spicy</span></label>
                            <label class="choice"><input type="radio" name="food" value="any"><span>Any</span></label>
                        </div>
                    </div>

                    <div class="preference-box">
                        <label>How do you like to relax?</label>
                        <div class="choice-row compact">
                            <label class="choice"><input type="radio" name="relaxation" value="relaxing" checked><span>🧘 Chill</span></label>
                            <label class="choice"><input type="radio" name="relaxation" value="shopping"><span>🛍 Shopping</span></label>
                            <label class="choice"><input type="radio" name="relaxation" value="nightlife"><span>🌙 Nightlife</span></label>
                            <label class="choice"><input type="radio" name="relaxation" value="photography"><span>📸 Photography</span></label>
                            <label class="choice"><input type="radio" name="relaxation" value="spa"><span>💆 Spa</span></label>
                        </div>
                    </div>
                </div>
            </div>

            <p id="tripValidationMessage" class="validation-message"></p>
            <button type="submit" class="primary-button ai-submit">✨ Find My Perfect Trip</button>
        </form>
    </section>
</main>
<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
