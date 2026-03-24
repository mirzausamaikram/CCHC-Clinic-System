<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%
    // simple check
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<ClinicBean> clinicList = (List<ClinicBean>) request.getAttribute("clinicList");
    List<ServiceBean> serviceList = (List<ServiceBean>) request.getAttribute("serviceList");
    String msg = (String) request.getAttribute("msg");

    int selClinicId = request.getAttribute("selClinicId") != null ? (int) request.getAttribute("selClinicId") : 0;
    int selServiceId = request.getAttribute("selServiceId") != null ? (int) request.getAttribute("selServiceId") : 0;
    String selStatus = request.getAttribute("selStatus") != null ? String.valueOf(request.getAttribute("selStatus")) : "";
    int selMonth = request.getAttribute("selMonth") != null ? (int) request.getAttribute("selMonth") : 1;
    int selYear = request.getAttribute("selYear") != null ? (int) request.getAttribute("selYear") : 2026;

    int utilRate = request.getAttribute("utilRate") != null ? (int) request.getAttribute("utilRate") : 0;
    int noShow = request.getAttribute("noShow") != null ? (int) request.getAttribute("noShow") : 0;
    int totalSlots = request.getAttribute("totalSlots") != null ? (int) request.getAttribute("totalSlots") : 0;

    int userCount = request.getAttribute("userCount") != null ? (int) request.getAttribute("userCount") : 0;
    int apptCount = request.getAttribute("apptCount") != null ? (int) request.getAttribute("apptCount") : 0;
    int queueCount = request.getAttribute("queueCount") != null ? (int) request.getAttribute("queueCount") : 0;
    int notiCount = request.getAttribute("notiCount") != null ? (int) request.getAttribute("notiCount") : 0;
    List<Map<String, Object>> records = (List<Map<String, Object>>) request.getAttribute("records");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Reports</title>
