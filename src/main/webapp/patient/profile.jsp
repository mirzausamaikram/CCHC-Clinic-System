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
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
    <div class="page">
        <div class="panel">
            <% if (msg != null) { %>
            <div class="notice success"><%= msg %></div>
            <% } %>
            <form method="post" action="<%= request.getContextPath() %>/patient/profile">
                <div class="form-grid">
                    <div class="field">
                        <label>Email</label>
                        <input type="email" name="email" value="<%= user.getEmail() == null ? "" : user.getEmail() %>" required />
                    </div>
                    <div class="field">
                        <label>Phone</label>
                        <input type="text" name="phone" value="<%= (p != null && p.getPhone() != null) ? p.getPhone() : "" %>" />
                    </div>
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
                    <input type="submit" value="Update" />
                </div>
            </form>
        </div>
    </div>
</body>
</html>

