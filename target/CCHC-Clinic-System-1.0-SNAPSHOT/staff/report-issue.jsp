<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"STAFF".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Report Operational Issue</title>
</head>
<body>
    <h2>Report Operational Issue</h2>

    <% if (msg != null) { %>
    <p><b><%= msg %></b></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/staff/report-issue">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <td>Issue Type</td>
                <td>
                    <select name="issueType" required>
                        <option value="">-- Select --</option>
                        <option value="DOCTOR_UNAVAILABLE">Doctor unavailable</option>
                        <option value="SERVICE_SUSPENDED">Service suspended</option>
                        <option value="SYSTEM_PROBLEM">System problem</option>
                        <option value="OTHER">Other</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Details</td>
                <td><textarea name="details" rows="5" cols="50" required></textarea></td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Submit Issue" /></td>
            </tr>
        </table>
    </form>

    <p>
        <a href="<%= request.getContextPath() %>/staff/dashboard.jsp">Back Dashboard</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
