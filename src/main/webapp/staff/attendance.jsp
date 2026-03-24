<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.AppointmentBean"%>
<%@page import="com.cchc.model.QueueEntryBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    // simple session check
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    if (!"STAFF".equals(session.getAttribute("loginRole"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<AppointmentBean> apptList = (List<AppointmentBean>) request.getAttribute("apptList");
    List<QueueEntryBean> queueList = (List<QueueEntryBean>) request.getAttribute("queueList");
    Map<Integer, ServiceBean> serviceMap = (Map<Integer, ServiceBean>) request.getAttribute("serviceMap");
    List<ClinicBean> clinicList = (List<ClinicBean>) request.getAttribute("clinicList");
    String msg = (String) request.getAttribute("msg");
    String selectedDate = (String) request.getAttribute("selectedDate");
    String clinicName = (String) request.getAttribute("clinicName");
    Integer selectedClinicId = (Integer) request.getAttribute("selectedClinicId");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Daily Appointments</title>
    </head>
    <body>
        <h2>Daily Appointments</h2>

        <form method="get" action="<%= request.getContextPath() %>/staff/attendance">
            <label>Date:</label>
            <input type="date" name="selectedDate" value="<%= selectedDate != null ? selectedDate : "" %>" />
            <label>Clinic:</label>
            <select name="clinicId">
                <% if (clinicList != null) {
                    for (int i = 0; i < clinicList.size(); i++) {
                        ClinicBean c = clinicList.get(i);
                %>
                <option value="<%= c.getClinicId() %>" <%= (selectedClinicId != null && c.getClinicId() == selectedClinicId.intValue()) ? "selected" : "" %>><%= c.getClinicName() %></option>
                <%  }
                   } %>
            </select>
            <input type="submit" value="View" />
        </form>

        <p>Clinic: <%= clinicName != null ? clinicName : "-" %></p>

        <% if (msg != null) { %>
        <p><b><%= msg %></b></p>
        <% } %>

        <h3>Daily Appointment List</h3>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>ID</th>
                <th>Clinic</th>
                <th>Service</th>
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
                <td><%= clinicName != null ? clinicName : ("Clinic #" + appt.getClinicId()) %></td>
                <td><%= (serviceMap != null && serviceMap.get(appt.getServiceId()) != null)
                        ? serviceMap.get(appt.getServiceId()).getServiceName()
                        : ("Service #" + appt.getServiceId()) %></td>
                <td><%= appt.getStartTime() %></td>
                <td><%= appt.getEndTime() %></td>
                <td><%= appt.getStatus() %></td>
                <td>
                    <% if (!"COMPLETED".equals(appt.getStatus()) && !"CANCELLED".equals(appt.getStatus())) { %>

                    <form method="post" action="<%= request.getContextPath() %>/staff/attendance" style="display:inline">
                        <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />
                        <input type="hidden" name="status" value="ARRIVED" />
                        <input type="hidden" name="selectedDate" value="<%= selectedDate != null ? selectedDate : "" %>" />
                        <input type="hidden" name="clinicId" value="<%= selectedClinicId != null ? selectedClinicId : "" %>" />
                        <input type="submit" value="Mark Arrived" />
                    </form>

                    <form method="post" action="<%= request.getContextPath() %>/staff/attendance" style="display:inline">
                        <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />
                        <input type="hidden" name="status" value="COMPLETED" />
                        <input type="hidden" name="selectedDate" value="<%= selectedDate != null ? selectedDate : "" %>" />
                        <input type="hidden" name="clinicId" value="<%= selectedClinicId != null ? selectedClinicId : "" %>" />
                        <input type="submit" value="Mark Completed" />
                    </form>

                    <form method="post" action="<%= request.getContextPath() %>/staff/attendance" style="display:inline">
                        <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />
                        <input type="hidden" name="status" value="NO_SHOW" />
                        <input type="hidden" name="selectedDate" value="<%= selectedDate != null ? selectedDate : "" %>" />
                        <input type="hidden" name="clinicId" value="<%= selectedClinicId != null ? selectedClinicId : "" %>" />
                        <input type="submit" value="Mark No-show" />
                    </form>

                    <form method="post" action="<%= request.getContextPath() %>/staff/attendance" style="display:inline">
                        <input type="hidden" name="appointmentId" value="<%= appt.getAppointmentId() %>" />
                        <input type="hidden" name="status" value="CANCELLED" />
                        <input type="hidden" name="selectedDate" value="<%= selectedDate != null ? selectedDate : "" %>" />
                        <input type="hidden" name="clinicId" value="<%= selectedClinicId != null ? selectedClinicId : "" %>" />
                        <input type="text" name="reason" placeholder="cancel reason" />
                        <input type="submit" value="Cancel by Clinic" />
                    </form>

                    <% } else { %>
                    -
                    <% } %>
                </td>
            </tr>
            <%  }
               } else { %>
            <tr>
                <td colspan="7">No appointments found</td>
            </tr>
            <% } %>
        </table>

        <h3>Walk-in Queue List</h3>
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <th>Token No</th>
                <th>Service</th>
                <th>Status</th>
                <th>Priority</th>
            </tr>
            <% if (queueList != null && queueList.size() > 0) {
                for (int i = 0; i < queueList.size(); i++) {
                    QueueEntryBean q = queueList.get(i);
            %>
            <tr>
                <td><%= q.getTokenNo() %></td>
                <td><%= (serviceMap != null && serviceMap.get(q.getServiceId()) != null)
                        ? serviceMap.get(q.getServiceId()).getServiceName()
                        : ("Service #" + q.getServiceId()) %></td>
                <td><%= q.getQueueStatus() %></td>
                <td><%= q.getPriorityLevel() %></td>
            </tr>
            <%  }
               } else { %>
            <tr>
                <td colspan="4">No queue entries found</td>
            </tr>
            <% } %>
        </table>

        <p>
            <a href="<%= request.getContextPath() %>/staff/dashboard.jsp">Back to Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
