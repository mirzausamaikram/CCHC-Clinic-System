package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.ClinicDAO;
import com.cchc.dao.ClinicServiceDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.dao.SystemSettingDAO;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebServlet(name = "AppointmentBookingServlet", urlPatterns = {"/patient/appointments"})
public class AppointmentBookingServlet extends HttpServlet {

    private AppointmentDAO dao = new AppointmentDAO();
    private PatientProfileDAO pDao = new PatientProfileDAO();
    private ClinicDAO cDao = new ClinicDAO();
    private ClinicServiceDAO csDao = new ClinicServiceDAO();
    private ServiceDAO sDao = new ServiceDAO();
    private NotificationDAO nDao = new NotificationDAO();
    private SystemSettingDAO setDao = new SystemSettingDAO();

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
            if (p != null) {
                request.setAttribute("patientProfile", p);
            }

            if ("list".equalsIgnoreCase(act)) {
                if (p == null) {
                    request.setAttribute("appointments", new java.util.ArrayList<AppointmentBean>());
                    request.getRequestDispatcher("/patient/my-appointments.jsp").forward(request, response);
                    return;
                }
                List<AppointmentBean> list = dao.findByPatientId(user.getUserId());
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

            List<ClinicBean> clinicList = cDao.getAllClinics();
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
            List<ServiceBean> allServices = sDao.getAllServices();
            for (int i = 0; i < allServices.size(); i++) {
                ServiceBean s = allServices.get(i);
                sMap.put(s.getServiceId(), s);
            }

            List<ServiceBean> sList = new ArrayList<>();
            for (int i = 0; i < csList.size(); i++) {
                ServiceBean s = sMap.get(csList.get(i).getServiceId());
                if (s != null) {
                    sList.add(s);
                }
            }
            // fallback: if no clinic services configured, show all active services
            if (sList.isEmpty()) {
                sList = sDao.getAllServices();
            }

            request.setAttribute("clinics", clinicList);
            request.setAttribute("services", sList);
            request.setAttribute("clinicServices", csList);
            request.setAttribute("serviceMap", sMap);
            request.setAttribute("selectedClinicId", clinicId);

            // simple timeslot list
            String selectedDate = request.getParameter("appointmentDate");
            if (selectedDate == null || selectedDate.isEmpty()) {
                selectedDate = LocalDate.now().toString();
            }
            List<String> allSlots = buildSimpleSlots();
            Set<String> booked = dao.getBookedStartTimes(clinicId, Date.valueOf(selectedDate));
            List<String> availableSlots = new ArrayList<>();
            for (int i = 0; i < allSlots.size(); i++) {
                String s = allSlots.get(i);
                if (!booked.contains(s)) {
                    availableSlots.add(s);
                }
            }
            request.setAttribute("selectedDate", selectedDate);
            request.setAttribute("availableSlots", availableSlots);

            request.getRequestDispatcher("/patient/book-appointment.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error");
            request.getRequestDispatcher("/patient/book-appointment.jsp").forward(request, response);
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

            // simple policy check
            int maxBookings = 3;
            try {
                String x = setDao.getValue("maxBookingsPerPatient");
                if (x == null || x.isEmpty()) {
                    x = setDao.getValue("MAX_DAILY_APPOINTMENTS_PER_PATIENT");
                }
                if (x != null && !x.isEmpty()) {
                    maxBookings = Integer.parseInt(x);
                }
            } catch (Exception e) {
                maxBookings = 3;
            }

            int activeCnt = dao.countActiveBookingsByPatient(user.getUserId());
            if (activeCnt >= maxBookings) {
                request.setAttribute("error", "Booking failed - slot may be full");
                doGet(request, response);
                return;
            }

            int csId = 0;
            try {
                csId = Integer.parseInt(request.getParameter("clinicServiceId"));
            } catch (Exception e) {
                csId = 0;
            }
            int clinicIdParam = 0;
            try {
                clinicIdParam = Integer.parseInt(request.getParameter("clinicId"));
            } catch (Exception e) {
                clinicIdParam = 0;
            }
            String d = request.getParameter("appointmentDate");
            String t = request.getParameter("startTime");
            if (t == null || t.isEmpty()) {
                t = request.getParameter("timeSlot");
            }
            String notes = request.getParameter("notes");

            if (csId <= 0 || d == null || t == null || "".equals(d) || "".equals(t)) {
                request.setAttribute("error", "Booking failed - slot may be full");
                doGet(request, response);
                return;
            }

            ClinicServiceBean cs = csDao.findById(csId);
            int useClinicId = 0;
            int useServiceId = 0;
            int useDuration = 20;

            if (cs != null) {
                useClinicId = cs.getClinicId();
                useServiceId = cs.getServiceId();
                useDuration = cs.getDurationMinutes();
            } else {
                ServiceBean s = sDao.findById(csId);
                if (s != null && clinicIdParam > 0) {
                    useClinicId = clinicIdParam;
                    useServiceId = s.getServiceId();
                    useDuration = s.getDefaultDurationMinutes();
                }
            }

            if (useClinicId <= 0 || useServiceId <= 0) {
                request.setAttribute("error", "Booking failed - slot may be full");
                doGet(request, response);
                return;
            }

            Date ad = Date.valueOf(d);

            // simple slot conflict check
            java.util.Set<String> bookedSlots = dao.getBookedStartTimes(useClinicId, ad);
            if (bookedSlots.contains(t)) {
                request.setAttribute("error", "Selected slot already booked");
                doGet(request, response);
                return;
            }

            LocalTime st = LocalTime.parse(t);
            LocalTime et = st.plusMinutes(useDuration);

            AppointmentBean appt = new AppointmentBean();
            appt.setUserId(user.getUserId());
            appt.setClinicId(useClinicId);
            appt.setServiceId(useServiceId);
            appt.setAppointmentDate(LocalDate.parse(d));
            appt.setTimeSlot(st.toString() + "-" + et.toString());

            // simple quota rule: if service is limited, keep as PENDING for staff approval
            boolean limitedQuota = useDuration >= 30;
            int dayCount = dao.countByClinicAndServiceAndDate(useClinicId, useServiceId, ad);
            if (limitedQuota && dayCount >= 5) {
                appt.setStatus("PENDING");
            } else {
                appt.setStatus("BOOKED");
            }

            appt.setNotes(notes);

            int id = dao.bookAppointment(appt);
            if (id > 0) {
                NotificationBean n = new NotificationBean();
                n.setUserId(user.getUserId());

                // simple notification system
                n.setTitle("Appointment Confirmed");
                n.setMessage("Your appointment is confirmed for " + d + " at " + t);
                n.setNotificationType("Appointment Confirmed");

                n.setRelatedAppointmentId(id);
                n.setRead(false);
                nDao.create(n);

                session.setAttribute("msg", "Appointment booked successfully!");
                response.sendRedirect(request.getContextPath() + "/my_appointments");
                return;
            }

            request.setAttribute("error", "Booking failed - slot may be full");
            doGet(request, response);
        } catch (Exception ex) {
            request.setAttribute("error", "Booking failed - slot may be full");
            doGet(request, response);
        }
    }

    private List<String> buildSimpleSlots() {
        List<String> list = new ArrayList<>();
        LocalTime t = LocalTime.of(8, 0);
        LocalTime end = LocalTime.of(17, 0);
        while (!t.isAfter(end.minusMinutes(30))) {
            String x = t.toString();
            if (x.length() == 5) {
                list.add(x);
            } else {
                list.add(String.format("%02d:%02d", t.getHour(), t.getMinute()));
            }
            t = t.plusMinutes(30);
        }
        return list;
    }
}
