<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Something Went Wrong | TravelMate AI</title>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/auth.css">
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
				<h1>Something went wrong</h1>
				<p>The page you requested could not be loaded. Please try again or return to the home page.</p>
			</div>
			<a href="<%= request.getContextPath() %>/" class="btn btn-primary btn-block">Back to Home</a>
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
