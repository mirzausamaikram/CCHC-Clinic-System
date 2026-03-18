package com.cchc.servlet;

import com.cchc.dao.NotificationDAO;
import com.cchc.model.NotificationBean;
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

@WebServlet(name = "NotificationServlet", urlPatterns = {"/notifications"})
public class NotificationServlet extends HttpServlet {

    private NotificationDAO dao = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // simple check
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");
        try {
            // get data from database
            List<NotificationBean> list = dao.findByUserId(user.getUserId());
            request.setAttribute("notifications", list);
            request.getRequestDispatcher("/notifications.jsp").forward(request, response);
        } catch (Exception ex) {
            request.setAttribute("errorMessage", "Database error");
            request.getRequestDispatcher("/notifications.jsp").forward(request, response);
        }
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
        int id = 0;
        try {
            id = Integer.parseInt(request.getParameter("notificationId"));
        } catch (Exception e) {
            id = 0;
        }

        if (id > 0) {
            try {
                dao.markAsRead(id, user.getUserId());
            } catch (Exception ex) {
                request.setAttribute("errorMessage", "Database error");
                doGet(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/notifications");
    }
}
