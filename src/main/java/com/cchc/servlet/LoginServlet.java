package com.cchc.servlet;

import com.cchc.dao.UserDAO;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

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
