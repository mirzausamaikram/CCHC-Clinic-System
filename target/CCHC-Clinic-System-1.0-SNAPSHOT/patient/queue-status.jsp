<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.cchc.model.QueueEntryBean"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%@page import="com.cchc.model.UserBean"%>
<%
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<QueueEntryBean> queueList = (List<QueueEntryBean>) request.getAttribute("queueList");
    Map<Integer, ClinicBean> clinicMap = (Map<Integer, ClinicBean>) request.getAttribute("clinicMap");
    Map<Integer, ServiceBean> serviceMap = (Map<Integer, ServiceBean>) request.getAttribute("serviceMap");
    Map<Integer, Integer> estimateMap = (Map<Integer, Integer>) request.getAttribute("estimateMap");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>My Queue Status</title>
    <meta http-equiv="refresh" content="20">
</head>
<body>
    <h2>My Queue Status</h2>
    <p>Auto refresh every 20 seconds.</p>

    <% if (msg != null) { %>
    <p><b><%= msg %></b></p>
    <% } %>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>Clinic</th>
            <th>Service</th>
            <th>Token</th>
            <th>Status</th>
            <th>Estimated Wait (mins)</th>
            <th>Queue Date</th>
        </tr>
        <% if (queueList != null && queueList.size() > 0) {
            for (int i = 0; i < queueList.size(); i++) {
                QueueEntryBean q = queueList.get(i);
                ClinicBean c = clinicMap != null ? clinicMap.get(q.getClinicId()) : null;
                ServiceBean s = serviceMap != null ? serviceMap.get(q.getServiceId()) : null;
        %>
        <tr>
            <td><%= c != null ? c.getClinicName() : ("Clinic #" + q.getClinicId()) %></td>
            <td><%= s != null ? s.getServiceName() : ("Service #" + q.getServiceId()) %></td>
            <td><%= q.getTokenNo() %></td>
            <td><%= q.getQueueStatus() %></td>
            <td><%= estimateMap != null && estimateMap.get(q.getQueueId()) != null ? estimateMap.get(q.getQueueId()) : 0 %></td>
            <td><%= q.getQueueDate() %></td>
        </tr>
        <%  }
           } else { %>
        <tr>
            <td colspan="6">No queue tickets found for today</td>
        </tr>
        <% } %>
    </table>

    <p>
        <a href="<%= request.getContextPath() %>/patient/queue">Join Walk-in Queue</a> |
        <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
