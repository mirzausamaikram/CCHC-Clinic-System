<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.ClinicServiceBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%@taglib prefix="cchc" uri="http://cchc/tags" %>
<%
    // simple check
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
    if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
    List<ClinicServiceBean> clinicServices = (List<ClinicServiceBean>) request.getAttribute("clinicServices");
    Map<Integer, ServiceBean> serviceMap = (Map<Integer, ServiceBean>) request.getAttribute("serviceMap");
    Integer cid = (Integer) request.getAttribute("selectedClinicId");
    if (cid == null) {
        cid = 0;
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book Appointment</title>
    </head>
    <body>
        <h1>Book Appointment</h1>
        <p>Welcome, <cchc:username /></p>

        <% String msg = (String) request.getAttribute("errorMessage"); %>
        <% if (msg != null) { %>
            <p><%= msg %></p>
        <% } %>

        <form method="get" action="<%= request.getContextPath() %>/patient/appointments">
            <input type="hidden" name="action" value="book" />
            <label for="clinicId">Clinic</label>
            <select id="clinicId" name="clinicId" onchange="this.form.submit()">
                <% if (clinics != null) {
                    for (ClinicBean clinic : clinics) { %>
                        <option value="<%= clinic.getClinicId() %>" <%= clinic.getClinicId() == cid ? "selected" : "" %>>
                            <%= clinic.getClinicName() %>
                        </option>
                <%  }
                } %>
            </select>
            <noscript><button type="submit">Load Services</button></noscript>
        </form>

        <hr/>

        <form method="post" action="<%= request.getContextPath() %>/patient/appointments">
            <label for="clinicServiceId">Service</label>
            <select id="clinicServiceId" name="clinicServiceId" required>
                <option value="">-- Select Service --</option>
                <% if (clinicServices != null) {
                    for (ClinicServiceBean cs : clinicServices) {
                        ServiceBean service = serviceMap != null ? serviceMap.get(cs.getServiceId()) : null;
                        String serviceName = service != null ? service.getServiceName() : ("Service #" + cs.getServiceId());
                %>
                        <option value="<%= cs.getClinicServiceId() %>">
                            <%= serviceName %> (<%= cs.getDurationMinutes() %> min)
                        </option>
                <%  }
                } %>
            </select>
            <br/><br/>

            <label for="appointmentDate">Appointment Date</label>
            <input type="date" id="appointmentDate" name="appointmentDate" required />
            <br/><br/>

            <label for="startTime">Start Time</label>
            <input type="time" id="startTime" name="startTime" required />
            <br/><br/>

            <label for="notes">Notes</label>
            <textarea id="notes" name="notes" rows="3" cols="40"></textarea>
            <br/><br/>

            <button type="submit">Book Appointment</button>
        </form>

        <hr/>
        <p>
            <a href="<%= request.getContextPath() %>/patient/appointments?action=list">My Appointments</a> |
            <a href="<%= request.getContextPath() %>/notifications">My Notifications</a> |
            <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
