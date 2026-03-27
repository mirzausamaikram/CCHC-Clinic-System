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
    // simple past-slot check: disable slots before now when date is today
    boolean isToday = Boolean.TRUE.equals(request.getAttribute("isToday"));
    String currentTime = (String) request.getAttribute("currentTime");
    if (currentTime == null) currentTime = "00:00";
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book Appointment</title>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/app.css">
    </head>
    <body>
    <%@ include file="/WEB-INF/jspf/role_navbar.jspf" %>
    <div class="page">
        <div class="panel">
            <h2>Book Appointment</h2>

            <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="notice error"><%= request.getAttribute("errorMessage") %></div>
            <% } %>
            <% if (request.getAttribute("error") != null) { %>
                <div class="notice error"><%= request.getAttribute("error") %></div>
            <% } %>

            <%-- Step 1: pick clinic + date + service --%>
            <h3>Step 1 &mdash; Select Clinic, Date &amp; Service</h3>
            <form method="get" action="<%= request.getContextPath() %>/patient/appointments">
                <input type="hidden" name="action" value="book" />
                <div class="form-grid">
                    <div class="field">
                        <label>Clinic</label>
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
                    </div>
                    <div class="field">
                        <label>Date</label>
                        <input type="date" name="appointmentDate" value="<%= selectedDate %>" min="<%= java.time.LocalDate.now().toString() %>" onchange="this.form.submit()" />
                    </div>
                    <div class="field">
                        <label>Service</label>
                        <select name="selectedService" onchange="this.form.submit()">
                            <option value="">-- Select Service --</option>
                            <%
                                List xServices = (List) request.getAttribute("services");
                                String prevSvc = request.getParameter("selectedService");
                                if (xServices != null) {
                                    for (Object obj : xServices) {
                                        ServiceBean service = (ServiceBean) obj;
                                        String sId = String.valueOf(service.getServiceId());
                            %>
                            <option value="<%= sId %>" <%= sId.equals(prevSvc) ? "selected" : "" %>><%= service.getServiceName() %></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                </div>
                <div style="margin-top:14px;">
                    <input type="submit" value="Load Available Slots" />
                </div>
            </form>

            <%-- Step 2: pick timeslot and book --%>
            <h3 style="margin-top:24px;">Step 2 &mdash; Choose a Timeslot</h3>
            <form method="post" action="<%= request.getContextPath() %>/patient/appointments">
                <input type="hidden" name="clinicId" value="<%= cid %>" />
                <input type="hidden" name="appointmentDate" value="<%= selectedDate %>" />
                <%
                    // carry the service selected in step 1
                    String selSvc = request.getParameter("selectedService");
                    if (selSvc == null) selSvc = "";
                %>
                <input type="hidden" name="clinicServiceId" value="<%= selSvc %>" />
                <div class="form-grid">
                    <div class="field">
                        <label>Available Timeslot</label>
                        <select name="timeSlot" required>
                            <option value="">-- Select Slot --</option>
                            <% for (int i = 0; i < slots.size(); i++) {
                                String slot = slots.get(i);
                                boolean isPast = isToday && slot.compareTo(currentTime) <= 0;
                            %>
                                <option value="<%= slot %>" <%= isPast ? "disabled" : "" %>><%= slot %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="field">
                        <label>Notes</label>
                        <textarea name="notes" rows="3"></textarea>
                    </div>
                </div>
                <div style="margin-top:14px;">
                    <input type="submit" value="Book Appointment" />
                </div>
            </form>
        </div>
    </div>
    </body>
</html>

