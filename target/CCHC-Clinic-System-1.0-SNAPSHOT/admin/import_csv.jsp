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

    <p>
        <a href="<%= request.getContextPath() %>/admin/csv-import?action=sample">Download Sample CSV</a>
    </p>

    <% if (msg != null) { %>
        <p><%= msg %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/admin/csv-import" enctype="multipart/form-data">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <td>CSV File</td>
                <td><input type="file" name="file" accept=".csv" required /></td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Upload" /></td>
            </tr>
        </table>
    </form>

    <p>
        <a href="<%= request.getContextPath() %>/admin/users">User List</a> |
        <a href="<%= request.getContextPath() %>/admin/reports">Reports</a> |
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back</a>
    </p>
</body>
</html>
