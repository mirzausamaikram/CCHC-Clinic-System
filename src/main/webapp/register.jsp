<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <div class="page narrow">
        <div class="topbar">
            <h1 class="brand">CCHC Clinic System</h1>
            <div class="nav-links">
                <a class="secondary" href="<%= request.getContextPath() %>/login.jsp">Back to Login</a>
            </div>
        </div>

        <div class="panel">
            <h2>Patient Registration</h2>
            <p class="muted">Complete all fields to create your patient account.</p>

            <% if (request.getAttribute("msg") != null) { %>
                <div class="notice error"><%= request.getAttribute("msg") %></div>
            <% } %>

            <form method="post" action="<%= request.getContextPath() %>/register">
                <div class="form-grid">
                    <div class="field">
                        <label for="username">Username</label>
                        <input type="text" id="username" name="username" required />
                    </div>
                    <div class="field">
                        <label for="email">Email</label>
                        <input type="email" id="email" name="email" required />
                    </div>
                    <div class="field">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required minlength="6" />
                    </div>
                    <div class="field">
                        <label for="fullName">Full Name</label>
                        <input type="text" id="fullName" name="fullName" required />
                    </div>
                    <div class="field">
                        <label for="phone">Phone</label>
                        <input type="text" id="phone" name="phone" required />
                    </div>
                </div>
                <div class="actions">
                    <input type="submit" value="Register" />
                    <a class="btn secondary" href="<%= request.getContextPath() %>/login.jsp">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
