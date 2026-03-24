package com.cchc.servlet;

import com.cchc.dao.UserDAO;
import com.cchc.dao.AppointmentDAO;
import com.cchc.dao.NotificationDAO;
import com.cchc.dao.PatientProfileDAO;
import com.cchc.model.AppointmentBean;
import com.cchc.model.NotificationBean;
import com.cchc.model.PatientProfileBean;
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

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String password = request.getParameter("password");

        if (isBlank(usernameOrEmail) || isBlank(password)) {
            request.setAttribute("errorMessage", "Username/email and password are required.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            UserBean user = userDAO.authenticate(usernameOrEmail.trim(), password);
            if (user == null) {
                request.setAttribute("errorMessage", "Invalid credentials.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            String roleName = userDAO.getRoleNameByRoleId(user.getRoleId());
            if (roleName == null) {
                request.setAttribute("errorMessage", "User role is invalid.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            userDAO.updateLastLogin(user.getUserId());

            HttpSession session = request.getSession(true);
            session.setAttribute("loginUser", user);
            session.setAttribute("loginRole", roleName);
            session.setMaxInactiveInterval(30 * 60);

            switch (roleName) {
                case "PATIENT":
                    // simple notification system - check tomorrow appointments and create reminder
                    try {
                        PatientProfileDAO ppDao = new PatientProfileDAO();
                        PatientProfileBean pp = ppDao.findByUserId(user.getUserId());
                        if (pp != null) {
                            AppointmentDAO apptDao = new AppointmentDAO();
                            NotificationDAO nDao = new NotificationDAO();
                            List<AppointmentBean> tomorrowList = apptDao.getTomorrowAppointments(user.getUserId());
                            for (int i = 0; i < tomorrowList.size(); i++) {
                                AppointmentBean a = tomorrowList.get(i);
                                if (!nDao.reminderExists(user.getUserId(), a.getAppointmentId())) {
                                    NotificationBean nb = new NotificationBean();
                                    nb.setUserId(user.getUserId());
                                    nb.setTitle("Reminder for upcoming appointment");
                                    nb.setMessage("Reminder: you have an appointment tomorrow on " + a.getAppointmentDate() + " at " + a.getStartTime());
                                    nb.setNotificationType("Reminder for upcoming appointment");
                                    nb.setRelatedAppointmentId(a.getAppointmentId());
                                    nb.setRead(false);
                                    nDao.create(nb);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // ignore for now
                    }
                    response.sendRedirect(request.getContextPath() + "/patient/dashboard.jsp");
                    break;
                case "STAFF":
                    response.sendRedirect(request.getContextPath() + "/staff/dashboard.jsp");
                    break;
                case "ADMIN":
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                    break;
                default:
                    session.invalidate();
                    request.setAttribute("errorMessage", "Unsupported role.");
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                    break;
            }
        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Database error: " + ex.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
