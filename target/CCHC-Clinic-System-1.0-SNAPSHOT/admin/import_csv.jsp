<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Import CSV</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
<div class="page">
    <h2>Import Services CSV</h2>

    <% if (msg != null) { %>
        <div class="notice success"><%= msg %></div>
    <% } %>

    <div class="panel">
        <p><a href="<%= request.getContextPath() %>/admin/csv-import?action=sample">Download Sample CSV</a></p>
        <form method="post" action="<%= request.getContextPath() %>/admin/csv-import" enctype="multipart/form-data">
            <div class="field">
                <label>CSV File</label>
                <input type="file" name="file" accept=".csv" required />
            </div>
            <div class="actions">
                <input type="submit" value="Upload" />
            </div>
        </form>
    </div>

</div>
</body>
</html>

