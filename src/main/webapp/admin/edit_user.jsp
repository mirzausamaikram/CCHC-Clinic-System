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
        <title>Edit User</title>
    </head>
    <body>
        <h2>Edit User</h2>

        <% if (errorMsg != null) { %>
            <p style="color: red;"><b><%= errorMsg %></b></p>
        <% } %>

        <% if (user != null) { %>
            <form method="post" action="<%= request.getContextPath() %>/admin/edit-user">
                <input type="hidden" name="userId" value="<%= user.getUserId() %>" />
                
                <table border="0" cellpadding="5">
                    <tr>
                        <td><b>Username (read-only)</b></td>
                        <td><input type="text" value="<%= user.getUsername() %>" disabled /></td>
                    </tr>
                    <tr>
                        <td><b>Full Name</b></td>
                        <td><input type="text" name="fullName" value="<%= user.getFullName() != null ? user.getFullName() : "" %>" size="40" /></td>
                    </tr>
                    <tr>
                        <td><b>Email</b></td>
                        <td><input type="email" name="email" value="<%= user.getEmail() %>" required size="40" /></td>
                    </tr>
                    <tr>
                        <td><b>Phone</b></td>
                        <td><input type="text" name="phone" value="<%= user.getPhone() != null ? user.getPhone() : "" %>" size="40" /></td>
                    </tr>
                    <tr>
                        <td><b>Reset Password (optional)</b></td>
                        <td><input type="password" name="newPassword" size="40" /></td>
                    </tr>
                    <tr>
                        <td><b>Confirm New Password</b></td>
                        <td><input type="password" name="confirmPassword" size="40" /></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <input type="submit" value="Save Changes" />
                            <a href="<%= request.getContextPath() %>/admin/users">Cancel</a>
                        </td>
                    </tr>
                </table>
            </form>
        <% } else { %>
            <p>User not found.</p>
            <a href="<%= request.getContextPath() %>/admin/users">Back to User List</a>
        <% } %>
    </body>
</html>
