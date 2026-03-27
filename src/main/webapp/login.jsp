<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%-- using JSP Action for bean management --%>
<jsp:useBean id="user" class="com.cchc.model.UserBean" scope="session" />
<jsp:setProperty name="user" property="*" />
<%
    HttpSession existingSession = request.getSession(false);
    if (existingSession != null && existingSession.getAttribute("loginUser") != null && existingSession.getAttribute("loginRole") != null) {
        String role = String.valueOf(existingSession.getAttribute("loginRole"));
        if ("PATIENT".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/patient/dashboard.jsp");
            return;
        } else if ("STAFF".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/staff/dashboard.jsp");
            return;
        } else if ("ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
            return;
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>CCHC Login</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    </head>
    <body>
        <div class="page narrow">
            <div class="topbar">
                <h1 class="brand">CCHC Clinic System</h1>
                <div class="nav-links">
                    <a class="secondary" href="<%= request.getContextPath() %>/register">Create Patient Account</a>
                </div>
            </div>
            <div class="panel login-card">
                <h2>Sign In</h2>

                <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
                <% if (errorMessage != null) { %>
                    <div class="notice error"><%= errorMessage %></div>
                <% } %>

                <form method="post" action="<%= request.getContextPath() %>/login">
                    <div class="field">
                        <label for="usernameOrEmail">Username or Email</label>
                        <input type="text" id="usernameOrEmail" name="usernameOrEmail" required>
                    </div>
                    <div class="field">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <div class="actions">
                        <button type="submit">Login</button>
                    </div>
                </form>
            </div>
        </div>
    </body>
</html>
