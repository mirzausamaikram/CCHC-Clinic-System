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

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/users"})
public class AdminServlet extends HttpServlet {

    private UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // simple admin page
        HttpSession s = request.getSession(false);
        if (s == null || s.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String role = String.valueOf(s.getAttribute("loginRole"));
        if (!"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // get data from database
            List<UserBean> list = dao.getAllUsers();
            request.setAttribute("list", list);
        } catch (SQLException e) {
            request.setAttribute("msg", "Cannot load users");
        }

        request.getRequestDispatcher("/admin/user_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // TODO: make better later
        HttpSession s = request.getSession(false);
        if (s == null || s.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String role = String.valueOf(s.getAttribute("loginRole"));
        if (!"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String act = request.getParameter("action");
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("userId"));
        } catch (Exception e) {
            id = 0;
        }

        try {
            if ("add".equals(act)) {
                String un = request.getParameter("username");
                String em = request.getParameter("email");
                String pw = request.getParameter("password");
                int r = 1;
                try {
                    r = Integer.parseInt(request.getParameter("roleId"));
                } catch (Exception e) {
                    r = 1;
                }
                dao.addSimpleUser(un, em, pw, r, true);
            } else if ("delete".equals(act)) {
                if (id > 0) {
                    dao.deleteUser(id);
                }
            }
        } catch (Exception e) {
            // not sure if this works
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
