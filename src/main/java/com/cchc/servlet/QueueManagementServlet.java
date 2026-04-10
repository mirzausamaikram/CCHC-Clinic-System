package com.cchc.servlet;

import com.cchc.dao.ClinicServiceDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.QueueEntryDAO;
import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.dao.StaffProfileDAO;
import com.cchc.dao.UserDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.ClinicServiceBean;
import com.cchc.model.NotificationBean;
import com.cchc.model.PatientProfileBean;
import com.cchc.model.QueueEntryBean;
import com.cchc.model.ServiceBean;
import com.cchc.model.StaffProfileBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "QueueManagementServlet", urlPatterns = {"/staff/queue"})
public class QueueManagementServlet extends HttpServlet {

    private StaffProfileDAO sDao = new StaffProfileDAO();
    private QueueEntryDAO qDao = new QueueEntryDAO();
    private ClinicServiceDAO csDao = new ClinicServiceDAO();
    private ServiceDAO serDao = new ServiceDAO();
    private PatientProfileDAO pDao = new PatientProfileDAO();
    private NotificationDAO nDao = new NotificationDAO();
    private UserDAO uDao = new UserDAO();
    private AppointmentDAO apptDao = new AppointmentDAO();

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
            StaffProfileBean staff = sDao.findByUserId(user.getUserId());
            if (staff == null) {
                request.setAttribute("errorMessage", "Staff profile not found.");
                request.getRequestDispatcher("/staff/queue-management.jsp").forward(request, response);
                return;
            }

            Date d = new Date(System.currentTimeMillis());
            List<QueueEntryBean> qList = qDao.findWaitingByClinicAndDate(staff.getClinicId(), d);
            List<ClinicServiceBean> csList = csDao.findByClinicId(staff.getClinicId());
            List<PatientProfileBean> pList = pDao.findAll();
            List<UserBean> uList = uDao.findActiveByRoleName("PATIENT");
            List<ServiceBean> sList = serDao.findAllActive();
            List<AppointmentBean> bList = apptDao.getTodayAppointments(staff.getClinicId());

            Map<Integer, ServiceBean> sMap = new HashMap<>();
            for (int i = 0; i < sList.size(); i++) {
                ServiceBean s = sList.get(i);
                sMap.put(s.getServiceId(), s);
            }

            Map<Integer, PatientProfileBean> pMap = new HashMap<>();
            for (int i = 0; i < pList.size(); i++) {
                PatientProfileBean p = pList.get(i);
                pMap.put(p.getPatientId(), p);
            }

            Map<Integer, UserBean> uMap = new HashMap<>();
            for (int i = 0; i < uList.size(); i++) {
                UserBean u = uList.get(i);
                uMap.put(u.getUserId(), u);
            }

            request.setAttribute("staffProfile", staff);
            request.setAttribute("queueDate", d);
            request.setAttribute("queueList", qList);
            request.setAttribute("clinicServices", csList);
            request.setAttribute("patients", pList);
            request.setAttribute("patientUsers", uList);
            request.setAttribute("allServices", sList);
            request.setAttribute("todayBookings", bList);
            request.setAttribute("serviceMap", sMap);
            request.setAttribute("patientMap", pMap);
            request.setAttribute("userMap", uMap);
            request.getRequestDispatcher("/staff/queue-management.jsp").forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Queue data is not available now");
            request.getRequestDispatcher("/staff/queue-management.jsp").forward(request, response);
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
        if (!"STAFF".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");
        String act = request.getParameter("action");

        try {
            StaffProfileBean staff = sDao.findByUserId(user.getUserId());
            if (staff == null) {
                request.setAttribute("errorMessage", "Staff profile not found.");
                doGet(request, response);
                return;
            }

            if ("addWalkIn".equals(act)) {
                int userId = 0;
                int sid = 0;
                int pri = 0;
                try { userId = Integer.parseInt(request.getParameter("patientId")); } catch (Exception e) { userId = 0; }
                try { sid = Integer.parseInt(request.getParameter("clinicServiceId")); } catch (Exception e) { sid = 0; }
                try { pri = Integer.parseInt(request.getParameter("priorityLevel")); } catch (Exception e) { pri = 0; }

                if (userId > 0 && sid > 0) {
                    PatientProfileBean p = pDao.findByUserId(userId);
                    ServiceBean s = serDao.findById(sid);
                    if (p != null && s != null) {
                        Date d = new Date(System.currentTimeMillis());

                        boolean hasTicket = qDao.hasActiveTicket(userId, staff.getClinicId(), sid, d);
                        if (hasTicket) {
                            response.sendRedirect(request.getContextPath() + "/staff/queue?err=Patient+already+has+active+queue+today");
                            return;
                        }

                        int token = qDao.getNextTokenNo(staff.getClinicId(), d);

                        QueueEntryBean q = new QueueEntryBean();
                        q.setClinicId(staff.getClinicId());
                        q.setServiceId(sid);
                        q.setPatientId(p.getPatientId());
                        q.setAppointmentId(null);
                        q.setQueueDate(d);
                        q.setTokenNo(token);
                        q.setPriorityLevel(pri);
                        q.setQueueStatus("WAITING");
                        int qid = qDao.create(q);

                        if (qid > 0) {
                            NotificationBean n = new NotificationBean();
                            n.setUserId(p.getUserId());
                            n.setTitle("Queue Token Issued");
                            n.setMessage("Token: " + token);
                            n.setNotificationType("QUEUE");
                            n.setRelatedAppointmentId(null);
                            n.setRead(false);
                            nDao.create(n);
                        }
                    }
                }
            } else if ("updateStatus".equals(act)) {
                int qid = 0;
                int pid = 0;
                try { qid = Integer.parseInt(request.getParameter("queueId")); } catch (Exception e) { qid = 0; }
                try { pid = Integer.parseInt(request.getParameter("patientId")); } catch (Exception e) { pid = 0; }
                String st = request.getParameter("queueStatus");

                if (qid > 0 && pid > 0 && st != null && !"".equals(st)) {
                    boolean ok = qDao.updateQueueStatus(qid, st);
                    if (ok) {
                        PatientProfileBean p = pDao.findById(pid);
                        if (p != null) {
                            NotificationBean n = new NotificationBean();
                            n.setUserId(p.getUserId());
                            n.setTitle("Queue Status Updated");
                            n.setMessage("Status: " + st);
                            n.setNotificationType("QUEUE");
                            n.setRelatedAppointmentId(null);
                            n.setRead(false);
                            nDao.create(n);
                        }
                    }
                }
            }

            response.sendRedirect(request.getContextPath() + "/staff/queue?success=1");
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Cannot save queue action");
            doGet(request, response);
        }
    }
}
