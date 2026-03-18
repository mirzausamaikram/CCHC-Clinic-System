<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>CSV Import</title>
</head>
<body>
    <h2>CSV Import (Services)</h2>

    <p>CSV format example:</p>
    <p>service_code,service_name,service_description,duration</p>
    <p>SRV-900,Test Service,Some text,20</p>

    <% if (msg != null) { %>
        <p><%= msg %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/admin/csv-import" enctype="multipart/form-data">
        <input type="file" name="csvFile" accept=".csv" required>
        <input type="submit" value="Upload CSV">
    </form>

    <p>
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back Dashboard</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
