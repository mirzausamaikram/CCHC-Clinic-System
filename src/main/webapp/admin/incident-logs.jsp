<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
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

    List<Map<String, Object>> issueList = (List<Map<String, Object>>) request.getAttribute("issueList");
    List<Map<String, Object>> noShowList = (List<Map<String, Object>>) request.getAttribute("noShowList");
    List<Map<String, Object>> cancelList = (List<Map<String, Object>>) request.getAttribute("cancelList");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Incident Logs</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
<div class="page">
    <h2>Incident Logs</h2>

    <% if (msg != null) { %>
    <p><b><%= msg %></b></p>
    <% } %>

    <h3>Operational Issues from Staff</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>ID</th>
            <th>Staff</th>
            <th>Issue Type</th>
            <th>Details</th>
            <th>Time</th>
        </tr>
        <% if (issueList != null && issueList.size() > 0) {
            for (int i = 0; i < issueList.size(); i++) {
                Map<String, Object> x = issueList.get(i);
        %>
        <tr>
            <td><%= x.get("auditId") %></td>
            <td><%= x.get("username") != null ? x.get("username") : ("User #" + x.get("userId")) %></td>
            <td><%= x.get("entityId") %></td>
            <td><%= x.get("details") %></td>
            <td><%= x.get("createdAt") %></td>
        </tr>
        <%  }
           } else { %>
        <tr><td colspan="5">No operational issues logged</td></tr>
        <% } %>
    </table>

    <h3>Repeated No-show (2+)</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>User ID</th>
            <th>No-show Count</th>
        </tr>
        <% if (noShowList != null && noShowList.size() > 0) {
            for (int i = 0; i < noShowList.size(); i++) {
                Map<String, Object> x = noShowList.get(i);
        %>
        <tr>
            <td><%= x.get("userId") %></td>
            <td><%= x.get("count") %></td>
        </tr>
        <%  }
           } else { %>
        <tr><td colspan="2">No repeated no-show found</td></tr>
        <% } %>
    </table>

    <h3>Frequent Cancellations (2+)</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>User ID</th>
            <th>Cancellation Count</th>
        </tr>
        <% if (cancelList != null && cancelList.size() > 0) {
            for (int i = 0; i < cancelList.size(); i++) {
                Map<String, Object> x = cancelList.get(i);
        %>
        <tr>
            <td><%= x.get("userId") %></td>
            <td><%= x.get("count") %></td>
        </tr>
        <%  }
           } else { %>
        <tr><td colspan="2">No frequent cancellation found</td></tr>
        <% } %>
    </table>

</div>
</body>
</html>

