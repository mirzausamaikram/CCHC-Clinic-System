<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@page import="com.cchc.model.PatientProfileBean"%>
<%
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String role = String.valueOf(session.getAttribute("loginRole"));
    if (!"PATIENT".equals(role) && !"STAFF".equals(role) && !"ADMIN".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    PatientProfileBean patientProfile = (PatientProfileBean) request.getAttribute("patientProfile");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>My Account Profile</title>
</head>
<body>
    <h2>My Account Profile</h2>
    <p>Role: <%= role %></p>

    <% if (msg != null) { %>
    <p><%= msg %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/account/profile">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <td>Username</td>
                <td><input type="text" name="username" value="<%= user.getUsername() == null ? "" : user.getUsername() %>" required /></td>
            </tr>
            <tr>
                <td>Email</td>
                <td><input type="email" name="email" value="<%= user.getEmail() == null ? "" : user.getEmail() %>" required /></td>
            </tr>
            <% if ("PATIENT".equals(role)) { %>
            <tr>
                <td>Phone</td>
                <td><input type="text" name="phone" value="<%= (patientProfile != null && patientProfile.getPhone() != null) ? patientProfile.getPhone() : "" %>" /></td>
            </tr>
            <% } %>
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
                <td colspan="2"><input type="submit" value="Update Profile" /></td>
            </tr>
        </table>
    </form>

    <p>
        <% if ("PATIENT".equals(role)) { %>
            <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a>
        <% } else if ("STAFF".equals(role)) { %>
            <a href="<%= request.getContextPath() %>/staff/dashboard.jsp">Back Dashboard</a>
        <% } else { %>
            <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back Dashboard</a>
        <% } %>
        |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
