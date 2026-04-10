package com.cchc.servlet;

import com.cchc.dao.QueueEntryDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.StaffProfileDAO;
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
import java.sql.SQLException;
import java.util.List;

public class QueueProgressionServlet extends HttpServlet {

    private QueueEntryDAO queueDao = new QueueEntryDAO();
    private StaffProfileDAO staffDao = new StaffProfileDAO();
    private PatientProfileDAO patientDao = new PatientProfileDAO();
    private NotificationDAO notiDao = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        StaffProfileBean profile = null;
        try {
            profile = staffDao.findByUserId(user.getUserId());
        } catch (SQLException e) {
            request.setAttribute("msg", "Error loading staff profile");
            request.getRequestDispatcher("/staff/queue_progression.jsp").forward(request, response);
            return;
        }

        if (profile == null) {
            request.setAttribute("msg", "Staff profile not found");
            request.getRequestDispatcher("/staff/queue_progression.jsp").forward(request, response);
            return;
        }

        request.setAttribute("clinicId", profile.getClinicId());

        try {
            List<QueueEntryBean> list = queueDao.getWaitingQueue(profile.getClinicId());
            request.setAttribute("queueList", list);
        } catch (SQLException e) {
            request.setAttribute("msg", "Error loading queue");
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/staff/queue_progression.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
        String action = request.getParameter("action");

        if ("callNext".equals(action)) {
            StaffProfileBean profile = null;
            try {
                profile = staffDao.findByUserId(user.getUserId());
            } catch (SQLException e) {
                response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=Error+loading+profile");
                return;
            }

            if (profile == null) {
                response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=Profile+not+found");
                return;
            }

            try {
                QueueEntryBean next = queueDao.getNextQueueNumber(profile.getClinicId());
                if (next != null) {
                    queueDao.updateStatus(next.getQueueId(), "CALLED");

                    PatientProfileBean p = patientDao.findById(next.getPatientId());
                    if (p != null) {
                        NotificationBean n = new NotificationBean();
                        n.setUserId(p.getUserId());
                        n.setTitle("Queue called");
                        n.setMessage("Queue called: token " + next.getTokenNo() + " please proceed to counter.");
                        n.setNotificationType("Queue called");
                        n.setRelatedAppointmentId(null);
                        n.setRead(false);
                        notiDao.create(n);
                    }

                    response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=Token+" + next.getTokenNo() + "+called");
                } else {
                    response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=No+waiting+patients");
                }
            } catch (SQLException e) {
                response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=Error+calling+next");
            }

        } else {
            String idStr = request.getParameter("queueId");
            int queueId = 0;
            try {
                queueId = Integer.parseInt(idStr);
            } catch (Exception e) {
                queueId = 0;
            }

            String newStatus = null;
            if ("skip".equals(action)) {
                newStatus = "MISSED";
            } else if ("markServed".equals(action)) {
                newStatus = "DONE";
            }

            if (queueId <= 0 || newStatus == null) {
                response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=Invalid+input");
                return;
            }

            try {
                queueDao.updateStatus(queueId, newStatus);

                QueueEntryBean q = queueDao.findById(queueId);
                if (q != null) {
                    PatientProfileBean p = patientDao.findById(q.getPatientId());
                    if (p != null) {
                        NotificationBean n = new NotificationBean();
                        n.setUserId(p.getUserId());
                        if ("MISSED".equals(newStatus)) {
                            n.setTitle("Queue skipped");
                            n.setMessage("Queue skipped: your queue token was skipped.");
                        } else {
                            n.setTitle("Queue expired");
                            n.setMessage("Queue expired: your queue action is completed.");
                        }
                        n.setNotificationType(n.getTitle());
                        n.setRelatedAppointmentId(null);
                        n.setRead(false);
                        notiDao.create(n);
                    }
                }

                response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=Status+updated");
            } catch (SQLException e) {
                response.sendRedirect(request.getContextPath() + "/staff/queue-progression?msg=Update+failed");
            }
        }
    }
}
