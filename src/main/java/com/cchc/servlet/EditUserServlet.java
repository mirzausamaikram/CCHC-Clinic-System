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

@WebServlet(name = "EditUserServlet", urlPatterns = {"/admin/edit-user"})
public class EditUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserBean admin = (UserBean) session.getAttribute("loginUser");
        if (admin == null) {
            admin = (UserBean) session.getAttribute("user");
        }
        if (admin == null || !"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            UserDAO dao = new UserDAO();
            UserBean user = dao.getUserById(userId);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }
            request.setAttribute("user", user);
            request.getRequestDispatcher("/admin/edit_user.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        UserBean admin = (UserBean) session.getAttribute("loginUser");
        if (admin == null) {
            admin = (UserBean) session.getAttribute("user");
        }
        if (admin == null || !"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            if (email == null || email.isEmpty()) {
                request.setAttribute("error", "Email is required");
                doGet(request, response);
                return;
            }

            if (newPassword != null && !newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("error", "Passwords do not match");
                    doGet(request, response);
                    return;
                }
            }

            UserDAO dao = new UserDAO();
            boolean updated = dao.updateUser(userId, fullName, email, phone, 
                    (newPassword != null && !newPassword.isEmpty()) ? newPassword : null);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/users?msg=User updated successfully");
            } else {
                request.setAttribute("error", "Failed to update user");
                doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            doGet(request, response);
        }
    }
}
