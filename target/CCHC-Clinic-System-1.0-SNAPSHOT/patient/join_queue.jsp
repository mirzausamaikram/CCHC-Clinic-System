<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@page import="java.util.List"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%
    // Check if patient is logged in
    UserBean user = (UserBean) session.getAttribute("user");
    if (user == null) {
        user = (UserBean) session.getAttribute("loginUser");
        if (user != null) {
            session.setAttribute("user", user);
        }
    }
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String role = String.valueOf(session.getAttribute("loginRole"));
    if (!"PATIENT".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String displayName = "Patient";
    if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
        displayName = user.getFullName();
    } else if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
        displayName = user.getUsername();
    } else if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
        displayName = user.getEmail();
    }

    // Get clinics and services from request
    List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
    List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
    String msg = (String) request.getAttribute("msg");
    
    // Load if not set
    if (clinics == null) {
        try {
            com.cchc.dao.ClinicDAO clinicDao = new com.cchc.dao.ClinicDAO();
            clinics = clinicDao.getAllClinics();
        } catch (Exception e) {
            clinics = new java.util.ArrayList<>();
        }
    }
    
    if (services == null) {
        try {
            com.cchc.dao.ServiceDAO serviceDao = new com.cchc.dao.ServiceDAO();
            services = serviceDao.getAllServices();
        } catch (Exception e) {
            services = new java.util.ArrayList<>();
        }
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Join Walk-in Queue</title>
    </head>
    <body>
        <h1>Join Walk-in Queue</h1>
        <p>Welcome, <%= displayName %></p>

        <% if (msg != null) { %>
        <p><b><%= msg %></b></p>
        <% } %>

        <!-- Join queue form -->
        <h3>Select Clinic and Service</h3>
        <form method="post" action="<%= request.getContextPath() %>/patient/queue">
            <table border="0" cellpadding="5">
                <tr>
                    <td>Clinic:</td>
                    <td>
                        <select name="clinicId" required>
                            <option value="">-- Select Clinic --</option>
                            <% if (clinics != null) {
                                for (int i = 0; i < clinics.size(); i++) {
                                    ClinicBean c = clinics.get(i);
                            %>
                            <option value="<%= c.getClinicId() %>">
                                <%= c.getClinicName() %>
                            </option>
                            <% } } %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Service:</td>
                    <td>
                        <select name="serviceId" required>
                            <option value="">-- Select Service --</option>
                            <% if (services != null) {
                                for (int i = 0; i < services.size(); i++) {
                                    ServiceBean s = services.get(i);
                            %>
                            <option value="<%= s.getServiceId() %>">
                                <%= s.getServiceName() %>
                            </option>
                            <% } } %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Notes:</td>
                    <td><textarea name="notes" rows="3" cols="30"></textarea></td>
                </tr>
                <tr>
                    <td colspan="2"><input type="submit" value="Join Queue" /></td>
                </tr>
            </table>
        </form>

        <p>
            <a href="<%= request.getContextPath() %>/patient/queue-status">My Queue Status</a> |
            <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back to Dashboard</a>
        </p>
    </body>
</html>
