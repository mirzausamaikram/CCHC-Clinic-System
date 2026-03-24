package com.cchc.servlet;

import com.cchc.dao.ClinicServiceDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.QueueEntryDAO;
import com.cchc.dao.StaffProfileDAO;
import com.cchc.model.ClinicServiceBean;
import com.cchc.model.NotificationBean;
import com.cchc.model.PatientProfileBean;
import com.cchc.model.QueueEntryBean;
import com.cchc.model.StaffProfileBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

public class JoinQueueServlet extends HttpServlet {

    private StaffProfileDAO sDao = new StaffProfileDAO();
    private QueueEntryDAO qDao = new QueueEntryDAO();
    private ClinicServiceDAO csDao = new ClinicServiceDAO();
    private PatientProfileDAO pDao = new PatientProfileDAO();
    private NotificationDAO nDao = new NotificationDAO();

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

        int pid = 0;
        int csid = 0;
        int pri = 0;
        try { pid = Integer.parseInt(request.getParameter("patientId")); } catch (Exception e) { pid = 0; }
        try { csid = Integer.parseInt(request.getParameter("clinicServiceId")); } catch (Exception e) { csid = 0; }
        try { pri = Integer.parseInt(request.getParameter("priorityLevel")); } catch (Exception e) { pri = 0; }

        if (pid <= 0 || csid <= 0) {
            response.sendRedirect(request.getContextPath() + "/staff/queue?err=Invalid+input");
            return;
        }

        try {
            StaffProfileBean staff = sDao.findByUserId(user.getUserId());
            if (staff == null) {
                response.sendRedirect(request.getContextPath() + "/staff/queue?err=Staff+profile+not+found");
                return;
            }

            ClinicServiceBean cs = csDao.findById(csid);
            if (cs == null || cs.getClinicId() != staff.getClinicId()) {
                response.sendRedirect(request.getContextPath() + "/staff/queue?err=Invalid+clinic+service");
                return;
            }

            PatientProfileBean p = pDao.findById(pid);
            if (p == null) {
                response.sendRedirect(request.getContextPath() + "/staff/queue?err=Patient+not+found");
                return;
            }

            Date d = new Date(System.currentTimeMillis());

            // simple duplicate check
            boolean hasTicket = qDao.hasActiveTicket(p.getUserId(), staff.getClinicId(), cs.getServiceId(), d);
            if (hasTicket) {
                response.sendRedirect(request.getContextPath() + "/staff/queue?err=You+already+have+active+queue+ticket+for+this+service+today");
                return;
            }

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
                NotificationBean n = new NotificationBean();
                n.setUserId(p.getUserId());
                n.setTitle("Queue Token Issued");
                n.setMessage("Token: " + token);
                n.setNotificationType("QUEUE");
                n.setRelatedAppointmentId(null);
                n.setRead(false);
                nDao.create(n);
                response.sendRedirect(request.getContextPath() + "/staff/queue?success=1");
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/queue?err=Cannot+add+queue");
            }
        } catch (Exception ex) {
            response.sendRedirect(request.getContextPath() + "/staff/queue?err=Database+error");
        }
    }
}
