package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.ClinicDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.QueueEntryDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.dao.StaffProfileDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.ClinicBean;
import com.cchc.model.NotificationBean;
import com.cchc.model.QueueEntryBean;
import com.cchc.model.ServiceBean;
import com.cchc.model.StaffProfileBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceServlet extends HttpServlet {

    // simple attendance for staff
    private AppointmentDAO apptDao = new AppointmentDAO();
    private StaffProfileDAO staffDao = new StaffProfileDAO();
    private NotificationDAO nDao = new NotificationDAO();
    private QueueEntryDAO qDao = new QueueEntryDAO();
    private ServiceDAO serviceDao = new ServiceDAO();
    private ClinicDAO clinicDao = new ClinicDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = String.valueOf(session.getAttribute("loginRole"));
        if (!"STAFF".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");

        // get staff clinic from db
        StaffProfileBean profile = null;
        try {
            profile = staffDao.findByUserId(user.getUserId());
        } catch (SQLException e) {
            request.setAttribute("apptList", new java.util.ArrayList<AppointmentBean>());
            request.setAttribute("queueList", new java.util.ArrayList<QueueEntryBean>());
            request.setAttribute("serviceMap", new java.util.HashMap<Integer, ServiceBean>());
            request.setAttribute("selectedDate", LocalDate.now().toString());
            request.setAttribute("clinicName", "-");
            request.getRequestDispatcher("/staff/attendance.jsp").forward(request, response);
            return;
        }

        if (profile == null) {
            request.setAttribute("apptList", new java.util.ArrayList<AppointmentBean>());
            request.setAttribute("queueList", new java.util.ArrayList<QueueEntryBean>());
            request.setAttribute("serviceMap", new java.util.HashMap<Integer, ServiceBean>());
            request.setAttribute("clinicList", new java.util.ArrayList<ClinicBean>());
            request.setAttribute("selectedClinicId", 0);
            request.setAttribute("selectedDate", LocalDate.now().toString());
            request.setAttribute("clinicName", "-");
            request.getRequestDispatcher("/staff/attendance.jsp").forward(request, response);
            return;
        }

        int selectedClinicId = profile.getClinicId();
        String clinicIdStr = request.getParameter("clinicId");
        try {
            if (clinicIdStr != null && !clinicIdStr.trim().isEmpty()) {
                selectedClinicId = Integer.parseInt(clinicIdStr);
            }
        } catch (Exception e) {
            selectedClinicId = profile.getClinicId();
        }

        String selectedDate = request.getParameter("selectedDate");
        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            selectedDate = LocalDate.now().toString();
        }

        Date useDate;
        try {
            useDate = Date.valueOf(selectedDate);
        } catch (Exception e) {
            selectedDate = LocalDate.now().toString();
            useDate = Date.valueOf(selectedDate);
        }

        request.setAttribute("selectedDate", selectedDate);
        request.setAttribute("selectedClinicId", selectedClinicId);

        try {
            // load selected date appointments and queue for this clinic
            List<AppointmentBean> list = apptDao.findByClinicAndDate(selectedClinicId, useDate);
            List<QueueEntryBean> queueList = qDao.findWaitingByClinicAndDate(selectedClinicId, useDate);

            List<ServiceBean> services = serviceDao.findAll();
            Map<Integer, ServiceBean> serviceMap = new HashMap<>();
            for (int i = 0; i < services.size(); i++) {
                ServiceBean s = services.get(i);
                serviceMap.put(s.getServiceId(), s);
            }

            List<ClinicBean> clinicList = clinicDao.findAllActive();
            ClinicBean clinic = clinicDao.findById(selectedClinicId);

            request.setAttribute("apptList", list);
            request.setAttribute("queueList", queueList);
            request.setAttribute("serviceMap", serviceMap);
            request.setAttribute("clinicList", clinicList);
            request.setAttribute("clinicName", clinic != null ? clinic.getClinicName() : ("Clinic #" + selectedClinicId));
        } catch (SQLException e) {
            request.setAttribute("apptList", new java.util.ArrayList<AppointmentBean>());
            request.setAttribute("queueList", new java.util.ArrayList<QueueEntryBean>());
            request.setAttribute("serviceMap", new java.util.HashMap<Integer, ServiceBean>());
            request.setAttribute("clinicList", new java.util.ArrayList<ClinicBean>());
            request.setAttribute("clinicName", "Clinic #" + selectedClinicId);
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/staff/attendance.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // simple attendance for staff
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = String.valueOf(session.getAttribute("loginRole"));
        if (!"STAFF".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("appointmentId");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");
        String selectedDate = request.getParameter("selectedDate");
        String clinicId = request.getParameter("clinicId");

        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            id = 0;
        }

        // check status is one of the allowed values
        if (id <= 0 || status == null ||
                (!status.equals("ARRIVED") && !status.equals("COMPLETED") && !status.equals("NO_SHOW") && !status.equals("CANCELLED"))) {
            response.sendRedirect(request.getContextPath() + "/staff/attendance?msg=Invalid+input"
                    + "&selectedDate=" + (selectedDate == null ? "" : selectedDate)
                    + "&clinicId=" + (clinicId == null ? "" : clinicId));
            return;
        }

        try {
            AppointmentBean a = apptDao.findById(id);
            boolean ok;
            if ("CANCELLED".equals(status)) {
                ok = apptDao.cancelByClinic(id, reason);
            } else {
                ok = apptDao.updateStatus(id, status);
            }

            if (ok && a != null) {
                NotificationBean n = new NotificationBean();
                n.setUserId(a.getUserId());
                if ("CANCELLED".equals(status)) {
                    n.setTitle("Clinic Cancelled Appointment");
                    n.setMessage("Your appointment was cancelled by clinic. Reason: " + (reason == null || reason.isEmpty() ? "No reason" : reason));
                    n.setNotificationType("Appointment Cancelled");
                } else {
                    n.setTitle("Appointment Updated");
                    n.setMessage("Your appointment status is now: " + status);
                    n.setNotificationType("Appointment Updated");
                }
                n.setRelatedAppointmentId(id);
                n.setRead(false);
                nDao.create(n);
            }
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/staff/attendance?msg=Update+failed"
                    + "&selectedDate=" + (selectedDate == null ? "" : selectedDate)
                    + "&clinicId=" + (clinicId == null ? "" : clinicId));
            return;
        }

        response.sendRedirect(request.getContextPath() + "/staff/attendance?msg=Status+updated"
                + "&selectedDate=" + (selectedDate == null ? "" : selectedDate)
                + "&clinicId=" + (clinicId == null ? "" : clinicId));
    }
}
