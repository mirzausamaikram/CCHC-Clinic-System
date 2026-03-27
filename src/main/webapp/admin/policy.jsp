<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.cchc.model.UserBean"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="java.util.List"%>
<%
    UserBean user = (UserBean) session.getAttribute("loginUser");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String max = (String) request.getAttribute("maxBookingsPerPatient");
    String cut = (String) request.getAttribute("cancellationCutoffHours");
    String queueEnabled = (String) request.getAttribute("queueEnabled");
    List<ClinicBean> clinicList = (List<ClinicBean>) request.getAttribute("clinicList");
    String msg = (String) request.getAttribute("msg");
%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Policy Settings</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
<div class="page">
    <h2>Policy Settings</h2>

    <% if (msg != null) { %>
    <div class="notice success"><%= msg %></div>
    <% } %>

    <div class="panel">
        <form method="post" action="<%= request.getContextPath() %>/admin/policy">
            <div class="form-grid">
                <div class="field">
                    <label>Max Bookings Per Patient</label>
                    <input type="number" name="maxBookingsPerPatient" min="1" value="<%= max == null ? "3" : max %>" required />
                </div>
                <div class="field">
                    <label>Cancellation Cutoff (hours)</label>
                    <input type="number" name="cancellationCutoffHours" min="1" value="<%= cut == null ? "24" : cut %>" required />
                </div>
                <div class="field">
                    <label>Queue Enabled (Global)</label>
                    <select name="queueEnabled">
                        <option value="1" <%= "1".equals(queueEnabled) ? "selected" : "" %>>Enabled</option>
                        <option value="0" <%= "0".equals(queueEnabled) ? "selected" : "" %>>Disabled</option>
                    </select>
                </div>
            </div>
            <div class="form-grid" style="margin-top:16px;">
                <div class="field">
                    <label>Clinic Queue Toggle &mdash; Select Clinic</label>
                    <select name="queueClinicId">
                        <option value="">No clinic update</option>
                        <% if (clinicList != null) {
                            for (int i = 0; i < clinicList.size(); i++) {
                                ClinicBean c = clinicList.get(i);
                        %>
                        <option value="<%= c.getClinicId() %>"><%= c.getClinicName() %></option>
                        <% } } %>
                    </select>
                </div>
                <div class="field">
                    <label>Clinic Queue Status</label>
                    <select name="clinicQueueEnabled">
                        <option value="1">Enable</option>
                        <option value="0">Disable</option>
                    </select>
                </div>
            </div>
            <div style="margin-top:18px;">
                <input type="submit" value="Save Settings" />
            </div>
        </form>
    </div>

</div>
</body>
</html>

