package com.cchc.servlet;

import com.cchc.dao.PatientProfileDAO;
import com.cchc.dao.UserDAO;
import com.cchc.model.PatientProfileBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AccountProfileServlet extends HttpServlet {

    private UserDAO userDao = new UserDAO();
    private PatientProfileDAO patientDao = new PatientProfileDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");
        String role = String.valueOf(session.getAttribute("loginRole"));

        try {
            if ("PATIENT".equals(role)) {
                PatientProfileBean p = patientDao.findByUserId(user.getUserId());
                request.setAttribute("patientProfile", p);
            }
        } catch (Exception e) {
            request.setAttribute("msg", "Cannot load profile details");
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/account/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");
        String role = String.valueOf(session.getAttribute("loginRole"));

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/account/profile?msg=Username+and+email+are+required");
            return;
        }

        try {
            UserBean update = new UserBean();
            update.setUserId(user.getUserId());
            update.setUsername(username);
            update.setEmail(email);
            userDao.updateProfile(update);

            if ("PATIENT".equals(role)) {
                patientDao.updatePhoneByUserId(user.getUserId(), phone);
            }

            if (newPassword != null && !newPassword.isEmpty()) {
                if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
                    response.sendRedirect(request.getContextPath() + "/account/profile?msg=Confirm+password+not+match");
                    return;
                }

                UserBean auth = userDao.authenticate(user.getEmail(), currentPassword);
                if (auth == null || auth.getUserId() != user.getUserId()) {
                    response.sendRedirect(request.getContextPath() + "/account/profile?msg=Current+password+wrong");
                    return;
                }

                userDao.updatePassword(user.getUserId(), newPassword);
            }

            user.setUsername(username);
            user.setEmail(email);
            session.setAttribute("loginUser", user);

            response.sendRedirect(request.getContextPath() + "/account/profile?msg=Profile+updated");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/account/profile?msg=Update+failed");
        }
    }
}