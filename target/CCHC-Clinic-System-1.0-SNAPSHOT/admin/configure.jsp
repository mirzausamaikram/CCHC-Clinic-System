<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@page import="java.util.List"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%
    // simple check
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<ClinicBean> clinicList = (List<ClinicBean>) request.getAttribute("clinicList");
    List<ServiceBean> serviceList = (List<ServiceBean>) request.getAttribute("serviceList");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Configure Clinics & Services</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
    <h2>Configure Clinics & Services</h2>
    <% if (msg != null) { %>
        <p><b><%= msg %></b></p>
    <% } %>

    <h3>Add Clinic</h3>
    <form method="post" action="<%= request.getContextPath() %>/admin/configure">
        <input type="hidden" name="action" value="addClinic" />
        Code: <input type="text" name="clinicCode" required />
        Name: <input type="text" name="clinicName" required />
        Address: <input type="text" name="address" required />
        City: <input type="text" name="city" required />
        State: <input type="text" name="state" required />
        Phone: <input type="text" name="phone" required />
        Open: <input type="time" name="openingTime" required />
        Close: <input type="time" name="closingTime" required />
        <input type="submit" value="Add Clinic" />
    </form>

    <h3>Add Service</h3>
    <form method="post" action="<%= request.getContextPath() %>/admin/configure">
        <input type="hidden" name="action" value="addService" />
        Code: <input type="text" name="serviceCode" required />
        Name: <input type="text" name="serviceName" required />
        Description: <input type="text" name="serviceDescription" required />
        Duration (mins): <input type="number" name="duration" min="5" value="20" required />
        <input type="submit" value="Add Service" />
    </form>

    <h3>Clinics</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>ID</th><th>Code</th><th>Name</th><th>City</th><th>Open</th><th>Close</th><th>Active</th><th>Actions</th>
        </tr>
        <% if (clinicList != null) {
            for (int i = 0; i < clinicList.size(); i++) {
                ClinicBean c = clinicList.get(i);
        %>
        <tr>
            <td><%= c.getClinicId() %></td>
            <td><%= c.getClinicCode() %></td>
            <td><%= c.getClinicName() %></td>
            <td><%= c.getCity() %></td>
            <td><%= c.getOpeningTime() %></td>
            <td><%= c.getClosingTime() %></td>
            <td><%= c.isActive() ? "Yes" : "No" %></td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/admin/configure" style="display:inline;">
                    <input type="hidden" name="action" value="toggleClinic" />
                    <input type="hidden" name="clinicId" value="<%= c.getClinicId() %>" />
                    <input type="hidden" name="active" value="<%= c.isActive() ? "0" : "1" %>" />
                    <input type="submit" value="<%= c.isActive() ? "Deactivate" : "Activate" %>" />
                </form>
                <form method="post" action="<%= request.getContextPath() %>/admin/configure" style="display:inline;">
                    <input type="hidden" name="action" value="updateHours" />
                    <input type="hidden" name="clinicId" value="<%= c.getClinicId() %>" />
                    <input type="time" name="openingTime" required />
                    <input type="time" name="closingTime" required />
                    <input type="submit" value="Update Hours" />
                </form>
            </td>
        </tr>
        <%  }
           } %>
    </table>

    <h3>Services</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>ID</th><th>Code</th><th>Name</th><th>Duration</th><th>Active</th><th>Action</th>
        </tr>
        <% if (serviceList != null) {
            for (int i = 0; i < serviceList.size(); i++) {
                ServiceBean s = serviceList.get(i);
        %>
        <tr>
            <td><%= s.getServiceId() %></td>
            <td><%= s.getServiceCode() %></td>
            <td><%= s.getServiceName() %></td>
            <td><%= s.getDefaultDurationMinutes() %></td>
            <td><%= s.isActive() ? "Yes" : "No" %></td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/admin/configure">
                    <input type="hidden" name="action" value="toggleService" />
                    <input type="hidden" name="serviceId" value="<%= s.getServiceId() %>" />
                    <input type="hidden" name="active" value="<%= s.isActive() ? "0" : "1" %>" />
                    <input type="submit" value="<%= s.isActive() ? "Deactivate" : "Activate" %>" />
                </form>
            </td>
        </tr>
        <%  }
           } %>
    </table>

    <h3>Capacity Rule (Quota per Clinic+Service per Timeslot)</h3>
    <form method="post" action="<%= request.getContextPath() %>/admin/configure">
        <input type="hidden" name="action" value="saveQuota" />
        Clinic ID: <input type="number" name="clinicId" min="1" required />
        Service ID: <input type="number" name="serviceId" min="1" required />
        Quota: <input type="number" name="quota" min="1" required />
        <input type="submit" value="Save Capacity Rule" />
    </form>

</body>
</html>

