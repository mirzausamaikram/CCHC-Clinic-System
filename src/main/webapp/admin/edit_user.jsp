<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    // check admin
    UserBean admin = (UserBean) session.getAttribute("loginUser");
    if (admin == null) {
        admin = (UserBean) session.getAttribute("user");
    }
    if (admin == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    UserBean user = (UserBean) request.getAttribute("user");
    String errorMsg = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Edit User</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    </head>
    <body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
        <div class="page narrow">
            <div class="panel">

        <% if (errorMsg != null) { %>
            <div class="notice error"><%= errorMsg %></div>
        <% } %>

        <% if (user != null) { %>
            <form method="post" action="<%= request.getContextPath() %>/admin/edit-user">
                <input type="hidden" name="userId" value="<%= user.getUserId() %>" />

                <div class="form-grid">
                    <div class="field">
                        <label>Username (read-only)</label>
                        <input type="text" value="<%= user.getUsername() %>" disabled />
                    </div>
                    <div class="field">
                        <label for="fullName">Full Name</label>
                        <input id="fullName" type="text" name="fullName" value="<%= user.getFullName() != null ? user.getFullName() : "" %>" />
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input id="email" type="email" name="email" value="<%= user.getEmail() %>" required />
                    </div>
                    <div class="field">
                        <label for="phone">Phone</label>
                        <input id="phone" type="text" name="phone" value="<%= user.getPhone() != null ? user.getPhone() : "" %>" />
                    </div>
                    <div class="field">
                        <label for="newPassword">Reset Password (optional)</label>
                        <input id="newPassword" type="password" name="newPassword" />
                    </div>
                    <div class="field">
                        <label for="confirmPassword">Confirm New Password</label>
                        <input id="confirmPassword" type="password" name="confirmPassword" />
                    </div>
                </div>
                <div class="actions">
                    <input type="submit" value="Save Changes" />
                    <a class="btn secondary" href="<%= request.getContextPath() %>/admin/users">Cancel</a>
                </div>
            </form>
        <% } else { %>
            <p class="notice error">User not found.</p>
            <div class="actions">
                <a class="btn" href="<%= request.getContextPath() %>/admin/users">Back to User List</a>
            </div>
        <% } %>
            </div>
        </div>
    </body>
</html>

