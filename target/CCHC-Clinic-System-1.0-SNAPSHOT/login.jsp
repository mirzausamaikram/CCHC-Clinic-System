<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
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
        <title>CCHC Login</title>
        <style>
            body { font-family: Arial, sans-serif; margin: 40px; }
            .container { width: 380px; margin: 0 auto; }
            .card { border: 1px solid #ddd; border-radius: 8px; padding: 24px; }
            .field { margin-bottom: 12px; }
            label { display: block; margin-bottom: 5px; }
            input { width: 100%; padding: 8px; box-sizing: border-box; }
            .error { color: #b00020; margin-bottom: 12px; }
            button { padding: 10px 14px; }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="card">
                <h2>CCHC System Login</h2>

                <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
                <% if (errorMessage != null) { %>
                    <div class="error"><%= errorMessage %></div>
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
                    <button type="submit">Login</button>
                </form>
            </div>
        </div>
    </body>
</html>
