<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.PatientProfileBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    PatientProfileBean p = (PatientProfileBean) request.getAttribute("profile");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Update Profile</title>
</head>
<body>
    <h2>Update Profile</h2>

    <% if (msg != null) { %>
    <p><%= msg %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/patient/profile">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <td>Email</td>
                <td><input type="email" name="email" value="<%= user.getEmail() == null ? "" : user.getEmail() %>" required /></td>
            </tr>
            <tr>
                <td>Phone</td>
                <td><input type="text" name="phone" value="<%= (p != null && p.getPhone() != null) ? p.getPhone() : "" %>" /></td>
            </tr>
            <tr>
                <td>Current Password</td>
                <td><input type="password" name="currentPassword" /></td>
            </tr>
            <tr>
                <td>New Password</td>
                <td><input type="password" name="newPassword" /></td>
            </tr>
            <tr>
                <td>Confirm Password</td>
                <td><input type="password" name="confirmPassword" /></td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Update" /></td>
            </tr>
        </table>
    </form>

    <p>
        <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
