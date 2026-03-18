<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Import CSV</title>
</head>
<body>
    <h2>Import Services CSV</h2>
    <p>// TODO: make better later</p>

    <p>CSV format:</p>
    <p>service_code,service_name,service_description,duration</p>

    <% if (msg != null) { %>
        <p><%= msg %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/admin/csv-import" enctype="multipart/form-data">
        <input type="file" name="file" accept=".csv" required />
        <input type="submit" value="Upload" />
    </form>

    <p>
        <a href="<%= request.getContextPath() %>/admin/users">User List</a> |
        <a href="<%= request.getContextPath() %>/admin/reports">Reports</a> |
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back</a>
    </p>
</body>
</html>
