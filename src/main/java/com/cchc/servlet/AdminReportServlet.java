package com.cchc.servlet;

import com.cchc.dao.ReportDAO;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AdminReportServlet", urlPatterns = {"/admin/reports"})
public class AdminReportServlet extends HttpServlet {

    private ReportDAO dao = new ReportDAO();

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

        try {
            int userCount = dao.getUserCount();
            int apptCount = dao.getAppointmentCount();
            int queueCount = dao.getTodayQueueCount();
            int notiCount = dao.getUnreadNotificationCount();

            request.setAttribute("userCount", userCount);
            request.setAttribute("apptCount", apptCount);
            request.setAttribute("queueCount", queueCount);
            request.setAttribute("notiCount", notiCount);
        } catch (SQLException e) {
            request.setAttribute("msg", "Cannot load report now");
        }

        request.getRequestDispatcher("/admin/reports.jsp").forward(request, response);
    }
}
