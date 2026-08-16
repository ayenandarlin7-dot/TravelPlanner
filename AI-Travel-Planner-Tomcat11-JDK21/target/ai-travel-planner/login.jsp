<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.travelplanner.model.User"%>
<%@ page import="com.travelplanner.util.EscapeUtil"%>
<%
	String errorMessage = (String) request.getAttribute("errorMessage");
	String successMessage = "success".equals(request.getParameter("register")) ? "Registration successful! Please login to continue." : null;
	User loggedInUser = (User) session.getAttribute("loggedInUser");
	String savedEmail = "";
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
		for (Cookie cookie : cookies) {
			if ("travelmate_email".equals(cookie.getName())) {
				savedEmail = cookie.getValue();
			}
		}
	}
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Login - TravelMate AI</title>
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
				<h1>Welcome Back</h1>
				<p>Sign in to continue planning your next trip.</p>
			</div>

				<% if (errorMessage != null) { %>
					<div class="auth-error"><%= EscapeUtil.escapeHtml(errorMessage) %></div>
				<% } %>
				<% if (successMessage != null) { %>
					<div class="auth-success"><%= EscapeUtil.escapeHtml(successMessage) %></div>
				<% } %>

				<form action="<%= request.getContextPath() %>/login" method="post" class="auth-form" autocomplete="on">
					<div class="form-group">
						<label for="email">Email Address</label>
						<input type="email" id="email" name="email" placeholder="you@example.com"
							value="<%= EscapeUtil.escapeHtml(savedEmail) %>" required>
					</div>
				<div class="form-group">
					<label for="password">Password</label>
					<input type="password" id="password" name="password" placeholder="Enter your password" required>
				</div>
				<div class="form-row">
					<label class="checkbox-label">
						<input type="checkbox" name="rememberMe" value="yes"> Remember Me
					</label>
					<a href="<%= request.getContextPath() %>/register" class="auth-link">Create account</a>
				</div>
				<button type="submit" class="btn btn-primary btn-block">Login</button>
			</form>

			<p class="auth-footer">New to TravelMate AI? <a href="<%= request.getContextPath() %>/register">Register free</a></p>
		</div>
	</main>

	<footer class="site-footer">
		<div class="container">
			<div class="footer-bottom">
				<p>&copy; 2026 TravelMate AI. All rights reserved.</p>
			</div>
		</div>
	</footer>

</body>
</html>
