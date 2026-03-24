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

public class RegisterServlet extends HttpServlet {

    private UserDAO userDao = new UserDAO();
    private PatientProfileDAO patientDao = new PatientProfileDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");

        if (username == null || username.isEmpty()
                || email == null || email.isEmpty()
                || password == null || password.isEmpty()
                || fullName == null || fullName.isEmpty()) {
            request.setAttribute("msg", "Please fill all required fields");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            int patientRoleId = userDao.getRoleIdByName("PATIENT");
            if (patientRoleId <= 0) {
                request.setAttribute("msg", "Cannot find patient role");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            int userId = userDao.createUser(username, email, password, patientRoleId);
            if (userId <= 0) {
                request.setAttribute("msg", "Registration failed");
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            PatientProfileBean p = new PatientProfileBean();
            p.setUserId(userId);
            p.setFullName(fullName);
            p.setPhone(phone);
            patientDao.create(p);

            UserBean user = userDao.authenticate(username, password);
            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("loginUser", user);
                session.setAttribute("user", user);
                session.setAttribute("loginRole", "PATIENT");
                response.sendRedirect(request.getContextPath() + "/patient/dashboard.jsp");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } catch (Exception e) {
            request.setAttribute("msg", "Registration failed. Username or email may already exist.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}
