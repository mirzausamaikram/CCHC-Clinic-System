<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.cchc.model.AppointmentBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    // check if user is logged in
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

    List<AppointmentBean> apptList = (List<AppointmentBean>) request.getAttribute("apptList");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Bookings</title>
    </head>
    <body>
        <h2>My Appointments</h2>

        <% if (msg != null) { %>
            <p><%= msg %></p>
        <% } %>

        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>ID</th>
                <th>Date</th>
                <th>Start</th>
                <th>End</th>
                <th>Status</th>
                <th>Action</th>
            </tr>

            <% if (apptList != null && apptList.size() > 0) {
                for (int i = 0; i < apptList.size(); i++) {
                    AppointmentBean appt = apptList.get(i);
            %>
            <tr>
                <td><%= appt.getAppointmentId() %></td>
                <td><%= appt.getAppointmentDate() %></td>
                <td><%= appt.getStartTime() %></td>
                <td><%= appt.getEndTime() %></td>
                <td><%= appt.getStatus() %></td>
                <td>
                    <% if (!"CANCELLED".equals(appt.getStatus()) && !"COMPLETED".equals(appt.getStatus())) { %>
                    <!-- simple reschedule for patient -->
                    <a href="<%= request.getContextPath() %>/patient/reschedule?appointmentId=<%= appt.getAppointmentId() %>">Reschedule</a>
                    |
                    <form method="post" action="<%= request.getContextPath() %>/patient/my-bookings">
                        <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />
                        <input type="submit" value="Cancel" />
                    </form>
                    <% } else { %>
                    -
                    <% } %>
                </td>
            </tr>
            <%  }
               } else { %>
            <tr>
                <td colspan="6">No bookings found</td>
            </tr>
            <% } %>
        </table>

        <p>
            <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
