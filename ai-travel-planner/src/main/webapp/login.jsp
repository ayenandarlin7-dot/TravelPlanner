<%@ page contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Login | AI Travel Planner</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="auth-body">

	<div class="auth-container">

		<div class="auth-card">

			<div class="brand">
				<h1>AI Travel Planner</h1>
				<p>Plan smarter. Travel better.</p>
			</div>

			<h2>Welcome Back</h2>

			<% if (request.getAttribute("errorMessage") != null) { %>

			<div class="alert error-alert">
				<%= request.getAttribute("errorMessage") %>
			</div>

			<% } %>

			<% if ("success".equals(request.getParameter("register"))) { %>

			<div class="alert success-alert">Registration successful.
				Please login.</div>

			<% } %>

			<form action="${pageContext.request.contextPath}/login" method="post"
				class="auth-form">

				<div class="form-group">

					<label for="email">Email Address</label> <input type="email"
						id="email" name="email" placeholder="example@email.com" required>

				</div>

				<div class="form-group">

					<label for="password">Password</label> <input type="password"
						id="password" name="password" placeholder="Enter your password"
						required>

				</div>

				<button type="submit" class="primary-button">Login</button>

			</form>

			<p class="auth-footer">

				Do not have an account? <a
					href="${pageContext.request.contextPath}/register"> Create
					Account </a>

			</p>

		</div>

	</div>

</body>
</html>