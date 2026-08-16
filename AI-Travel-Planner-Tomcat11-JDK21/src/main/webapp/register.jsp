<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.travelplanner.util.EscapeUtil"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register - TravelMate AI</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/auth.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/responsive.css">
</head>
<body class="auth-body">

	<header class="site-header">
		<nav class="navbar container">
			<a href="<%= request.getContextPath() %>/" class="brand">
				<span class="brand-mark">TM</span>
				<span class="brand-name">TravelMate <span class="brand-ai">AI</span></span>
			</a>
			<div class="nav-actions">
				<a href="<%= request.getContextPath() %>/" class="btn btn-ghost">Back to Home</a>
			</div>
		</nav>
	</header>

	<main class="auth-main">
		<div class="auth-card">
			<div class="auth-card-header">
				<span class="auth-icon">&#128640;</span>
				<h1>Create Your Account</h1>
				<p>Join TravelMate AI and plan smarter trips.</p>
			</div>

			<%
				String errorMessage = (String) request.getAttribute("errorMessage");
				String successMessage = (String) request.getAttribute("successMessage");
				if (errorMessage != null) {
			%>
				<div class="auth-error"><%= EscapeUtil.escapeHtml(errorMessage) %></div>
			<%
				}
				if (successMessage != null) {
			%>
				<div class="auth-success"><%= successMessage %></div>
			<%
				}
			%>

			<form action="<%= request.getContextPath() %>/register" method="post" class="auth-form" autocomplete="on"
				onsubmit="return validateRegistrationForm();">
				<div class="form-group">
					<label for="name">Full Name</label>
					<input type="text" id="name" name="fullName" placeholder="Your full name" required>
				</div>
				<div class="form-group">
					<label for="email">Email Address</label>
					<input type="email" id="email" name="email" placeholder="you@example.com" required>
				</div>
				<div class="form-group">
					<label for="password">Password</label>
					<input type="password" id="password" name="password" placeholder="Create a password" required>
				</div>
				<div class="form-group">
					<label for="confirmPassword">Confirm Password</label>
					<input type="password" id="confirmPassword" name="confirmPassword" placeholder="Repeat your password" required>
				</div>
				<p id="validationMessage" class="validation-message" role="alert"></p>
				<button type="submit" class="btn btn-primary btn-block">Create Account</button>
			</form>

			<p class="auth-footer">Already have an account? <a href="<%= request.getContextPath() %>/login">Login</a></p>
		</div>
	</main>

	<footer class="site-footer">
		<div class="container">
			<div class="footer-bottom">
				<p>&copy; 2026 TravelMate AI. All rights reserved.</p>
			</div>
		</div>
	</footer>

	<script src="<%= request.getContextPath() %>/js/script.js"></script>

</body>
</html>
