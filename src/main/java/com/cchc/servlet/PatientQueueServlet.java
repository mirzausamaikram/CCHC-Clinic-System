package com.cchc.servlet;

import com.cchc.dao.ClinicDAO;
import com.cchc.dao.ClinicServiceDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.QueueEntryDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.dao.SystemSettingDAO;
import com.cchc.model.ClinicBean;
import com.cchc.model.ClinicServiceBean;
import com.cchc.model.NotificationBean;
import com.cchc.model.PatientProfileBean;
import com.cchc.model.QueueEntryBean;
import com.cchc.model.ServiceBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class PatientQueueServlet extends HttpServlet {

    private ClinicDAO clinicDao = new ClinicDAO();
    private ServiceDAO serviceDao = new ServiceDAO();
    private ClinicServiceDAO clinicServiceDao = new ClinicServiceDAO();
    private PatientProfileDAO patientDao = new PatientProfileDAO();
    private QueueEntryDAO queueDao = new QueueEntryDAO();
    private NotificationDAO notificationDao = new NotificationDAO();
    private SystemSettingDAO settingDao = new SystemSettingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<ClinicBean> clinics = clinicDao.findAllActive();
            List<ServiceBean> services = serviceDao.findAllActive();
            request.setAttribute("clinics", clinics);
            request.setAttribute("services", services);
        } catch (Exception e) {
            request.setAttribute("msg", "Cannot load clinic/service list");
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/patient/join_queue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");

        int clinicId = 0;
        int serviceId = 0;
        try { clinicId = Integer.parseInt(request.getParameter("clinicId")); } catch (Exception e) { clinicId = 0; }
        try { serviceId = Integer.parseInt(request.getParameter("serviceId")); } catch (Exception e) { serviceId = 0; }

        if (clinicId <= 0 || serviceId <= 0) {
            response.sendRedirect(request.getContextPath() + "/patient/queue?msg=Invalid+input");
            return;
        }

        try {
            String globalQueue = settingDao.getValue("queueEnabled");
            String clinicQueue = settingDao.getValue("queueEnabled_clinic_" + clinicId);
            boolean enabled = true;
            if (globalQueue != null && ("0".equals(globalQueue) || "false".equalsIgnoreCase(globalQueue))) {
                enabled = false;
            }
            if (clinicQueue != null && ("0".equals(clinicQueue) || "false".equalsIgnoreCase(clinicQueue))) {
                enabled = false;
            }
            if (!enabled) {
                response.sendRedirect(request.getContextPath() + "/patient/queue?msg=Walk-in+queue+is+disabled+for+this+clinic");
                return;
            }

            PatientProfileBean patient = patientDao.findByUserId(user.getUserId());
            if (patient == null) {
                response.sendRedirect(request.getContextPath() + "/patient/queue?msg=Patient+profile+not+found");
                return;
            }

            List<ClinicServiceBean> mappings = clinicServiceDao.findByClinicId(clinicId);
            boolean serviceAllowed = false;
            for (int i = 0; i < mappings.size(); i++) {
                if (mappings.get(i).getServiceId() == serviceId) {
                    serviceAllowed = true;
                    break;
                }
            }

            if (!serviceAllowed) {
                response.sendRedirect(request.getContextPath() + "/patient/queue?msg=Service+not+available+for+this+clinic");
                return;
            }

            Date today = new Date(System.currentTimeMillis());
            boolean hasActive = queueDao.hasActiveTicket(user.getUserId(), clinicId, serviceId, today);
            if (hasActive) {
                response.sendRedirect(request.getContextPath() + "/patient/queue?msg=You+already+have+an+active+queue+ticket");
                return;
            }

            int token = queueDao.getNextTokenNo(clinicId, today);

            QueueEntryBean q = new QueueEntryBean();
            q.setClinicId(clinicId);
            q.setServiceId(serviceId);
            q.setPatientId(patient.getPatientId());
            q.setAppointmentId(null);
            q.setQueueDate(today);
            q.setTokenNo(token);
            q.setPriorityLevel(0);
            q.setQueueStatus("WAITING");

            int queueId = queueDao.create(q);
            if (queueId > 0) {
                int estMinutes = (token - 1) * 15;
                NotificationBean n = new NotificationBean();
                n.setUserId(user.getUserId());
                n.setTitle("Queue Token Issued");
                n.setMessage("You joined the queue. Token: " + token + ". Estimated wait: " + estMinutes + " minutes.");
                n.setNotificationType("QUEUE");
                n.setRelatedAppointmentId(null);
                n.setRead(false);
                notificationDao.create(n);

                response.sendRedirect(request.getContextPath() + "/patient/queue-status?msg=Joined+queue+successfully.+Estimated+wait+" + estMinutes + "+minutes");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/patient/queue?msg=Cannot+join+queue");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/patient/queue?msg=Database+error");
        }
    }
}
