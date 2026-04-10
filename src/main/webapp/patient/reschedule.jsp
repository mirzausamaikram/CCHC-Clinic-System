<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.AppointmentBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
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
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    </head>
    <body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
    <div class="page">
        <div class="panel">
            <% if (msg != null) { %>
            <div class="notice error"><%= msg %></div>
            <% } %>
            <% if (appt == null) { %>
                <p class="notice error">Booking not found.</p>
                <div class="actions">
                    <a class="btn secondary" href="<%= request.getContextPath() %>/patient/my-bookings">Back</a>
                </div>
            <% } else { %>
                <div class="form-grid" style="margin-bottom:18px;">
                    <div class="field"><label>Appointment ID</label><p><%= appt.getAppointmentId() %></p></div>
                    <div class="field"><label>Current Date</label><p><%= appt.getAppointmentDate() %></p></div>
                    <div class="field"><label>Time</label><p><%= appt.getStartTime() %> &ndash; <%= appt.getEndTime() %></p></div>
                    <div class="field"><label>Status</label><p><%= appt.getStatus() %></p></div>
                </div>
                <form method="post" action="<%= request.getContextPath() %>/patient/reschedule">
                    <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />
                    <div class="form-grid">
                        <div class="field">
                            <label>New Date</label>
                            <input type="date" name="newDate" required />
                        </div>
                        <div class="field">
                            <label>Time Slot</label>
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
                        </div>
                    </div>
                    <div class="actions">
                        <input type="submit" value="Update Booking" />
                        <a class="btn secondary" href="<%= request.getContextPath() %>/patient/my-bookings">Cancel</a>
                    </div>
                </form>
            <% } %>
        </div>
    </div>
    </body>
</html>
