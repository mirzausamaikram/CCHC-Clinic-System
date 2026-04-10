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
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
    <div class="page">
        <div class="panel">
            <% if (msg != null) { %>
            <div class="notice success"><%= msg %></div>
            <% } %>
            <form method="post" action="<%= request.getContextPath() %>/account/profile">
                <div class="form-grid">
                    <div class="field">
                        <label>Username</label>
                        <input type="text" name="username" value="<%= user.getUsername() == null ? "" : user.getUsername() %>" required />
                    </div>
                    <div class="field">
                        <label>Email</label>
                        <input type="email" name="email" value="<%= user.getEmail() == null ? "" : user.getEmail() %>" required />
                    </div>
                    <% if ("PATIENT".equals(role)) { %>
                    <div class="field">
                        <label>Phone</label>
                        <input type="text" name="phone" value="<%= (patientProfile != null && patientProfile.getPhone() != null) ? patientProfile.getPhone() : "" %>" />
                    </div>
                    <% } %>
                    <div class="field">
                        <label>Current Password</label>
                        <input type="password" name="currentPassword" />
                    </div>
                    <div class="field">
                        <label>New Password</label>
                        <input type="password" name="newPassword" />
                    </div>
                    <div class="field">
                        <label>Confirm Password</label>
                        <input type="password" name="confirmPassword" />
                    </div>
                </div>
                <div class="actions">
                    <input type="submit" value="Update Profile" />
                </div>
            </form>
        </div>
    </div>
</body>
</html>

