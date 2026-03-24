<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.cchc.model.NotificationBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%@taglib prefix="cchc" uri="http://cchc/tags" %>
<%
    // simple check
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
    List<NotificationBean> list = (List<NotificationBean>) request.getAttribute("notifications");
    if (list == null) {
        try {
            com.cchc.dao.NotificationDAO dao = new com.cchc.dao.NotificationDAO();
            list = dao.findByUserId(user.getUserId());
        } catch (Exception e) {
            list = new java.util.ArrayList<NotificationBean>();
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Notifications</title>
    </head>
    <body>
        <h1>My Notifications</h1>
        <p>Welcome, <cchc:username /></p>

        <% String msg = (String) request.getAttribute("errorMessage"); %>
        <% if (msg != null) { %>
            <p><%= msg %></p>
        <% } %>

        <table border="1" cellpadding="6" cellspacing="0">
            <tr>
                <th>ID</th>
                <th>Type</th>
                <th>Title</th>
                <th>Message</th>
                <th>Time</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <% if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) { 
                    NotificationBean notification = list.get(i); %>
                    <tr>
                        <td><%= notification.getNotificationId() %></td>
                        <td><%= notification.getNotificationType() %></td>
                        <td><%= notification.getTitle() %></td>
                        <td><%= notification.getMessage() %></td>
                        <td><%= notification.getCreatedAt() %></td>
                        <td><%= notification.isRead() ? "Read" : "Unread" %></td>
                        <td>
                            <% if (!notification.isRead()) { %>
                                <form method="post" action="<%= request.getContextPath() %>/notifications">
                                    <input type="hidden" name="notificationId" value="<%= notification.getNotificationId() %>" />
                                    <button type="submit">Mark Read</button>
                                </form>
                            <% } else { %>
                                -
                            <% } %>
                        </td>
                    </tr>
            <%  }
            } else { %>
                <tr>
                    <td colspan="7">No notifications available.</td>
                </tr>
            <% } %>
        </table>

        <p>
            <% if ("PATIENT".equals(role)) { %>
                <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a>
            <% } else if ("STAFF".equals(role)) { %>
                <a href="<%= request.getContextPath() %>/staff/dashboard.jsp">Back Dashboard</a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back Dashboard</a>
            <% } %>
            |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
