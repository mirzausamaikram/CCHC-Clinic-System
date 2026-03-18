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
    if (!"PATIENT".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Patient Dashboard</title>
    </head>
    <body>
        <h1>Patient Dashboard</h1>
        <p>Welcome, <cchc:username /></p>
        <p>Role: <%= role %></p>
        <p>Unread Notifications: <cchc:unreadCount /></p>
        <cchc:hasRole value="PATIENT">
            <p>Access granted for patient module.</p>
        </cchc:hasRole>
        <p>
            <a href="<%= request.getContextPath() %>/patient/appointments?action=book">Book Appointment</a> |
            <a href="<%= request.getContextPath() %>/patient/appointments?action=list">My Appointments</a> |
            <a href="<%= request.getContextPath() %>/patient/my-bookings">My Bookings (Simple)</a> |
            <a href="<%= request.getContextPath() %>/notifications">My Notifications</a> |
            <a href="<%= request.getContextPath() %>/admin/reports">Reports</a>
        </p>
        <p><a href="<%= request.getContextPath() %>/logout">Logout</a></p>
    </body>
</html>
