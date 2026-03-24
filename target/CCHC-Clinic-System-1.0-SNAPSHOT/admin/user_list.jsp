<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    // simple check
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
    <title>User List</title>
</head>
<body>
    <h2>User List</h2>

    <% if (msg != null) { %><p><%= msg %></p><% } %>

    <h3>Add User</h3>
    <form method="post" action="<%= request.getContextPath() %>/admin/users">
        <input type="hidden" name="action" value="add" />
        Username: <input type="text" name="username" required />
        Email: <input type="text" name="email" required />
        Password: <input type="text" name="password" required />
        Role ID: <input type="number" name="roleId" value="1" min="1" max="3" />
        Staff Clinic ID (if role=STAFF): <input type="number" name="clinicId" min="1" />
        <input type="submit" value="Add" />
    </form>

    <h3>Current Users</h3>
    <table border="1" cellpadding="4" cellspacing="0">
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
                    <input type="submit" value="Delete" />
                </form>
            </td>
        </tr>
        <%  }
           } else { %>
          <tr><td colspan="7">No users</td></tr>
        <% } %>
    </table>

    <p>
        <a href="<%= request.getContextPath() %>/admin/reports">Open Reports</a> |
        <a href="<%= request.getContextPath() %>/admin/csv-import">Import CSV</a> |
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
