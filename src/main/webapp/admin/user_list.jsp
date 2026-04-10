<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<UserBean> list = (List<UserBean>) request.getAttribute("list");
    Map<Integer, String> roleNameMap = (Map<Integer, String>) request.getAttribute("roleNameMap");
    Map<Integer, Integer> staffClinicMap = (Map<Integer, Integer>) request.getAttribute("staffClinicMap");
    List<ClinicBean> clinicList = (List<ClinicBean>) request.getAttribute("clinicList");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User List</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
    <div class="page">
        <div class="panel">
            <% if (msg != null) { %><div class="notice success"><%= msg %></div><% } %>

            <h3>Add User</h3>
            <form method="post" action="<%= request.getContextPath() %>/admin/users">
                <input type="hidden" name="action" value="add" />
                <div class="form-grid">
                    <div class="field">
                        <label for="username">Username</label>
                        <input id="username" type="text" name="username" required />
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input id="email" type="email" name="email" required />
                    </div>
                    <div class="field">
                        <label for="password">Password</label>
                        <input id="password" type="text" name="password" required />
                    </div>
                    <div class="field">
                        <label for="roleId">Role ID (1: Patient, 2: Staff, 3: Admin)</label>
                        <input id="roleId" type="number" name="roleId" value="1" min="1" max="3" required />
                    </div>
                    <div class="field">
                        <label for="clinicId">Staff Clinic ID (only for staff)</label>
                        <input id="clinicId" type="number" name="clinicId" min="1" />
                    </div>
                </div>
                <div class="actions">
                    <input type="submit" value="Add User" />
                </div>
            </form>
        </div>

        <div class="panel">
            <h3>Current Users</h3>
            <table>
        <tr>
            <th>ID</th>
            <th>Role</th>
            <th>Username</th>
            <th>Email</th>
            <th>Staff Clinic</th>
            <th>Edit</th>
            <th>Delete</th>
        </tr>
        <% if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                UserBean u = list.get(i);
        %>
        <tr>
            <td><%= u.getUserId() %></td>
            <td><%= roleNameMap != null && roleNameMap.get(u.getRoleId()) != null ? roleNameMap.get(u.getRoleId()) : u.getRoleId() %></td>
            <td><%= u.getUsername() %></td>
            <td><%= u.getEmail() %></td>
            <td><%= staffClinicMap != null && staffClinicMap.get(u.getUserId()) != null ? staffClinicMap.get(u.getUserId()) : "-" %></td>
            <td>
                <a href="<%= request.getContextPath() %>/admin/edit-user?userId=<%= u.getUserId() %>">Edit</a>
            </td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/admin/users">
                    <input type="hidden" name="action" value="delete" />
                    <input type="hidden" name="userId" value="<%= u.getUserId() %>" />
                    <input type="submit" value="Delete" class="btn danger" onclick="return confirm('Delete this user?');" />
                </form>
            </td>
        </tr>
        <%  }
           } else { %>
          <tr><td colspan="7">No users</td></tr>
        <% } %>
            </table>
        </div>

    </div>
</body>
</html>

