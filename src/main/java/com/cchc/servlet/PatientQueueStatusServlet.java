package com.cchc.servlet;

import com.cchc.dao.ClinicDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.QueueEntryDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.model.ClinicBean;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientQueueStatusServlet extends HttpServlet {

    private PatientProfileDAO patientDao = new PatientProfileDAO();
    private QueueEntryDAO queueDao = new QueueEntryDAO();
    private ClinicDAO clinicDao = new ClinicDAO();
    private ServiceDAO serviceDao = new ServiceDAO();

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

        UserBean user = (UserBean) session.getAttribute("loginUser");

        try {
            PatientProfileBean patient = patientDao.findByUserId(user.getUserId());
            if (patient == null) {
                request.setAttribute("msg", "Patient profile not found");
                request.getRequestDispatcher("/patient/queue-status.jsp").forward(request, response);
                return;
            }

            Date today = new Date(System.currentTimeMillis());
            List<QueueEntryBean> queueList = queueDao.findByPatientAndDate(patient.getPatientId(), today);

            List<ClinicBean> clinics = clinicDao.findAll();
            List<ServiceBean> services = serviceDao.findAll();
            Map<Integer, ClinicBean> clinicMap = new HashMap<>();
            Map<Integer, ServiceBean> serviceMap = new HashMap<>();
            for (int i = 0; i < clinics.size(); i++) {
                clinicMap.put(clinics.get(i).getClinicId(), clinics.get(i));
            }
            for (int i = 0; i < services.size(); i++) {
                serviceMap.put(services.get(i).getServiceId(), services.get(i));
            }

            request.setAttribute("queueList", queueList);
            request.setAttribute("clinicMap", clinicMap);
            request.setAttribute("serviceMap", serviceMap);

            Map<Integer, Integer> estimateMap = new HashMap<>();
            for (int i = 0; i < queueList.size(); i++) {
                QueueEntryBean q = queueList.get(i);
                int est = 0;
                if ("WAITING".equals(q.getQueueStatus())) {
                    est = (q.getTokenNo() - 1) * 15;
                }
                estimateMap.put(q.getQueueId(), est);
            }
            request.setAttribute("estimateMap", estimateMap);
        } catch (Exception e) {
            request.setAttribute("msg", "Cannot load queue status");
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/patient/queue-status.jsp").forward(request, response);
    }
}
