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
import java.util.List;

@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private UserDAO dao = new UserDAO();

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
            // get data from database
            List<UserBean> list = dao.getAllUsers();
            request.setAttribute("userList", list);
        } catch (SQLException e) {
            request.setAttribute("msg", "Error loading users");
        }

        request.getRequestDispatcher("/admin/user_management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null || !"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idStr = request.getParameter("userId");
        String activeStr = request.getParameter("active");

        int id = 0;
        try {
            id = Integer.parseInt(idStr);
        } catch (Exception e) {
            id = 0;
        }

        boolean active = "1".equals(activeStr);

        if (id > 0) {
            try {
                dao.setUserActive(id, active);
            } catch (SQLException e) {
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
