package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.ClinicDAO;
import com.cchc.dao.ClinicServiceDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.ClinicBean;
import com.cchc.model.ClinicServiceBean;
import com.cchc.model.NotificationBean;
import com.cchc.model.PatientProfileBean;
import com.cchc.model.ServiceBean;
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
import java.sql.Time;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AppointmentBookingServlet", urlPatterns = {"/patient/appointments"})
public class AppointmentBookingServlet extends HttpServlet {

    private AppointmentDAO dao = new AppointmentDAO();
    private PatientProfileDAO pDao = new PatientProfileDAO();
    private ClinicDAO cDao = new ClinicDAO();
    private ClinicServiceDAO csDao = new ClinicServiceDAO();
    private ServiceDAO sDao = new ServiceDAO();
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
        if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");
        String act = request.getParameter("action");
        if (act == null || "".equals(act)) {
            act = "book";
        }

        try {
            PatientProfileBean p = pDao.findByUserId(user.getUserId());
            if (p == null) {
                request.setAttribute("errorMessage", "Patient profile not found.");
                request.getRequestDispatcher("/patient/book-appointment.jsp").forward(request, response);
                return;
            }

            request.setAttribute("patientProfile", p);

            if ("list".equalsIgnoreCase(act)) {
                List<AppointmentBean> list = dao.findByPatientId(p.getPatientId());
                Map<Integer, ClinicServiceBean> csMap = new HashMap<>();
                Map<Integer, ServiceBean> sMap = new HashMap<>();
                Map<Integer, ClinicBean> cMap = new HashMap<>();

                List<ClinicBean> clinicList = cDao.findAllActive();
                for (int i = 0; i < clinicList.size(); i++) {
                    ClinicBean c = clinicList.get(i);
                    cMap.put(c.getClinicId(), c);
                    List<ClinicServiceBean> x = csDao.findByClinicId(c.getClinicId());
                    for (int j = 0; j < x.size(); j++) {
                        ClinicServiceBean z = x.get(j);
                        csMap.put(z.getClinicServiceId(), z);
                    }
                }
                List<ServiceBean> sList = sDao.findAllActive();
                for (int i = 0; i < sList.size(); i++) {
                    ServiceBean s = sList.get(i);
                    sMap.put(s.getServiceId(), s);
                }

                request.setAttribute("appointments", list);
                request.setAttribute("clinicServiceMap", csMap);
                request.setAttribute("serviceMap", sMap);
                request.setAttribute("clinicMap", cMap);
                request.getRequestDispatcher("/patient/my-appointments.jsp").forward(request, response);
                return;
            }

            List<ClinicBean> clinicList = cDao.findAllActive();
            int clinicId = 0;
            try {
                clinicId = Integer.parseInt(request.getParameter("clinicId"));
            } catch (Exception e) {
                clinicId = 0;
            }
            if (clinicId == 0 && clinicList.size() > 0) {
                clinicId = clinicList.get(0).getClinicId();
            }

            List<ClinicServiceBean> csList = new java.util.ArrayList<>();
            if (clinicId > 0) {
                csList = csDao.findByClinicId(clinicId);
            }

            Map<Integer, ServiceBean> sMap = new HashMap<>();
            List<ServiceBean> sList = sDao.findAllActive();
            for (int i = 0; i < sList.size(); i++) {
                ServiceBean s = sList.get(i);
                sMap.put(s.getServiceId(), s);
            }

            request.setAttribute("clinics", clinicList);
            request.setAttribute("clinicServices", csList);
            request.setAttribute("serviceMap", sMap);
            request.setAttribute("selectedClinicId", clinicId);

            request.getRequestDispatcher("/patient/book-appointment.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error");
            request.getRequestDispatcher("/patient/book-appointment.jsp").forward(request, response);
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
        if (!"PATIENT".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");

        try {
            PatientProfileBean p = pDao.findByUserId(user.getUserId());
            if (p == null) {
                request.setAttribute("errorMessage", "Patient profile not found.");
                doGet(request, response);
                return;
            }

            int csId = 0;
            try {
                csId = Integer.parseInt(request.getParameter("clinicServiceId"));
            } catch (Exception e) {
                csId = 0;
            }
            String d = request.getParameter("appointmentDate");
            String t = request.getParameter("startTime");
            String notes = request.getParameter("notes");

            if (csId <= 0 || d == null || t == null || "".equals(d) || "".equals(t)) {
                request.setAttribute("errorMessage", "Clinic service, date and start time are required.");
                doGet(request, response);
                return;
            }

            ClinicServiceBean cs = csDao.findById(csId);
            if (cs == null) {
                request.setAttribute("errorMessage", "Selected clinic service is invalid.");
                doGet(request, response);
                return;
            }

            Date ad = Date.valueOf(d);
            LocalTime st = LocalTime.parse(t);
            LocalTime et = st.plusMinutes(cs.getDurationMinutes());

            AppointmentBean appt = new AppointmentBean();
            appt.setPatientId(p.getPatientId());
            appt.setClinicServiceId(csId);
            appt.setAssignedStaffId(null);
            appt.setAppointmentDate(ad);
            appt.setStartTime(Time.valueOf(st));
            appt.setEndTime(Time.valueOf(et));
            appt.setBookingChannel("ONLINE");
            appt.setStatus("BOOKED");
            appt.setNotes(notes);
            appt.setCreatedByUserId(user.getUserId());

            int id = dao.create(appt);
            if (id > 0) {
                NotificationBean n = new NotificationBean();
                n.setUserId(user.getUserId());
                n.setTitle("Appointment Booked");
                n.setMessage("Booked on " + d + " " + t);
                n.setNotificationType("APPOINTMENT");
                n.setRelatedAppointmentId(id);
                n.setRead(false);
                nDao.create(n);

                response.sendRedirect(request.getContextPath() + "/patient/appointments?action=list&success=1");
                return;
            }

            request.setAttribute("errorMessage", "Failed to book appointment.");
            doGet(request, response);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Invalid date or time format.");
            doGet(request, response);
        }
    }
}
