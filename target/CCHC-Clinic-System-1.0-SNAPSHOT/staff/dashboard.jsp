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
    if (!"STAFF".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Staff Dashboard</title>
    </head>
    <body>
        <h1>Staff Dashboard</h1>
        <p>Welcome, <cchc:username /></p>
        <p>Role: <%= role %></p>
        <p>Unread Notifications: <cchc:unreadCount /></p>

        <!-- simple notification table -->
        <%
            com.cchc.dao.NotificationDAO notiDao = new com.cchc.dao.NotificationDAO();
            java.util.List<com.cchc.model.NotificationBean> recentList = null;
            try {
                recentList = notiDao.getRecentNotifications(user.getUserId());
            } catch (Exception e) {
                recentList = new java.util.ArrayList<>();
            }
        %>
        <h3>Recent Notifications</h3>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Type</th>
                <th>Message</th>
                <th>Time</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <% if (recentList != null && recentList.size() > 0) {
                for (int i = 0; i < recentList.size(); i++) {
                    com.cchc.model.NotificationBean rn = recentList.get(i);
            %>
            <tr>
                <td><%= rn.getNotificationType() %></td>
                <td><%= rn.getMessage() %></td>
                <td><%= rn.getCreatedAt() %></td>
                <td><%= rn.isRead() ? "Read" : "Unread" %></td>
                <td>
                    <% if (!rn.isRead()) { %>
                    <form method="post" action="<%= request.getContextPath() %>/notifications">
                        <input type="hidden" name="notificationId" value="<%= rn.getNotificationId() %>" />
                        <input type="submit" value="Mark Read" />
                    </form>
                    <% } else { %>
                    -
                    <% } %>
                </td>
            </tr>
            <%  }
               } else { %>
            <tr><td colspan="5">No notifications yet</td></tr>
            <% } %>
        </table>

        <cchc:hasRole value="STAFF">
            <p>Access granted for staff module.</p>
        </cchc:hasRole>
        <p>
            <a href="<%= request.getContextPath() %>/staff/attendance">Daily Appointments</a> |
            <a href="<%= request.getContextPath() %>/staff/queue">Queue Management</a> |
            <a href="<%= request.getContextPath() %>/staff/queue-progression">Manage Queue Progression</a> |
            <a href="<%= request.getContextPath() %>/staff/approval">Approve Bookings</a> |
            <a href="<%= request.getContextPath() %>/staff/report-issue">Report Operational Issue</a> |
            <a href="<%= request.getContextPath() %>/account/profile">My Account</a> |
            <a href="<%= request.getContextPath() %>/notifications">My Notifications</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
