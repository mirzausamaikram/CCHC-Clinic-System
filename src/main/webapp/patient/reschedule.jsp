<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.AppointmentBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    // simple session check
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (!"PATIENT".equals(session.getAttribute("loginRole"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    AppointmentBean appt = (AppointmentBean) request.getAttribute("appt");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Reschedule Appointment</title>
    </head>
    <body>
        <h2>Reschedule Appointment</h2>

        <% if (msg != null) { %>
        <p><%= msg %></p>
        <% } %>

        <% if (appt == null) { %>
        <p>Booking not found.</p>
        <p><a href="<%= request.getContextPath() %>/patient/my-bookings">Back</a></p>
        <% } else { %>

        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Appointment ID</th>
                <th>Current Date</th>
                <th>Current Start</th>
                <th>Current End</th>
                <th>Status</th>
            </tr>
            <tr>
                <td><%= appt.getAppointmentId() %></td>
                <td><%= appt.getAppointmentDate() %></td>
                <td><%= appt.getStartTime() %></td>
                <td><%= appt.getEndTime() %></td>
                <td><%= appt.getStatus() %></td>
            </tr>
        </table>

        <br/>

        <!-- simple reschedule for patient -->
        <form method="post" action="<%= request.getContextPath() %>/patient/reschedule">
            <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />

            <table border="1" cellpadding="5" cellspacing="0">
                <tr>
                    <td>New Date</td>
                    <td><input type="date" name="newDate" required /></td>
                </tr>
                <tr>
                    <td>Time Slot</td>
                    <td>
                        <select name="timeSlot" required>
                            <option value="">-- Select --</option>
                            <option value="09:00-09:30">09:00-09:30</option>
                            <option value="09:30-10:00">09:30-10:00</option>
                            <option value="10:00-10:30">10:00-10:30</option>
                            <option value="10:30-11:00">10:30-11:00</option>
                            <option value="11:00-11:30">11:00-11:30</option>
                            <option value="11:30-12:00">11:30-12:00</option>
                            <option value="14:00-14:30">14:00-14:30</option>
                            <option value="14:30-15:00">14:30-15:00</option>
                            <option value="15:00-15:30">15:00-15:30</option>
                            <option value="15:30-16:00">15:30-16:00</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <input type="submit" value="Update Booking" />
                        <a href="<%= request.getContextPath() %>/patient/my-bookings">Cancel</a>
                    </td>
                </tr>
            </table>
        </form>

        <% } %>
    </body>
</html>