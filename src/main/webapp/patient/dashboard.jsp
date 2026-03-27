<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@taglib prefix="cchc" uri="http://cchc/tags" %>
<%
    // simple check - use loginUser first (same order as unreadCount tag)
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        user = (UserBean) session.getAttribute("user");
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
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Patient Dashboard</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    </head>
    <body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
        <div class="page">
            <div class="panel">
                <h2>Patient Dashboard</h2>
                <div class="grid">
                    <div class="stat">
                        <p class="muted">Role</p>
                        <p class="value"><%= role %></p>
                    </div>
                    <div class="stat">
                        <p class="muted">Unread Notifications</p>
                        <p class="value"><cchc:unreadCount /></p>
                    </div>
                </div>
            </div>

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
            <div class="panel">
                <h3>Recent Notifications</h3>
                <table>
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
                    <form class="inline" method="post" action="<%= request.getContextPath() %>/notifications">
                        <input type="hidden" name="notificationId" value="<%= rn.getNotificationId() %>" />
                        <input type="submit" value="Mark Read" class="btn" />
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
            </div>

        </div>
    </body>
</html>

