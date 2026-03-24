package com.cchc.servlet;

import com.cchc.dao.ClinicDAO;
import com.cchc.dao.ReportDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.model.ClinicBean;
import com.cchc.model.ServiceBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminReportServlet", urlPatterns = {"/admin/reports"})
public class AdminReportServlet extends HttpServlet {

    private ReportDAO dao = new ReportDAO();
    private ClinicDAO clinicDao = new ClinicDAO();
    private ServiceDAO serviceDao = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // check if user is logged in
        HttpSession session = request.getSession(false);
        UserBean user = null;
        if (session != null) {
            user = (UserBean) session.getAttribute("user");
            if (user == null) {
                user = (UserBean) session.getAttribute("loginUser");
                if (user != null) {
                    session.setAttribute("user", user);
                }
            }
        }

        if (user == null || !"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // load clinic list for dropdown
        List<ClinicBean> clinicList = null;
        List<ServiceBean> serviceList = null;
        try {
            clinicList = clinicDao.findAllActive();
            serviceList = serviceDao.findAllActive();
        } catch (SQLException e) {
            request.setAttribute("msg", "Cannot load clinics");
        }
        request.setAttribute("clinicList", clinicList);
        request.setAttribute("serviceList", serviceList);

        // default to current month and year if not provided
        Calendar now = Calendar.getInstance();
        int defaultMonth = now.get(Calendar.MONTH) + 1;
        int defaultYear = now.get(Calendar.YEAR);

        int clinicId = 0;
        int serviceId = 0;
        String status = "";
        int month = defaultMonth;
        int year = defaultYear;

        try {
            String cStr = request.getParameter("clinicId");
            String sStr = request.getParameter("serviceId");
            status = request.getParameter("status");
            String mStr = request.getParameter("month");
            String yStr = request.getParameter("year");
            if (cStr != null && !cStr.isEmpty()) {
                clinicId = Integer.parseInt(cStr);
            }
            if (sStr != null && !sStr.isEmpty()) {
                serviceId = Integer.parseInt(sStr);
            }
            if (mStr != null && !mStr.isEmpty()) {
                month = Integer.parseInt(mStr);
            }
            if (yStr != null && !yStr.isEmpty()) {
                year = Integer.parseInt(yStr);
            }
        } catch (Exception e) {
            // keep defaults
        }

        // pick first clinic if none selected
        if (clinicId <= 0 && clinicList != null && clinicList.size() > 0) {
            clinicId = clinicList.get(0).getClinicId();
        }

        request.setAttribute("selClinicId", clinicId);
        request.setAttribute("selServiceId", serviceId);
        request.setAttribute("selStatus", status == null ? "" : status);
        request.setAttribute("selMonth", month);
        request.setAttribute("selYear", year);

        // simple calculation for report
        if (clinicId > 0) {
            try {
                int utilRate = dao.getUtilisationRate(clinicId, serviceId, month, year);
                int noShow = dao.getNoShowSummary(clinicId, serviceId, month, year);
                int totalSlots = dao.getTotalSlots(clinicId, serviceId, month, year);

                request.setAttribute("utilRate", utilRate);
                request.setAttribute("noShow", noShow);
                request.setAttribute("totalSlots", totalSlots);

                List<Map<String, Object>> records = dao.getAppointmentRecords(clinicId, serviceId, month, year, status);
                request.setAttribute("records", records);
            } catch (SQLException e) {
                request.setAttribute("utilRate", 0);
                request.setAttribute("noShow", 0);
                request.setAttribute("totalSlots", 0);
                request.setAttribute("records", new java.util.ArrayList<Map<String, Object>>());
            }
        }

        // overall summary counts
        try {
            request.setAttribute("userCount", dao.getUserCount());
            request.setAttribute("apptCount", dao.getAppointmentCount());
            request.setAttribute("queueCount", dao.getTodayQueueCount());
            request.setAttribute("notiCount", dao.getUnreadNotificationCount());
        } catch (SQLException e) {
            // ignore
        }

        request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
    }
}
