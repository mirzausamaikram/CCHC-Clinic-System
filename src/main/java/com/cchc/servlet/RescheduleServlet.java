package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.SystemSettingDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Set;

public class RescheduleServlet extends HttpServlet {

    private AppointmentDAO dao = new AppointmentDAO();
    private SystemSettingDAO setDao = new SystemSettingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // simple reschedule for patient
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = String.valueOf(session.getAttribute("loginRole"));
        if (!"PATIENT".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");
        String idStr = request.getParameter("appointmentId");
        int id = 0;

        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            id = 0;
        }

        AppointmentBean appt = null;
        if (id > 0) {
            try {
                List<AppointmentBean> list = dao.getMyAppointments(user.getUserId());
                for (int i = 0; i < list.size(); i++) {
                    AppointmentBean one = list.get(i);
                    if (one.getAppointmentId() == id) {
                        appt = one;
                        break;
                    }
                }
            } catch (SQLException e) {
                request.setAttribute("msg", "Error loading booking");
            }
        }

        if (appt == null) {
            request.setAttribute("msg", "Booking not found");
        }

        request.setAttribute("appt", appt);
        request.getRequestDispatcher("/patient/reschedule.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // simple reschedule for patient
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = String.valueOf(session.getAttribute("loginRole"));
        if (!"PATIENT".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");

        String idStr = request.getParameter("appointmentId");
        String dateStr = request.getParameter("newDate");
        String slot = request.getParameter("timeSlot");

        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            id = 0;
        }

        if (id <= 0 || dateStr == null || dateStr.isEmpty() || slot == null || slot.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Invalid+input");
            return;
        }

        String[] parts = slot.split("-");
        if (parts.length != 2) {
            response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Invalid+time+slot");
            return;
        }

        try {
            Date newDate = Date.valueOf(dateStr);
            Time newStart = Time.valueOf(parts[0] + ":00");
            Time newEnd = Time.valueOf(parts[1] + ":00");

            AppointmentBean current = dao.findById(id);
            if (current == null || current.getUserId() != user.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Booking+not+found");
                return;
            }

            int cutHours = 24;
            try {
                String x = setDao.getValue("cancellationCutoffHours");
                if (x != null && !x.isEmpty()) {
                    cutHours = Integer.parseInt(x);
                }
            } catch (Exception ex) {
                cutHours = 24;
            }

            if (current.getAppointmentDate() != null && current.getStartTime() != null) {
                java.time.LocalDateTime oldAppt = java.time.LocalDateTime.of(current.getAppointmentDate(), current.getStartTime().toLocalTime());
                long hrs = java.time.Duration.between(java.time.LocalDateTime.now(), oldAppt).toHours();
                if (hrs < cutHours) {
                    response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Reschedule+not+allowed+(cutoff+rule)");
                    return;
                }
            }

            Set<String> booked = dao.getBookedStartTimes(current.getClinicId(), newDate);
            String newStartStr = parts[0];
            String oldStartStr = current.getStartTime() != null ? current.getStartTime().toString().substring(0, 5) : "";
            if (!newStartStr.equals(oldStartStr) && booked.contains(newStartStr)) {
                response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Slot+already+taken");
                return;
            }

            boolean ok = dao.updateAppointmentDateAndTime(id, user.getUserId(), newDate, newStart, newEnd);
            if (ok) {
                response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Appointment+rescheduled");
            } else {
                response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Reschedule+failed");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/patient/my-bookings?msg=Error+rescheduling");
        }
    }
}