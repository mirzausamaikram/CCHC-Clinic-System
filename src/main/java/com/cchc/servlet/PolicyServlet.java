package com.cchc.servlet;

import com.cchc.dao.ClinicDAO;
import com.cchc.dao.SystemSettingDAO;
import com.cchc.model.ClinicBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class PolicyServlet extends HttpServlet {

    private SystemSettingDAO settingDao = new SystemSettingDAO();
    private ClinicDAO clinicDao = new ClinicDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String max = settingDao.getValue("maxBookingsPerPatient");
            if (max == null || max.isEmpty()) {
                max = settingDao.getValue("MAX_DAILY_APPOINTMENTS_PER_PATIENT");
            }
            if (max == null || max.isEmpty()) {
                max = "3";
            }

            String cut = settingDao.getValue("cancellationCutoffHours");
            if (cut == null || cut.isEmpty()) {
                cut = "24";
            }

            String qGlobal = settingDao.getValue("queueEnabled");
            if (qGlobal == null || qGlobal.isEmpty()) {
                qGlobal = "1";
            }

            List<ClinicBean> clinics = clinicDao.findAllActive();

            request.setAttribute("maxBookingsPerPatient", max);
            request.setAttribute("cancellationCutoffHours", cut);
            request.setAttribute("queueEnabled", qGlobal);
            request.setAttribute("clinicList", clinics);
        } catch (Exception e) {
            request.setAttribute("msg", "Cannot load settings");
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/admin/policy.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean admin = (UserBean) session.getAttribute("loginUser");
        String max = request.getParameter("maxBookingsPerPatient");
        String cut = request.getParameter("cancellationCutoffHours");
        String qGlobal = request.getParameter("queueEnabled");
        String clinicIdStr = request.getParameter("queueClinicId");
        String clinicQueueEnabled = request.getParameter("clinicQueueEnabled");

        if (max == null || max.isEmpty() || cut == null || cut.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/policy?msg=Please+fill+all+fields");
            return;
        }

        try {
            settingDao.setValue("maxBookingsPerPatient", max, admin.getUserId());
            settingDao.setValue("cancellationCutoffHours", cut, admin.getUserId());
            settingDao.setValue("MAX_DAILY_APPOINTMENTS_PER_PATIENT", max, admin.getUserId());

            if (qGlobal != null && !qGlobal.isEmpty()) {
                settingDao.setValue("queueEnabled", qGlobal, admin.getUserId());
            }

            int clinicId = 0;
            try {
                clinicId = Integer.parseInt(clinicIdStr);
            } catch (Exception e) {
                clinicId = 0;
            }
            if (clinicId > 0 && clinicQueueEnabled != null && !clinicQueueEnabled.isEmpty()) {
                settingDao.setValue("queueEnabled_clinic_" + clinicId, clinicQueueEnabled, admin.getUserId());
            }

            response.sendRedirect(request.getContextPath() + "/admin/policy?msg=Policy+updated");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/policy?msg=Save+failed");
        }
    }
}
