<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.cchc.model.ClinicBean"%>
<%@page import="com.cchc.model.ServiceBean"%>
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
    if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<ClinicBean> clinics = (List<ClinicBean>) request.getAttribute("clinics");
    List<ServiceBean> services = (List<ServiceBean>) request.getAttribute("services");
    Integer cid = (Integer) request.getAttribute("selectedClinicId");
    if (cid == null) {
        cid = 0;
    }
    String selectedDate = (String) request.getAttribute("selectedDate");
    if (selectedDate == null || selectedDate.isEmpty()) {
        selectedDate = java.time.LocalDate.now().toString();
    }
    List<String> slots = (List<String>) request.getAttribute("availableSlots");
    if (slots == null) {
        slots = new ArrayList<>();
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book Appointment</title>
    </head>
    <body>
        <h2>Book Appointment</h2>
        <p>Welcome, <cchc:username /></p>

        <% if (request.getAttribute("errorMessage") != null) { %>
            <p style="color:red;"><%= request.getAttribute("errorMessage") %></p>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <p style="color:red;"><%= request.getAttribute("error") %></p>
        <% } %>

        <%-- Step 1: pick clinic + date + service, click Load Available Slots --%>
        <form method="get" action="<%= request.getContextPath() %>/patient/appointments">
            <input type="hidden" name="action" value="book" />
            <table border="0" cellpadding="4">
                <tr>
                    <td><b>Clinic</b></td>
                    <td>
                        <select name="clinicId" onchange="this.form.submit()">
                            <option value="">-- Select Clinic --</option>
                            <%
                                List xClinics = (List) request.getAttribute("clinics");
                                if (xClinics != null) {
                                    for (Object obj : xClinics) {
                                        ClinicBean clinic = (ClinicBean) obj;
                            %>
                            <option value="<%= clinic.getClinicId() %>" <%= clinic.getClinicId() == cid ? "selected" : "" %>><%= clinic.getClinicName() %></option>
                            <%      }
                                }
                            %>
                        </select>
                    </td>
                    <td><b>Date</b></td>
                    <td><input type="date" name="appointmentDate" value="<%= selectedDate %>" /></td>
                </tr>
                <tr>
                    <td><b>Service</b></td>
                    <td colspan="3">
                        <select name="selectedService">
                            <option value="">-- Select Service --</option>
                            <%
                                List xServices = (List) request.getAttribute("services");
                                if (xServices != null) {
                                    for (Object obj : xServices) {
                                        ServiceBean service = (ServiceBean) obj;
                            %>
                            <option value="<%= service.getServiceId() %>"><%= service.getServiceName() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="4"><input type="submit" value="Load Available Slots" /></td>
                </tr>
            </table>
        </form>

        <hr/>

        <%-- Step 2: pick timeslot and book --%>
        <form method="post" action="<%= request.getContextPath() %>/patient/appointments">
            <input type="hidden" name="clinicId" value="<%= cid %>" />
            <input type="hidden" name="appointmentDate" value="<%= selectedDate %>" />
            <%
                // carry the service chosen in step 1
                String selSvc = request.getParameter("selectedService");
                if (selSvc == null) selSvc = "";
            %>
            <input type="hidden" name="clinicServiceId" value="<%= selSvc %>" />
            <table border="0" cellpadding="4">
                <tr>
                    <td><b>Available Timeslot</b></td>
                    <td>
                        <select name="timeSlot" required>
                            <option value="">-- Select Slot --</option>
                            <% for (int i = 0; i < slots.size(); i++) { %>
                                <option value="<%= slots.get(i) %>"><%= slots.get(i) %></option>
                            <% } %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td><b>Notes</b></td>
                    <td><textarea name="notes" rows="3" cols="40"></textarea></td>
                </tr>
                <tr>
                    <td colspan="2"><input type="submit" value="Book Appointment" /></td>
                </tr>
            </table>
        </form>

        <hr/>
        <p>
            <a href="<%= request.getContextPath() %>/patient/appointments?action=list">My Appointments</a> |
            <a href="<%= request.getContextPath() %>/patient/queue-status">My Queue Status</a> |
            <a href="<%= request.getContextPath() %>/notifications">My Notifications</a> |
            <a href="<%= request.getContextPath() %>/patient/dashboard.jsp">Back Dashboard</a> |
            <a href="<%= request.getContextPath() %>/logout">Logout</a>
        </p>
    </body>
</html>
