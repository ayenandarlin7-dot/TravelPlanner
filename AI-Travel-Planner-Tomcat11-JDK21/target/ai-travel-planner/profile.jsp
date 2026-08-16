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
<title>Profile | TravelMate AI</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/dashboard.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/responsive.css">
</head>
<body class="dashboard-body app-shell">

	<aside class="sidebar">
		<div class="sidebar-brand">
			<span class="brand-mark">TM</span>
			<span class="brand-name">TravelMate <span class="brand-ai">AI</span></span>
		</div>
		<nav class="sidebar-nav">
			<a href="<%= request.getContextPath() %>/dashboard" class="sidebar-link">Dashboard</a>
			<a href="<%= request.getContextPath() %>/dashboard#plan-trip" class="sidebar-link">Plan New Trip</a>
			<a href="<%= request.getContextPath() %>/trip-history" class="sidebar-link">My Trips</a>
			<a href="<%= request.getContextPath() %>/trip-history" class="sidebar-link">Trip History</a>
			<a href="<%= request.getContextPath() %>/profile" class="sidebar-link active">Profile</a>
			<a href="<%= request.getContextPath() %>/logout" class="sidebar-link sidebar-logout">Logout</a>
		</nav>
	</aside>

	<main class="app-main">

		<header class="app-topbar">
			<h1>Profile</h1>
			<span class="topbar-user">Welcome, <%= loggedInUser == null ? "User" : loggedInUser.getFullName() %></span>
		</header>

		<section class="content-section">
			<div class="profile-card">
				<div class="profile-avatar">
					<%= loggedInUser == null || loggedInUser.getFullName() == null || loggedInUser.getFullName().isBlank()
							? "U"
							: loggedInUser.getFullName().substring(0, 1).toUpperCase() %>
				</div>
				<div class="profile-details">
					<h2><%= loggedInUser == null ? "User" : loggedInUser.getFullName() %></h2>
					<p class="profile-meta"><%= loggedInUser == null ? "" : loggedInUser.getEmail() %></p>
					<p class="profile-meta">Member of TravelMate AI</p>
				</div>
			</div>
		</section>

	</main>

</body>
</html>
