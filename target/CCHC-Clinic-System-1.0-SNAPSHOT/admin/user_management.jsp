<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    UserBean user = (UserBean) session.getAttribute("user");
    if (user == null) {
        user = (UserBean) session.getAttribute("loginUser");
        if (user != null) {
            session.setAttribute("user", user);
        }
    }
    if (user == null || !"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<UserBean> userList = (List<UserBean>) request.getAttribute("userList");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin User Management</title>
</head>
<body>
    <h2>Admin User Management</h2>

    <% if (msg != null) { %>
        <p><%= msg %></p>
    <% } %>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>ID</th>
            <th>Role ID</th>
            <th>Username</th>
            <th>Email</th>
            <th>Active</th>
            <th>Action</th>
        </tr>

        <% if (userList != null && userList.size() > 0) {
            for (int i = 0; i < userList.size(); i++) {
                UserBean u = userList.get(i);
        %>
        <tr>
            <td><%= u.getUserId() %></td>
            <td><%= u.getRoleId() %></td>
            <td><%= u.getUsername() %></td>
            <td><%= u.getEmail() %></td>
            <td><%= u.isActive() ? "YES" : "NO" %></td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/admin/users">
                    <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                    <select name="active">
                        <option value="1" <%= u.isActive() ? "selected" : "" %>>Active</option>
                        <option value="0" <%= !u.isActive() ? "selected" : "" %>>Inactive</option>
                    </select>
                    <input type="submit" value="Save">
                </form>
            </td>
        </tr>
        <%      }
            } else { %>
        <tr>
            <td colspan="6">No users found</td>
        </tr>
        <% } %>
    </table>

    <p>
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back Dashboard</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
