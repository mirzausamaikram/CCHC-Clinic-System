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

@WebServlet(name = "UserManagementServlet", urlPatterns = {"/admin/user-management"})
public class UserManagementServlet extends HttpServlet {

    private UserDAO dao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null || !"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String act = request.getParameter("action");
        String idStr = request.getParameter("userId");
        String un = request.getParameter("username");
        String em = request.getParameter("email");
        String pw = request.getParameter("password");
        String roleStr = request.getParameter("roleId");
        String activeStr = request.getParameter("active");

        int id = 0;
        int roleId = 1;
        boolean active = true;
        try { id = Integer.parseInt(idStr); } catch (Exception e) { id = 0; }
        try { roleId = Integer.parseInt(roleStr); } catch (Exception e) { roleId = 1; }
        active = "1".equals(activeStr);

        try {
            if ("add".equals(act)) {
                dao.addSimpleUser(un, em, pw, roleId, active);
            } else if ("edit".equals(act)) {
                dao.updateSimpleUser(id, un, em, roleId, active);
            } else if ("delete".equals(act)) {
                dao.deleteUser(id);
            }
        } catch (Exception e) {
        }

        response.sendRedirect(request.getContextPath() + "/admin/user-management");
    }
}