</head>
<body>
    <h2>Reports</h2>

    <% if (msg != null) { %>
    <p><b><%= msg %></b></p>
    <% } %>

    <!-- simple calculation for report - filter form -->
    <h3>Utilisation Report Filter</h3>
    <form method="get" action="<%= request.getContextPath() %>/admin/reports">
        <table border="0" cellpadding="5">
            <tr>
                <td>Clinic:</td>
                <td>
                    <select name="clinicId">
                        <% if (clinicList != null) {
                            for (int i = 0; i < clinicList.size(); i++) {
                                ClinicBean c = clinicList.get(i);
                        %>
                        <option value="<%= c.getClinicId() %>" <%= c.getClinicId() == selClinicId ? "selected" : "" %>>
                            <%= c.getClinicName() %>
                        </option>
                        <% } } %>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Service:</td>
                <td>
                    <select name="serviceId">
                        <option value="0">All Services</option>
                        <% if (serviceList != null) {
                            for (int i = 0; i < serviceList.size(); i++) {
                                ServiceBean s = serviceList.get(i);
                        %>
                        <option value="<%= s.getServiceId() %>" <%= s.getServiceId() == selServiceId ? "selected" : "" %>><%= s.getServiceName() %></option>
                        <% } } %>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Month:</td>
                <td>
                    <select name="month">
                        <% for (int m = 1; m <= 12; m++) { %>
                        <option value="<%= m %>" <%= m == selMonth ? "selected" : "" %>><%= m %></option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Status:</td>
                <td>
                    <select name="status">
                        <option value="" <%= "".equals(selStatus) ? "selected" : "" %>>All</option>
                        <option value="BOOKED" <%= "BOOKED".equals(selStatus) ? "selected" : "" %>>BOOKED</option>
                        <option value="CONFIRMED" <%= "CONFIRMED".equals(selStatus) ? "selected" : "" %>>CONFIRMED</option>
                        <option value="ARRIVED" <%= "ARRIVED".equals(selStatus) ? "selected" : "" %>>ARRIVED</option>
                        <option value="COMPLETED" <%= "COMPLETED".equals(selStatus) ? "selected" : "" %>>COMPLETED</option>
                        <option value="NO_SHOW" <%= "NO_SHOW".equals(selStatus) ? "selected" : "" %>>NO_SHOW</option>
                        <option value="CANCELLED" <%= "CANCELLED".equals(selStatus) ? "selected" : "" %>>CANCELLED</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Year:</td>
                <td>
                    <select name="year">
                        <% for (int y = 2024; y <= 2027; y++) { %>
                        <option value="<%= y %>" <%= y == selYear ? "selected" : "" %>><%= y %></option>
                        <% } %>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Generate Report" /></td>
            </tr>
        </table>
    </form>

    <br/>

    <!-- simple calculation for report - utilisation results -->
    <h3>Utilisation Rate</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr><th>Item</th><th>Value</th><th>Visual</th></tr>
        <tr>
            <td>Total Available Slots</td>
            <td><%= totalSlots %></td>
            <td>-</td>
        </tr>
        <tr>
            <td>Utilisation Rate</td>
            <td><%= utilRate %> %</td>
            <td>
                <!-- simple bar: repeat | for each 5% -->
                <%
                    int bars = utilRate / 5;
                    for (int b = 0; b < bars; b++) {
                        out.print("|");
                    }
                    if (bars == 0) out.print("-");
                %>
                (<%= utilRate %>%)
            </td>
        </tr>
    </table>

    <!-- simple bar chart for utilisation rate -->
    <h3>Utilisation Rate Chart</h3>
    <div style="margin: 10px 0;">
        <p><b>Utilisation Rate: <%= utilRate %>%</b></p>
        <div style="background-color: #ddd; width: 300px; height: 30px; border: 1px solid #999;">
            <div style="background-color: #22a922; height: 100%; width: <%= utilRate %>%; display: inline-block; text-align: center; color: white; line-height: 30px; font-weight: bold;">
                <% if (utilRate > 10) { %><%= utilRate %>%<% } %>
            </div>
        </div>
    </div>

    <br/>

    <h3>Appointment Records</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>ID</th>
            <th>Patient</th>
            <th>Clinic</th>
            <th>Service</th>
            <th>Date</th>
            <th>Time</th>
            <th>Status</th>
        </tr>
        <% if (records != null && records.size() > 0) {
            for (int i = 0; i < records.size(); i++) {
                Map<String, Object> r = records.get(i);
        %>
        <tr>
            <td><%= r.get("appointmentId") %></td>
            <td><%= r.get("patientName") %></td>
            <td><%= r.get("clinicName") %></td>
            <td><%= r.get("serviceName") %></td>
            <td><%= r.get("appointmentDate") %></td>
            <td><%= r.get("timeSlot") %></td>
            <td><%= r.get("status") %></td>
        </tr>
        <%  }
           } else { %>
        <tr><td colspan="7">No appointment records for selected filter</td></tr>
        <% } %>
    </table>

    <br/>

    <!-- simple calculation for report - no-show results -->
    <h3>No-Show Summary</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr><th>Item</th><th>Value</th><th>Visual</th></tr>
        <tr>
            <td>No-Show Count</td>
            <td><%= noShow %></td>
            <td>
                <%
                    for (int b = 0; b < noShow && b < 20; b++) {
                        out.print("X");
                    }
                    if (noShow == 0) out.print("-");
                %>
            </td>
        </tr>
    </table>

    <!-- simple bar chart for no-show summary -->
    <h3>No-Show Rate Chart</h3>
    <div style="margin: 10px 0;">
        <p><b>No-Show Count: <%= noShow %></b></p>
        <% 
            // calculate no-show percentage if total slots available
            int noShowPercent = (totalSlots > 0) ? (noShow * 100 / totalSlots) : 0;
            // keep a small visible bar when noShow > 0
            int noShowBarWidth = noShowPercent;
            if (noShow > 0 && noShowBarWidth < 3) {
                noShowBarWidth = 3;
            }
        %>
        <div style="background-color: #ddd; width: 300px; height: 30px; border: 1px solid #999;">
            <div style="background-color: #cc0000; height: 100%; width: <%= noShowBarWidth %>%; display: inline-block; text-align: center; color: white; line-height: 30px; font-weight: bold;">
                <% if (noShowPercent > 10) { %><%= noShowPercent %>%<% } %>
            </div>
        </div>
    </div>

    <br/>

    <!-- overall summary -->
    <h3>Overall Summary</h3>
    <table border="1" cellpadding="5" cellspacing="0">
        <tr><th>Item</th><th>Count</th></tr>
        <tr><td>Total Users</td><td><%= userCount %></td></tr>
        <tr><td>Total Appointments</td><td><%= apptCount %></td></tr>
        <tr><td>Today Queue Entries</td><td><%= queueCount %></td></tr>
        <tr><td>Unread Notifications</td><td><%= notiCount %></td></tr>
    </table>

    <p>
        <a href="<%= request.getContextPath() %>/admin/users">User List</a> |
        <a href="<%= request.getContextPath() %>/admin/csv-import">Import CSV</a> |
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
