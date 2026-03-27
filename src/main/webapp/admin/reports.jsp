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
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
<div class="page">
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

    <!-- Utilisation Dashboard -->
    <h3>Utilisation Rate</h3>
    <div class="grid" style="align-items:stretch;">
        <div class="panel" style="text-align:center;">
            <p class="muted" style="margin-bottom:8px;">Slot Utilisation</p>
            <svg width="160" height="160" viewBox="0 0 160 160">
                <!-- background track -->
                <circle cx="80" cy="80" r="60" fill="none" stroke="var(--border)" stroke-width="16"/>
                <!-- animated fill arc -->
                <circle cx="80" cy="80" r="60" fill="none"
                        stroke="var(--brand)" stroke-width="16"
                        stroke-linecap="round"
                        stroke-dasharray="376.99"
                        stroke-dashoffset="376.99"
                        transform="rotate(-90 80 80)"
                        id="utilArc"
                        style="transition: stroke-dashoffset 1.2s cubic-bezier(.4,0,.2,1);"/>
                <text x="80" y="86" text-anchor="middle" font-size="26" font-weight="700" fill="var(--brand)" id="utilLabel">0%</text>
            </svg>
            <p class="muted" style="margin:4px 0 0;">of slots utilised</p>
        </div>
        <div style="display:flex; flex-direction:column; gap:12px; flex:1; align-self:stretch;">
            <div class="stat" style="flex:1; display:flex; flex-direction:column; justify-content:center;">
                <p class="muted">Total Available Slots</p>
                <p class="value"><%= totalSlots %></p>
            </div>
            <div class="stat" style="flex:1; display:flex; flex-direction:column; justify-content:center;">
                <p class="muted">Utilisation Rate</p>
                <p class="value"><%= utilRate %>%</p>
            </div>
            <div class="stat" style="flex:1; display:flex; flex-direction:column; justify-content:center;">
                <p class="muted">No-Show Count</p>
                <p class="value" style="color:var(--danger);"><%= noShow %></p>
            </div>
        </div>
    </div>
    <script>
        (function(){
            var rate = <%= utilRate %>;
            var circ = 376.99;
            var offset = circ * (1 - rate / 100);
            window.addEventListener('load', function(){
                var arc = document.getElementById('utilArc');
                var lbl = document.getElementById('utilLabel');
                if (arc) arc.style.strokeDashoffset = offset;
                if (lbl) lbl.textContent = rate + '%';
            });
        })();
    </script>

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

    <!-- No-Show Summary -->
    <h3>No-Show Summary</h3>
    <div class="grid" style="align-items:stretch;">
        <div class="panel" style="text-align:center;">
            <p class="muted" style="margin-bottom:8px;">No-Show Count</p>
            <svg width="160" height="160" viewBox="0 0 160 160">
                <circle cx="80" cy="80" r="60" fill="none" stroke="var(--border)" stroke-width="16"/>
                <circle cx="80" cy="80" r="60" fill="none"
                        stroke="var(--danger)" stroke-width="16"
                        stroke-linecap="round"
                        stroke-dasharray="376.99"
                        stroke-dashoffset="376.99"
                        transform="rotate(-90 80 80)"
                        id="noShowArc"
                        style="transition: stroke-dashoffset 1.2s cubic-bezier(.4,0,.2,1);"/>
                <text x="80" y="86" text-anchor="middle" font-size="26" font-weight="700" fill="var(--danger)" id="noShowLabel">0</text>
            </svg>
            <p class="muted" style="margin:4px 0 0;">no-shows</p>
        </div>
        <div style="display:flex; flex-direction:column; gap:12px; flex:1; align-self:stretch;">
            <div class="stat" style="flex:1; display:flex; flex-direction:column; justify-content:center;">
                <p class="muted">No-Show Count</p>
                <p class="value" style="color:var(--danger);"><%= noShow %></p>
            </div>
            <div class="stat" style="flex:1; display:flex; flex-direction:column; justify-content:center;">
                <p class="muted">Total Slots</p>
                <p class="value"><%= totalSlots %></p>
            </div>
            <div class="stat" style="flex:1; display:flex; flex-direction:column; justify-content:center;">
                <p class="muted">No-Show Rate</p>
                <%
                    int noShowPct = (totalSlots > 0) ? (noShow * 100 / totalSlots) : 0;
                %>
                <p class="value" style="color:var(--danger);"><%= noShowPct %>%</p>
            </div>
        </div>
    </div>
    <script>
        (function(){
            var count = <%= noShow %>;
            var maxCount = 20;
            var circ = 376.99;
            // arc fills based on count out of maxCount (capped at full circle)
            var fraction = Math.min(count / maxCount, 1);
            var offset = circ * (1 - fraction);
            window.addEventListener('load', function(){
                var arc = document.getElementById('noShowArc');
                var lbl = document.getElementById('noShowLabel');
                if (arc) arc.style.strokeDashoffset = offset;
                if (lbl) lbl.textContent = count;
            });
        })();
    </script>

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

</div>
</body>
</html>

