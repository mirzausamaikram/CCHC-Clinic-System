package com.cchc.servlet;

import com.cchc.dao.ClinicServiceDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.QueueEntryDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.dao.StaffProfileDAO;
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
import java.sql.SQLException;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // simple check
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

            Map<Integer, ServiceBean> sMap = new HashMap<>();
            List<ServiceBean> x = serDao.findAllActive();
            for (int i = 0; i < x.size(); i++) {
                ServiceBean s = x.get(i);
                sMap.put(s.getServiceId(), s);
            }

            Map<Integer, PatientProfileBean> pMap = new HashMap<>();
            for (int i = 0; i < pList.size(); i++) {
                PatientProfileBean p = pList.get(i);
                pMap.put(p.getPatientId(), p);
            }

            request.setAttribute("staffProfile", staff);
            request.setAttribute("queueDate", d);
            request.setAttribute("queueList", qList);
            request.setAttribute("clinicServices", csList);
            request.setAttribute("patients", pList);
            request.setAttribute("serviceMap", sMap);
            request.setAttribute("patientMap", pMap);
            request.getRequestDispatcher("/staff/queue-management.jsp").forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Database error");
            request.getRequestDispatcher("/staff/queue-management.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO later
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
                int pid = 0;
                int csid = 0;
                int pri = 0;
                try { pid = Integer.parseInt(request.getParameter("patientId")); } catch (Exception e) { pid = 0; }
                try { csid = Integer.parseInt(request.getParameter("clinicServiceId")); } catch (Exception e) { csid = 0; }
                try { pri = Integer.parseInt(request.getParameter("priorityLevel")); } catch (Exception e) { pri = 0; }

                if (pid > 0 && csid > 0) {
                    ClinicServiceBean cs = csDao.findById(csid);
                    if (cs != null && cs.getClinicId() == staff.getClinicId()) {
                        Date d = new Date(System.currentTimeMillis());
                        int token = qDao.getNextTokenNo(staff.getClinicId(), d);

                        QueueEntryBean q = new QueueEntryBean();
                        q.setClinicId(staff.getClinicId());
                        q.setServiceId(cs.getServiceId());
                        q.setPatientId(pid);
                        q.setAppointmentId(null);
                        q.setQueueDate(d);
                        q.setTokenNo(token);
                        q.setPriorityLevel(pri);
                        q.setQueueStatus("WAITING");
                        int qid = qDao.create(q);

                        if (qid > 0) {
                            PatientProfileBean p = pDao.findById(pid);
                            if (p != null) {
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
            request.setAttribute("errorMessage", "Database error");
            doGet(request, response);
        }
    }
}
