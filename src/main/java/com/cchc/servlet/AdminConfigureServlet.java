package com.cchc.servlet;

import com.cchc.dao.ClinicDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.dao.SystemSettingDAO;
import com.cchc.model.ClinicBean;
import com.cchc.model.ServiceBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Time;
import java.util.List;

public class AdminConfigureServlet extends HttpServlet {

    private ClinicDAO clinicDao = new ClinicDAO();
    private ServiceDAO serviceDao = new ServiceDAO();
    private SystemSettingDAO settingDao = new SystemSettingDAO();

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
            List<ClinicBean> clinics = clinicDao.findAll();
            List<ServiceBean> services = serviceDao.findAll();
            request.setAttribute("clinicList", clinics);
            request.setAttribute("serviceList", services);
        } catch (Exception e) {
            request.setAttribute("msg", "Cannot load configuration data");
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/admin/configure.jsp").forward(request, response);
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
        String action = request.getParameter("action");

        try {
            if ("addClinic".equals(action)) {
                ClinicBean c = new ClinicBean();
                c.setClinicCode(request.getParameter("clinicCode"));
                c.setClinicName(request.getParameter("clinicName"));
                c.setAddressLine1(request.getParameter("address"));
                c.setCity(request.getParameter("city"));
                c.setState(request.getParameter("state"));
                c.setPhone(request.getParameter("phone"));
                c.setOpeningTime(Time.valueOf(request.getParameter("openingTime") + ":00"));
                c.setClosingTime(Time.valueOf(request.getParameter("closingTime") + ":00"));
                c.setActive(true);
                clinicDao.addClinic(c);
                response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Clinic+added");
                return;
            }

            if ("updateHours".equals(action)) {
                int clinicId = Integer.parseInt(request.getParameter("clinicId"));
                Time opening = Time.valueOf(request.getParameter("openingTime") + ":00");
                Time closing = Time.valueOf(request.getParameter("closingTime") + ":00");
                clinicDao.updateHours(clinicId, opening, closing);
                response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Clinic+hours+updated");
                return;
            }

            if ("toggleClinic".equals(action)) {
                int clinicId = Integer.parseInt(request.getParameter("clinicId"));
                boolean active = "1".equals(request.getParameter("active"));
                clinicDao.setActive(clinicId, active);
                response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Clinic+status+updated");
                return;
            }

            if ("addService".equals(action)) {
                ServiceBean s = new ServiceBean();
                s.setServiceCode(request.getParameter("serviceCode"));
                s.setServiceName(request.getParameter("serviceName"));
                s.setServiceDescription(request.getParameter("serviceDescription"));
                s.setDefaultDurationMinutes(Integer.parseInt(request.getParameter("duration")));
                s.setActive(true);
                serviceDao.addService(s);
                response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Service+added");
                return;
            }

            if ("toggleService".equals(action)) {
                int serviceId = Integer.parseInt(request.getParameter("serviceId"));
                boolean active = "1".equals(request.getParameter("active"));
                serviceDao.setActive(serviceId, active);
                response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Service+status+updated");
                return;
            }

            if ("saveQuota".equals(action)) {
                int clinicId = Integer.parseInt(request.getParameter("clinicId"));
                int serviceId = Integer.parseInt(request.getParameter("serviceId"));
                String quota = request.getParameter("quota");
                String key = "quota_clinic_" + clinicId + "_service_" + serviceId;
                settingDao.setValue(key, quota, admin.getUserId());
                response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Capacity+rule+saved");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Unknown+action");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/configure?msg=Save+failed");
        }
    }
}
