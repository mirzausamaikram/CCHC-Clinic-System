<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.cchc.model.NotificationBean"%>
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
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>My Notifications</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    </head>
    <body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
        <div class="page">
            <div class="panel">
                <p>Welcome, <strong><cchc:username /></strong></p>

        <% String msg = (String) request.getAttribute("errorMessage"); %>
        <% if (msg != null) { %>
            <div class="notice error"><%= msg %></div>
        <% } %>

                <table>
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
                                <form class="inline" method="post" action="<%= request.getContextPath() %>/notifications">
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
            </div>

        </div>
    </body>
</html>

