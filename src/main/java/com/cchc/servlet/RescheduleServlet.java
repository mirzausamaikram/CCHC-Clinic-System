package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
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

public class RescheduleServlet extends HttpServlet {

    private AppointmentDAO dao = new AppointmentDAO();

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