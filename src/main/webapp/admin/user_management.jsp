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
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin User Management</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
    <div class="page">
        <div class="panel">
            <% if (msg != null) { %>
                <div class="notice success"><%= msg %></div>
            <% } %>

            <table>
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
                <form class="inline" method="post" action="<%= request.getContextPath() %>/admin/users">
                    <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                    <select name="active">
                        <option value="1" <%= u.isActive() ? "selected" : "" %>>Active</option>
                        <option value="0" <%= !u.isActive() ? "selected" : "" %>>Inactive</option>
                    </select>
                    <input type="submit" value="Save" class="btn">
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
        </div>

    </div>
</body>
</html>

