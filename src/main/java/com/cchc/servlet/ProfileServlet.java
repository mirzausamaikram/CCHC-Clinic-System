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

public class ProfileServlet extends HttpServlet {

    private UserDAO userDao = new UserDAO();
    private PatientProfileDAO pDao = new PatientProfileDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
            request.setAttribute("profile", p);
        } catch (Exception e) {
            request.setAttribute("msg", "Cannot load profile");
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/patient/profile.jsp").forward(request, response);
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

        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        try {
            // update email using UserDAO method
            UserBean u = new UserBean();
            u.setUserId(user.getUserId());
            u.setUsername(user.getUsername());
            u.setEmail(email);
            userDao.updateProfile(u);

            // update phone in patient profile
            pDao.updatePhoneByUserId(user.getUserId(), phone);

            // update password if user typed new password
            if (newPassword != null && !newPassword.isEmpty()) {
                if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
                    response.sendRedirect(request.getContextPath() + "/patient/profile?msg=Confirm+password+not+match");
                    return;
                }

                UserBean auth = userDao.authenticate(user.getEmail(), currentPassword);
                if (auth == null || auth.getUserId() != user.getUserId()) {
                    response.sendRedirect(request.getContextPath() + "/patient/profile?msg=Current+password+wrong");
                    return;
                }

                userDao.updatePassword(user.getUserId(), newPassword);
            }

            // update session email
            user.setEmail(email);
            session.setAttribute("loginUser", user);

            response.sendRedirect(request.getContextPath() + "/patient/profile?msg=Profile+updated");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/patient/profile?msg=Update+failed");
        }
    }
}
