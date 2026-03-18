<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@taglib prefix="cchc" uri="http://cchc/tags" %>
<%
    UserBean user = (UserBean) session.getAttribute("user");
    if (user == null) {
        user = (UserBean) session.getAttribute("loginUser");
        if (user != null) {
            session.setAttribute("user", user);
        }
    }
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String role = String.valueOf(session.getAttribute("loginRole"));
    if (!"ADMIN".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Dashboard</title>
    </head>
    <body>
        <h1>Admin Dashboard</h1>
        <p>Welcome, <cchc:username /></p>
        <p>Role: <%= role %></p>
        <p>Unread Notifications: <cchc:unreadCount /></p>
        <cchc:hasRole value="ADMIN">
            <p>Access granted for admin module.</p>
        </cchc:hasRole>
        <p>
            <a href="<%= request.getContextPath() %>/admin/users">User Management</a> |
            <a href="<%= request.getContextPath() %>/admin/reports">Reports</a> |
            <a href="<%= request.getContextPath() %>/admin/csv-import">CSV Import</a> |
            <a href="<%= request.getContextPath() %>/notifications">My Notifications</a>
        </p>
        <p><a href="<%= request.getContextPath() %>/logout">Logout</a></p>
    </body>
</html>
