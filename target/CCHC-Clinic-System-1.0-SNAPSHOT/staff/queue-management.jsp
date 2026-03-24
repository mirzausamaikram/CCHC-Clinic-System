<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.ClinicServiceBean"%>
<%@page import="com.cchc.model.AppointmentBean"%>
<%@page import="com.cchc.model.PatientProfileBean"%>
<%@page import="com.cchc.model.QueueEntryBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%@page import="com.cchc.model.StaffProfileBean"%>
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
    if (!"STAFF".equals(session.getAttribute("loginRole"))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    StaffProfileBean staff = (StaffProfileBean) request.getAttribute("staffProfile");
    List<QueueEntryBean> list = (List<QueueEntryBean>) request.getAttribute("queueList");
    List<ClinicServiceBean> csList = (List<ClinicServiceBean>) request.getAttribute("clinicServices");
    List<PatientProfileBean> pList = (List<PatientProfileBean>) request.getAttribute("patients");
    List<UserBean> uList = (List<UserBean>) request.getAttribute("patientUsers");
    List<ServiceBean> svcList = (List<ServiceBean>) request.getAttribute("allServices");
    List<AppointmentBean> bList = (List<AppointmentBean>) request.getAttribute("todayBookings");
    Map<Integer, ServiceBean> sMap = (Map<Integer, ServiceBean>) request.getAttribute("serviceMap");
    Map<Integer, PatientProfileBean> pMap = (Map<Integer, PatientProfileBean>) request.getAttribute("patientMap");
    Map<Integer, UserBean> uMap = (Map<Integer, UserBean>) request.getAttribute("userMap");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Queue Management</title>
    </head>
    <body>
        <h1>Queue Management</h1>
        <p>Welcome, <cchc:username /></p>
        <% if (staff != null) { %>
            <p>Clinic ID: <%= staff.getClinicId() %></p>
        <% } %>

        <% String msg = (String) request.getAttribute("errorMessage"); %>
        <% String err = request.getParameter("err"); %>
        <% if (msg != null) { %>
            <p><%= msg %></p>
        <% } %>
        <% if (err != null) { %>
            <p style="color:red;"><%= err %></p>
        <% } %>
        <% if ("1".equals(request.getParameter("success"))) { %>
            <p style="color: green;">Queue action completed.</p>
        <% } %>

        <h2>Add Walk-in Patient</h2>
        <form method="post" action="<%= request.getContextPath() %>/staff/queue">
            <input type="hidden" name="action" value="addWalkIn" />

            <label for="patientId">Patient</label>
            <select id="patientId" name="patientId" required>
                <option value="">-- Select Patient --</option>
                <% if (uList != null) {
                    for (UserBean patient : uList) { %>
                        <option value="<%= patient.getUserId() %>"><%= patient.getUsername() %> (<%= patient.getEmail() %>)</option>
                <%  }
                } %>
            </select>

            <label for="clinicServiceId">Clinic Service</label>
            <select id="clinicServiceId" name="clinicServiceId" required>
                <option value="">-- Select Service --</option>
                <% if (svcList != null) {
                    for (ServiceBean service : svcList) {
                %>
                        <option value="<%= service.getServiceId() %>">
                            <%= service.getServiceName() %>
                        </option>
                <%  }
                } %>
            </select>

            <label for="priorityLevel">Priority Level</label>
            <input type="number" id="priorityLevel" name="priorityLevel" value="0" min="0" max="9" />

            <button type="submit">Add to Queue</button>
        </form>

        <h2>Today's Queue</h2>
        <table border="1" cellpadding="6" cellspacing="0">
            <tr>
                <th>Queue ID</th>
                <th>Token</th>
                <th>Patient</th>
                <th>Service</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <% if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    QueueEntryBean queue = list.get(i);
                    PatientProfileBean patient = pMap != null ? pMap.get(queue.getPatientId()) : null;
                    ServiceBean service = sMap != null ? sMap.get(queue.getServiceId()) : null;
            %>
            <tr>
                <td><%= queue.getQueueId() %></td>
                <td><%= queue.getTokenNo() %></td>
                <td><%= patient != null ? patient.getFullName() : ("Patient #" + queue.getPatientId()) %></td>
                <td><%= service != null ? service.getServiceName() : ("Service #" + queue.getServiceId()) %></td>
                <td><%= queue.getQueueStatus() %></td>
                <td>
                    <form method="post" action="<%= request.getContextPath() %>/staff/queue" style="display:inline;">
                        <input type="hidden" name="action" value="updateStatus" />
                        <input type="hidden" name="queueId" value="<%= queue.getQueueId() %>" />
                        <input type="hidden" name="patientId" value="<%= queue.getPatientId() %>" />
                        <select name="queueStatus">
                            <option value="WAITING">WAITING</option>
                            <option value="CALLED">CALLED</option>
                            <option value="IN_SERVICE">IN_SERVICE</option>
                            <option value="DONE">DONE</option>
                            <option value="MISSED">MISSED</option>
                            <option value="CANCELLED">CANCELLED</option>
                        </select>
                        <button type="submit">Update</button>
                    </form>
                </td>
            </tr>
            <%  }
            } else if (bList != null && bList.size() > 0) {
                for (int i = 0; i < bList.size(); i++) {
                    AppointmentBean a = bList.get(i);
                    UserBean pu = uMap != null ? uMap.get(a.getUserId()) : null;
                    ServiceBean service = sMap != null ? sMap.get(a.getServiceId()) : null;
            %>
            <tr>
                <td>-</td>
                <td>-</td>
                <td><%= pu != null ? pu.getUsername() : ("User #" + a.getUserId()) %></td>
                <td><%= service != null ? service.getServiceName() : ("Service #" + a.getServiceId()) %></td>
                <td><%= a.getStatus() %></td>
                <td>
                    <form method="post" action="<%= request.getContextPath() %>/staff/queue" style="display:inline;">
                        <input type="hidden" name="action" value="addWalkIn" />
                        <input type="hidden" name="patientId" value="<%= a.getUserId() %>" />
                        <input type="hidden" name="clinicServiceId" value="<%= a.getServiceId() %>" />
                        <input type="hidden" name="priorityLevel" value="0" />
                        <button type="submit">Add to Queue</button>
                    </form>
                </td>
            </tr>
            <%  }
            } else { %>
            <tr>
                <td colspan="6">No queue entries for today.</td>
            </tr>
            <% } %>
        </table>

        <p>
            <a href="<%= request.getContextPath() %>/notifications">My Notifications</a> |
            <a href="<%= request.getContextPath() %>/staff/dashboard.jsp">Back Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
