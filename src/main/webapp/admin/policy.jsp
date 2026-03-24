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
</head>
<body>
    <h2>Policy Settings</h2>

    <% if (msg != null) { %>
    <p><%= msg %></p>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/admin/policy">
        <table border="1" cellpadding="5" cellspacing="0">
            <tr>
                <td>maxBookingsPerPatient</td>
                <td><input type="number" name="maxBookingsPerPatient" min="1" value="<%= max == null ? "3" : max %>" required /></td>
            </tr>
            <tr>
                <td>cancellationCutoffHours</td>
                <td><input type="number" name="cancellationCutoffHours" min="1" value="<%= cut == null ? "24" : cut %>" required /></td>
            </tr>
            <tr>
                <td>queueEnabled (Global)</td>
                <td>
                    <select name="queueEnabled">
                        <option value="1" <%= "1".equals(queueEnabled) ? "selected" : "" %>>Enabled</option>
                        <option value="0" <%= "0".equals(queueEnabled) ? "selected" : "" %>>Disabled</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Clinic Queue Toggle</td>
                <td>
                    <select name="queueClinicId">
                        <option value="">No clinic update</option>
                        <% if (clinicList != null) {
                            for (int i = 0; i < clinicList.size(); i++) {
                                ClinicBean c = clinicList.get(i);
                        %>
                        <option value="<%= c.getClinicId() %>"><%= c.getClinicName() %></option>
                        <% } } %>
                    </select>
                    <select name="clinicQueueEnabled">
                        <option value="1">Enable</option>
                        <option value="0">Disable</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Save" /></td>
            </tr>
        </table>
    </form>

    <p>
        <a href="<%= request.getContextPath() %>/admin/dashboard.jsp">Back Dashboard</a> |
        <a href="<%= request.getContextPath() %>/logout">Logout</a>
    </p>
</body>
</html>
