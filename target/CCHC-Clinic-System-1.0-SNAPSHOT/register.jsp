<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Register</title>
</head>
<body>
    <h2>Register</h2>
    <% if (request.getAttribute("msg") != null) { %>
        <p style="color:red;"><%= request.getAttribute("msg") %></p>
    <% } %>
    <form method="post" action="<%= request.getContextPath() %>/register">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr><td>Username</td><td><input type="text" name="username" /></td></tr>
            <tr><td>Email</td><td><input type="email" name="email" /></td></tr>
            <tr><td>Password</td><td><input type="password" name="password" /></td></tr>
            <tr><td>Full Name</td><td><input type="text" name="fullName" /></td></tr>
            <tr><td>Phone</td><td><input type="text" name="phone" /></td></tr>
            <tr><td colspan="2"><input type="submit" value="Register" /></td></tr>
        </table>
    </form>
    <p><a href="<%= request.getContextPath() %>/login.jsp">Back to Login</a></p>
</body>
</html>
