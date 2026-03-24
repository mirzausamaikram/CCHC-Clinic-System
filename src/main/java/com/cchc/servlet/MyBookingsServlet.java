package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.SystemSettingDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.NotificationBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "MyBookingsServlet", urlPatterns = {"/patient/my-bookings"})
public class MyBookingsServlet extends HttpServlet {

    private AppointmentDAO dao = new AppointmentDAO();
    private NotificationDAO nDao = new NotificationDAO();
    private SystemSettingDAO setDao = new SystemSettingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // check if user is logged in
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

        try {
            // get data from database
            List<AppointmentBean> apptList = dao.getMyAppointments(user.getUserId());
            String msg = request.getParameter("msg");
            request.setAttribute("apptList", apptList);
            if (msg != null && !msg.isEmpty()) {
                request.setAttribute("msg", msg);
            }
            request.getRequestDispatcher("/patient/my_bookings.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("msg", "Error loading bookings");
            request.getRequestDispatcher("/patient/my_bookings.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
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

        if (id > 0) {
            try {
                AppointmentBean a = dao.findById(id);
                int cutHours = 24;
                try {
                    String x = setDao.getValue("cancellationCutoffHours");
                    if (x != null && !x.isEmpty()) {
                        cutHours = Integer.parseInt(x);
                    }
                } catch (Exception ex) {
                    cutHours = 24;
                }

                boolean allow = true;
                if (a != null && a.getAppointmentDate() != null && a.getStartTime() != null) {
                    java.time.LocalDateTime apptTime = java.time.LocalDateTime.of(a.getAppointmentDate(), a.getStartTime().toLocalTime());
                    java.time.LocalDateTime now = java.time.LocalDateTime.now();
                    long hrs = java.time.Duration.between(now, apptTime).toHours();
                    if (hrs < cutHours) {
                        allow = false;
                    }
                }

                if (!allow) {
                    request.getSession().setAttribute("msg", "Cancellation not allowed (cutoff rule)");
                    response.sendRedirect(request.getContextPath() + "/my_appointments");
                    return;
                }

                dao.cancelAppointment(id, user.getUserId());
                // simple notification system - appointment cancelled
                NotificationBean nb = new NotificationBean();
                nb.setUserId(user.getUserId());
                nb.setTitle("Appointment Cancelled");
                nb.setMessage("Your appointment has been cancelled.");
                nb.setNotificationType("Appointment Cancelled");
                nb.setRelatedAppointmentId(id);
                nb.setRead(false);
                nDao.create(nb);
            } catch (SQLException e) {
                // ignore for now
            }
        }

        request.getSession().setAttribute("msg", "Appointment cancelled");
        response.sendRedirect(request.getContextPath() + "/my_appointments");
    }
}
