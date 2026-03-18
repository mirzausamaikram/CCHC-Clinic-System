<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@page import="com.cchc.dao.AppointmentDAO"%>
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

    int total = 0;
    int done = 0;
    int noShow = 0;
    int util = 0;
    String msg = null;

    try {
        AppointmentDAO dao = new AppointmentDAO();
        total = dao.getTotalCount();
        done = dao.getCompletedCount();
        noShow = dao.getNoShowCount();
        if (total > 0) {
            util = (done * 100) / total; // simple calculation
        }
    } catch (Exception e) {
        msg = "Cannot load report now";
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Reports</title>
</head>
<body>
    <h2>Reports</h2>

    <% if (msg != null) { %>
        <p><%= msg %></p>
    <% } %>

    <h3>Utilisation Rate</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr><th>Item</th><th>Value</th></tr>
        <tr><td>Total Appointments</td><td><%= total %></td></tr>
        <tr><td>Completed Appointments</td><td><%= done %></td></tr>
        <tr><td>Utilisation %</td><td><%= util %></td></tr>
    </table>

    <h3>No-Show Summary</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr><th>Item</th><th>Value</th></tr>
        <tr><td>No Show Count</td><td><%= noShow %></td></tr>
    </table>

    <p>
        <a href="<%= request.getContextPath() %>/admin/users">User List</a> |
        <a href="<%= request.getContextPath() %>/admin/csv-import">Import CSV</a> |
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
