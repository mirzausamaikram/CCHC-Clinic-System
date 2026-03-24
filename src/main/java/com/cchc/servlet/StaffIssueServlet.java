package com.cchc.servlet;

import com.cchc.dao.AuditLogDAO;
import com.cchc.model.AuditLogBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class StaffIssueServlet extends HttpServlet {

    private AuditLogDAO auditDao = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"STAFF".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String msg = request.getParameter("msg");
        if (msg != null && !msg.isEmpty()) {
            request.setAttribute("msg", msg);
        }

        request.getRequestDispatcher("/staff/report-issue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"STAFF".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserBean user = (UserBean) session.getAttribute("loginUser");
        String issueType = request.getParameter("issueType");
        String details = request.getParameter("details");

        if (issueType == null || issueType.isEmpty() || details == null || details.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/staff/report-issue?msg=Please+fill+all+fields");
            return;
        }

        try {
            AuditLogBean log = new AuditLogBean();
            log.setUserId(user.getUserId());
            log.setAction("OPERATIONAL_ISSUE");
            log.setEntityType("STAFF_ISSUE");
            log.setEntityId(issueType);
            log.setDetails(details);
            auditDao.create(log);

            response.sendRedirect(request.getContextPath() + "/staff/report-issue?msg=Issue+reported");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/staff/report-issue?msg=Cannot+save+issue");
        }
    }
}
