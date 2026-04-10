package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.dao.StaffProfileDAO;
import com.cchc.dao.UserDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.NotificationBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApprovalServlet extends HttpServlet {

    private AppointmentDAO apptDao = new AppointmentDAO();
    private StaffProfileDAO staffDao = new StaffProfileDAO();
    private ServiceDAO sDao = new ServiceDAO();
    private UserDAO uDao = new UserDAO();
    private NotificationDAO nDao = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"STAFF".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");

        try {
            StaffProfileBean staff = staffDao.findByUserId(user.getUserId());
            if (staff == null) {
                request.setAttribute("pendingList", new java.util.ArrayList<AppointmentBean>());
                request.setAttribute("userMap", new java.util.HashMap<Integer, UserBean>());
                request.setAttribute("serviceMap", new java.util.HashMap<Integer, ServiceBean>());
                request.getRequestDispatcher("/staff/approval.jsp").forward(request, response);
                return;
            }

            List<AppointmentBean> list = apptDao.getAllBookingsForStaff(staff.getClinicId());
            List<ServiceBean> sList = sDao.findAllActive();
            List<UserBean> uList = uDao.findActiveByRoleName("PATIENT");

            Map<Integer, UserBean> uMap = new HashMap<>();
            for (int i = 0; i < uList.size(); i++) {
                UserBean u = uList.get(i);
                uMap.put(u.getUserId(), u);
            }

            Map<Integer, ServiceBean> sMap = new HashMap<>();
            for (int i = 0; i < sList.size(); i++) {
                ServiceBean s = sList.get(i);
                sMap.put(s.getServiceId(), s);
            }

            request.setAttribute("pendingList", list);
            request.setAttribute("userMap", uMap);
            request.setAttribute("serviceMap", sMap);

            String msg = request.getParameter("msg");
            if (msg != null && !msg.isEmpty()) {
                request.setAttribute("msg", msg);
            }
        } catch (SQLException e) {
            request.setAttribute("pendingList", new java.util.ArrayList<AppointmentBean>());
            request.setAttribute("userMap", new java.util.HashMap<Integer, UserBean>());
            request.setAttribute("serviceMap", new java.util.HashMap<Integer, ServiceBean>());
        }

        request.getRequestDispatcher("/staff/approval.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"STAFF".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idStr = request.getParameter("appointmentId");
        String action = request.getParameter("action");
        String reason = request.getParameter("reason");

        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            id = 0;
        }

        if (id <= 0 || action == null || action.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/approval?msg=Invalid+input");
            return;
        }

        try {
            AppointmentBean a = apptDao.findById(id);
            if (a == null) {
                response.sendRedirect(request.getContextPath() + "/staff/approval?msg=Booking+not+found");
                return;
            }

            int patientUserId = a.getUserId();
            if ("approve".equals(action)) {
                int quota = sDao.getQuotaPerSlot(a.getServiceId());
                int used = apptDao.countByClinicServiceDateAndSlotExcluding(
                        a.getClinicId(),
                        a.getServiceId(),
                        Date.valueOf(a.getAppointmentDate()),
                        a.getTimeSlot(),
                        a.getAppointmentId()
                );
                if (used >= quota) {
                    response.sendRedirect(request.getContextPath() + "/staff/approval?msg=Quota+full+for+this+slot");
                    return;
                }

                boolean ok = apptDao.approveBooking(id);
                if (ok) {
                    NotificationBean n = new NotificationBean();
                    n.setUserId(patientUserId);
                    n.setTitle("Appointment Confirmed");
                    n.setMessage("Your booking has been approved.");
                    n.setNotificationType("Appointment Confirmed");
                    n.setRelatedAppointmentId(id);
                    n.setRead(false);
                    nDao.create(n);
                }
                response.sendRedirect(request.getContextPath() + "/staff/approval?msg=Booking+approved");
            } else if ("reject".equals(action)) {
                boolean ok = apptDao.rejectBooking(id, reason);
                if (ok) {
                    NotificationBean n = new NotificationBean();
                    n.setUserId(patientUserId);
                    n.setTitle("Booking Rejected");
                    n.setMessage("Your booking was rejected. Reason: " + (reason == null ? "No reason" : reason));
                    n.setNotificationType("Appointment Cancelled");
                    n.setRelatedAppointmentId(id);
                    n.setRead(false);
                    nDao.create(n);
                }
                response.sendRedirect(request.getContextPath() + "/staff/approval?msg=Booking+rejected");
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/approval?msg=Unknown+action");
            }
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/staff/approval?msg=Action+failed");
        }
    }
}
