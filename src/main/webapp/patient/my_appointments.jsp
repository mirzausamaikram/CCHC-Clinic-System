<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.AppointmentBean"%>
<%@page import="com.cchc.model.UserBean"%>
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

    List list = (List) request.getAttribute("appointments");
    Map<Integer, String> clinicNameMap = (Map<Integer, String>) request.getAttribute("clinicNameMap");
    Map<Integer, String> serviceNameMap = (Map<Integer, String>) request.getAttribute("serviceNameMap");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Appointments</title>
    </head>
    <body>
        <h2>My Appointments</h2>

        <% if (session.getAttribute("msg") != null) { %>
          <p style="color:green;"><%= session.getAttribute("msg") %></p>
          <% session.removeAttribute("msg"); %>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
          <p style="color:red;"><%= request.getAttribute("error") %></p>
        <% } %>

        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Clinic</th>
                <th>Service</th>
                <th>Date</th>
                <th>Time</th>
                <th>Status</th>
                <th>Action</th>
            </tr>

            <% if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    AppointmentBean appt = (AppointmentBean) list.get(i);
                    String clinicName = clinicNameMap != null && clinicNameMap.get(appt.getClinicId()) != null
                            ? clinicNameMap.get(appt.getClinicId()) : "-";
                    String serviceName = serviceNameMap != null && serviceNameMap.get(appt.getServiceId()) != null
                            ? serviceNameMap.get(appt.getServiceId()) : "-";
            %>
            <tr>
                <td><%= clinicName %></td>
                <td><%= serviceName %></td>
                <td><%= appt.getAppointmentDate() %></td>
                <td><%= appt.getStartTime() %> - <%= appt.getEndTime() %></td>
                <td><%= appt.getStatus() %></td>
                <td>
                    <% if (!"CANCELLED".equals(appt.getStatus()) && !"COMPLETED".equals(appt.getStatus())) { %>
                    <form method="post" action="<%= request.getContextPath() %>/patient/my-bookings" style="display:inline;">
                        <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />
                        <input type="submit" value="Cancel" />
                    </form>
                    <a href="<%= request.getContextPath() %>/patient/reschedule?appointmentId=<%= appt.getAppointmentId() %>">Reschedule</a>
                    <% } else { %>
                    -
                    <% } %>
                </td>
            </tr>
            <%  }
               } else { %>
            <tr>
                <td colspan="6">No appointments found</td>
            </tr>
            <% } %>
        </table>

        <p>
            <a href="<%= request.getContextPath() %>/patient/appointments?action=book">Book Appointment</a> |
            <a href="<%= request.getContextPath() %>/my_appointments">My Appointments</a> |
            <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
