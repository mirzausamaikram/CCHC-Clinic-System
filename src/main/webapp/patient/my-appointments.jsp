<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.AppointmentBean"%>
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
    if (!"PATIENT".equals(session.getAttribute("loginRole"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<AppointmentBean> list = (List<AppointmentBean>) request.getAttribute("appointments");
    Map<Integer, ClinicServiceBean> csMap = (Map<Integer, ClinicServiceBean>) request.getAttribute("clinicServiceMap");
    Map<Integer, ServiceBean> sMap = (Map<Integer, ServiceBean>) request.getAttribute("serviceMap");
    Map<Integer, ClinicBean> cMap = (Map<Integer, ClinicBean>) request.getAttribute("clinicMap");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Appointments</title>
    </head>
    <body>
        <h1>My Appointments</h1>
        <p>Welcome, <cchc:username /></p>

        <% if ("1".equals(request.getParameter("success"))) { %>
            <p style="color: green;">Appointment booked successfully.</p>
        <% } %>

        <table border="1" cellpadding="6" cellspacing="0">
            <tr>
                <th>ID</th>
                <th>Date</th>
                <th>Time</th>
                <th>Clinic</th>
                <th>Service</th>
                <th>Status</th>
                <th>Notes</th>
            </tr>
            <% if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    AppointmentBean appt = list.get(i);
                    ClinicServiceBean cs = csMap != null ? csMap.get(appt.getClinicServiceId()) : null;
                    ClinicBean clinic = (cs != null && cMap != null) ? cMap.get(cs.getClinicId()) : null;
                    ServiceBean service = (cs != null && sMap != null) ? sMap.get(cs.getServiceId()) : null;
            %>
            <tr>
                <td><%= appt.getAppointmentId() %></td>
                <td><%= appt.getAppointmentDate() %></td>
                <td><%= appt.getStartTime() %> - <%= appt.getEndTime() %></td>
                <td><%= clinic != null ? clinic.getClinicName() : "-" %></td>
                <td><%= service != null ? service.getServiceName() : "-" %></td>
                <td><%= appt.getStatus() %></td>
                <td><%= appt.getNotes() != null ? appt.getNotes() : "" %></td>
            </tr>
            <%  }
            } else { %>
            <tr>
                <td colspan="7">No appointments found.</td>
            </tr>
            <% } %>
        </table>

        <p>
            <a href="<%= request.getContextPath() %>/patient/appointments?action=book">Book New Appointment</a> |
            <a href="<%= request.getContextPath() %>/notifications">My Notifications</a> |
            <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
