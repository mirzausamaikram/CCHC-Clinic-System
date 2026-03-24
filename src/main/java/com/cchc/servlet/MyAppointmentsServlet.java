package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.ClinicDAO;
import com.cchc.dao.ServiceDAO;
import com.cchc.model.AppointmentBean;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/my_appointments")
public class MyAppointmentsServlet extends HttpServlet {

    private AppointmentDAO dao = new AppointmentDAO();
    private ClinicDAO clinicDao = new ClinicDAO();
    private ServiceDAO serviceDao = new ServiceDAO();

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

        try {
            List<AppointmentBean> list = dao.getMyAppointments(user.getUserId());
            request.setAttribute("appointments", list);

            List<ClinicBean> clinics = clinicDao.findAll();
            List<ServiceBean> services = serviceDao.findAll();
            Map<Integer, String> clinicNameMap = new HashMap<>();
            Map<Integer, String> serviceNameMap = new HashMap<>();
            for (int i = 0; i < clinics.size(); i++) {
                clinicNameMap.put(clinics.get(i).getClinicId(), clinics.get(i).getClinicName());
            }
            for (int i = 0; i < services.size(); i++) {
                serviceNameMap.put(services.get(i).getServiceId(), services.get(i).getServiceName());
            }

            request.setAttribute("clinicNameMap", clinicNameMap);
            request.setAttribute("serviceNameMap", serviceNameMap);
        } catch (Exception e) {
            request.setAttribute("appointments", new ArrayList<AppointmentBean>());
            request.setAttribute("error", "Cannot load appointments");
        }

        request.getRequestDispatcher("/patient/my_appointments.jsp").forward(request, response);
    }
}
