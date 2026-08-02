<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>Register</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="card"><h1>Register</h1>
<% if (request.getAttribute("errorMessage") != null) { %><p class="error"><%= request.getAttribute("errorMessage") %></p><% } %>
<form action="${pageContext.request.contextPath}/register" method="post">
<label>Full Name</label><input type="text" name="fullName" required>
<label>Email</label><input type="email" name="email" required>
<label>Password</label><input type="password" name="password" minlength="6" required>
<label>Confirm Password</label><input type="password" name="confirmPassword" minlength="6" required>
<button type="submit">Register</button></form>
<p><a href="login.jsp">Back to login</a></p></div></body></html>