<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.travelplanner.model.User"%>
<%
	User loggedInUser = (User) session.getAttribute("loggedInUser");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>TravelMate AI - Plan Smarter, Travel Better</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/landing.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/responsive.css">
</head>
<body class="landing-body">

	<header class="site-header">
		<nav class="navbar container">
			<a href="<%= request.getContextPath() %>/" class="brand">
				<span class="brand-mark">TM</span>
				<span class="brand-name">TravelMate <span class="brand-ai">AI</span></span>
			</a>
			<ul class="nav-links">
				<li><a href="<%= request.getContextPath() %>/#how-it-works">How It Works</a></li>
				<li><a href="<%= request.getContextPath() %>/#features">Features</a></li>
				<li><a href="<%= request.getContextPath() %>/#destinations">Destinations</a></li>
			</ul>
			<div class="nav-actions">
				<%
					if (loggedInUser != null) {
				%>
				<a href="<%= request.getContextPath() %>/dashboard" class="btn btn-ghost">Dashboard</a>
				<a href="<%= request.getContextPath() %>/logout" class="btn btn-ghost">Logout</a>
				<%
					} else {
				%>
				<a href="<%= request.getContextPath() %>/login" class="btn btn-ghost">Login</a>
				<a href="<%= request.getContextPath() %>/register" class="btn btn-primary">Get Started</a>
				<%
					}
				%>
			</div>
		</nav>
	</header>

	<main>

		<section class="hero">
			<div class="hero-overlay"></div>
			<div class="container hero-content">
				<p class="hero-eyebrow">AI-Powered Travel Planning</p>
				<h1 class="hero-title">Plan Smarter.<br>Travel Better.</h1>
				<p class="hero-subtitle">
					Build a complete trip in minutes. Tell us your destinations, and TravelMate AI
					suggests optimized routes, real-time pricing, and a smart itinerary.
				</p>
				<div class="hero-actions">
					<%
						if (loggedInUser != null) {
					%>
					<a href="<%= request.getContextPath() %>/dashboard" class="btn btn-primary btn-lg">Start Planning</a>
					<a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-light btn-lg">Logout</a>
					<%
						} else {
					%>
					<a href="<%= request.getContextPath() %>/login" class="btn btn-primary btn-lg">Start Planning</a>
					<a href="<%= request.getContextPath() %>/register" class="btn btn-outline-light btn-lg">Create Account</a>
					<%
						}
					%>
				</div>
			</div>
		</section>

		<section id="how-it-works" class="section">
			<div class="container">
				<h2 class="section-title">How It Works</h2>
				<p class="section-subtitle">Four simple steps to your perfect trip.</p>
				<div class="steps-grid">
					<div class="step-card">
						<div class="step-icon">1</div>
						<h3>Create Account</h3>
						<p>Register for free and sign in to start building your travel plans.</p>
					</div>
					<div class="step-card">
						<div class="step-icon">2</div>
						<h3>Pick Your Cities</h3>
						<p>Add the destinations you want to visit with your travel dates and party size.</p>
					</div>
					<div class="step-card">
						<div class="step-icon">3</div>
						<h3>Get Smart Route</h3>
						<p>TravelMate AI generates an optimized route with transportation costs and times.</p>
					</div>
					<div class="step-card">
						<div class="step-icon">4</div>
						<h3>Save &amp; Review</h3>
						<p>Save your trip, view the full itinerary, and revisit your history anytime.</p>
					</div>
				</div>
			</div>
		</section>

		<section id="features" class="section section-alt">
			<div class="container">
				<h2 class="section-title">Smart Features</h2>
				<p class="section-subtitle">Everything you need to plan a memorable journey.</p>
				<div class="features-grid">
					<div class="feature-card">
						<div class="feature-icon">&#128506;</div>
						<h3>Optimized Routes</h3>
						<p>Cost-based pathfinding across cities picks the smartest order for your trip.</p>
					</div>
					<div class="feature-card">
						<div class="feature-icon">&#128176;</div>
						<h3>Live Cost Estimates</h3>
						<p>Real-time transportation pricing and fuel estimates keep your budget on track.</p>
					</div>
					<div class="feature-card">
						<div class="feature-icon">&#128197;</div>
						<h3>Date Flexibility</h3>
						<p>Set flexible dates to unlock cheaper options and better availability.</p>
					</div>
					<div class="feature-card">
						<div class="feature-icon">&#128681;</div>
						<h3>Group Planning</h3>
						<p>Plan for solo trips or family vacations with adjustable party sizes.</p>
					</div>
					<div class="feature-card">
						<div class="feature-icon">&#128274;</div>
						<h3>Save &amp; Sync</h3>
						<p>Store every itinerary in your account and pick up right where you left off.</p>
					</div>
					<div class="feature-card">
						<div class="feature-icon">&#128278;</div>
						<h3>One-Click Reports</h3>
						<p>Review your saved trips with clean summaries and total costs.</p>
					</div>
				</div>
			</div>
		</section>

		<section id="destinations" class="section">
			<div class="container">
				<h2 class="section-title">Popular Destinations</h2>
				<p class="section-subtitle">Hand-picked cities to start your next adventure.</p>
				<div class="destination-grid">

					<a href="<%= request.getContextPath() %>/login" class="destination-card">
						<div class="destination-image">
							<img src="<%= request.getContextPath() %>/images/destinations/santorini.svg" alt="Santorini" onerror="this.closest('.destination-image').classList.add('img-missing'); this.style.display='none';">
						</div>
						<div class="destination-overlay">
							<h3>Santorini, Greece</h3>
							<p>White-washed villages above the Aegean</p>
						</div>
					</a>

					<a href="<%= request.getContextPath() %>/login" class="destination-card">
						<div class="destination-image">
							<img src="<%= request.getContextPath() %>/images/destinations/kyoto.svg" alt="Kyoto" onerror="this.closest('.destination-image').classList.add('img-missing'); this.style.display='none';">
						</div>
						<div class="destination-overlay">
							<h3>Kyoto, Japan</h3>
							<p>Timeless temples and lantern-lit streets</p>
						</div>
					</a>

					<a href="<%= request.getContextPath() %>/login" class="destination-card">
						<div class="destination-image">
							<img src="<%= request.getContextPath() %>/images/destinations/paris.svg" alt="Paris" onerror="this.closest('.destination-image').classList.add('img-missing'); this.style.display='none';">
						</div>
						<div class="destination-overlay">
							<h3>Paris, France</h3>
							<p>The city of light and endless cafÃ©s</p>
						</div>
					</a>

					<a href="<%= request.getContextPath() %>/login" class="destination-card">
						<div class="destination-image">
							<img src="<%= request.getContextPath() %>/images/destinations/banff.svg" alt="Banff" onerror="this.closest('.destination-image').classList.add('img-missing'); this.style.display='none';">
						</div>
						<div class="destination-overlay">
							<h3>Banff, Canada</h3>
							<p>Turquoise lakes under mountain peaks</p>
						</div>
					</a>

					<a href="<%= request.getContextPath() %>/login" class="destination-card">
						<div class="destination-image">
							<img src="<%= request.getContextPath() %>/images/destinations/dubai.svg" alt="Dubai" onerror="this.closest('.destination-image').classList.add('img-missing'); this.style.display='none';">
						</div>
						<div class="destination-overlay">
							<h3>Dubai, UAE</h3>
							<p>Futuristic skyline meets desert dunes</p>
						</div>
					</a>

					<a href="<%= request.getContextPath() %>/login" class="destination-card">
						<div class="destination-image">
							<img src="<%= request.getContextPath() %>/images/destinations/capetown.svg" alt="Cape Town" onerror="this.closest('.destination-image').classList.add('img-missing'); this.style.display='none';">
						</div>
						<div class="destination-overlay">
							<h3>Cape Town, South Africa</h3>
							<p>Table Mountain and ocean-meets-city charm</p>
						</div>
					</a>

				</div>
			</div>
		</section>

	</main>

	<footer class="site-footer">
		<div class="container">
			<div class="footer-top">
				<div class="footer-brand">
					<a href="<%= request.getContextPath() %>/" class="brand">
						<span class="brand-mark">TM</span>
						<span class="brand-name">TravelMate <span class="brand-ai">AI</span></span>
					</a>
					<p>Your AI-powered companion for smarter, better trips.</p>
				</div>
				<div class="footer-links">
					<h4>Explore</h4>
					<a href="<%= request.getContextPath() %>/#how-it-works">How It Works</a>
					<a href="<%= request.getContextPath() %>/#features">Features</a>
					<a href="<%= request.getContextPath() %>/#destinations">Destinations</a>
				</div>
				<div class="footer-links">
					<h4>Account</h4>
					<a href="<%= request.getContextPath() %>/login">Login</a>
					<a href="<%= request.getContextPath() %>/register">Register</a>
				</div>
			</div>
			<div class="footer-bottom">
				<p>&copy; 2026 TravelMate AI. All rights reserved.</p>
				<p class="footer-disclaimer">Planning costs are estimates and may vary with live market rates.</p>
			</div>
		</div>
	</footer>

	<script src="<%= request.getContextPath() %>/js/script.js"></script>
</body>
</html>
