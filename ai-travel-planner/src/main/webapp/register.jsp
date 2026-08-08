<%@ page contentType="text/html; charset=UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Register | AI Travel Planner</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
</head>

<body class="auth-body">

	<div class="auth-container">

		<div class="auth-card">

			<div class="brand">
				<h1>AI Travel Planner</h1>
				<p>Create your travel planning account.</p>
			</div>

			<h2>Create Account</h2>

			<% if (request.getAttribute("errorMessage") != null) { %>

			<div class="alert error-alert">
				<%= request.getAttribute("errorMessage") %>
			</div>

			<% } %>

			<form action="${pageContext.request.contextPath}/register"
				method="post" class="auth-form"
				onsubmit="return validateRegistrationForm();">

				<div class="form-group">

					<label for="fullName">Full Name</label> <input type="text"
						id="fullName" name="fullName" placeholder="Enter your full name"
						required>

				</div>

				<div class="form-group">

					<label for="email">Email Address</label> <input type="email"
						id="email" name="email" placeholder="example@email.com" required>

				</div>

				<div class="form-group">

					<label for="password">Password</label> <input type="password"
						id="password" name="password" minlength="6"
						placeholder="Minimum 6 characters" required>

				</div>

				<div class="form-group">

					<label for="confirmPassword"> Confirm Password </label> <input
						type="password" id="confirmPassword" name="confirmPassword"
						minlength="6" placeholder="Enter password again" required>

				</div>

				<p id="validationMessage" class="validation-message"></p>

				<button type="submit" class="primary-button">Register</button>

			</form>

			<p class="auth-footer">

				Already have an account? <a
					href="${pageContext.request.contextPath}/login.jsp"> Login </a>

			</p>

		</div>

	</div>

	<script src="${pageContext.request.contextPath}/js/script.js"></script>

</body>
</html>