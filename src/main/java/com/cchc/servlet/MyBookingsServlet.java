package com.cchc.servlet;

import com.cchc.dao.AppointmentDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "MyBookingsServlet", urlPatterns = {"/patient/my-bookings"})
public class MyBookingsServlet extends HttpServlet {

    private AppointmentDAO dao = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = String.valueOf(session.getAttribute("loginRole"));
        if (!"PATIENT".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");

        try {
            // get data from database
            List<AppointmentBean> apptList = dao.getMyAppointments(user.getUserId());
            String msg = request.getParameter("msg");
            request.setAttribute("apptList", apptList);
            if (msg != null && !msg.isEmpty()) {
                request.setAttribute("msg", msg);
            }
            request.getRequestDispatcher("/patient/my_bookings.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("msg", "Error loading bookings");
            request.getRequestDispatcher("/patient/my_bookings.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO: finish this later
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");

        String idStr = request.getParameter("appointmentId");
        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            // not sure if this works
            id = 0;
        }

        if (id > 0) {
            try {
                dao.cancelAppointment(id, user.getUserId());
            } catch (SQLException e) {
                // ignore for now
            }
        }

        response.sendRedirect(request.getContextPath() + "/patient/my-bookings");
    }
}
