<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html><html><head><meta charset="UTF-8"><title>Login</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body><div class="card"><h1>AI Travel Planner</h1><h2>Login</h2>
<% if (request.getAttribute("errorMessage") != null) { %><p class="error"><%= request.getAttribute("errorMessage") %></p><% } %>
<form action="${pageContext.request.contextPath}/login" method="post">
<label>Email</label><input type="email" name="email" required>
<label>Password</label><input type="password" name="password" required>
<button type="submit">Login</button></form>
<p><a href="register.jsp">Create account</a></p></div></body></html>