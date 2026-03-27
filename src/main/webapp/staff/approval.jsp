<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.AppointmentBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"STAFF".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<AppointmentBean> list = (List<AppointmentBean>) request.getAttribute("pendingList");
    Map<Integer, UserBean> uMap = (Map<Integer, UserBean>) request.getAttribute("userMap");
    Map<Integer, ServiceBean> sMap = (Map<Integer, ServiceBean>) request.getAttribute("serviceMap");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Approve Bookings</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
<div class="page">
    <h2>Approve or Reject Bookings</h2>

    <% if (msg != null) { %>
    <p><%= msg %></p>
    <% } %>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Date</th>
            <th>Time</th>
            <th>Patient</th>
            <th>Service</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        <% if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                AppointmentBean a = list.get(i);
                UserBean p = uMap != null ? uMap.get(a.getUserId()) : null;
                ServiceBean s = sMap != null ? sMap.get(a.getServiceId()) : null;
        %>
        <tr>
            <td><%= a.getAppointmentDate() %></td>
            <td><%= a.getTimeSlot() %></td>
            <td><%= p != null ? p.getUsername() : ("User #" + a.getUserId()) %></td>
            <td><%= s != null ? s.getServiceName() : ("Service #" + a.getServiceId()) %></td>
            <td><%= a.getStatus() %></td>
            <td>
                <form method="post" action="<%= request.getContextPath() %>/staff/approval" style="display:inline;">
                    <input type="hidden" name="appointmentId" value="<%= a.getAppointmentId() %>" />
                    <input type="hidden" name="action" value="approve" />
                    <input type="submit" value="Approve" />
                </form>
                <form method="post" action="<%= request.getContextPath() %>/staff/approval" style="display:inline;">
                    <input type="hidden" name="appointmentId" value="<%= a.getAppointmentId() %>" />
                    <input type="hidden" name="action" value="reject" />
                    <input type="text" name="reason" placeholder="reason" />
                    <input type="submit" value="Reject" />
                </form>
            </td>
        </tr>
        <%  }
           } else { %>
        <tr><td colspan="7">No pending bookings</td></tr>
        <% } %>
    </table>

</div>
</body>
</html>

