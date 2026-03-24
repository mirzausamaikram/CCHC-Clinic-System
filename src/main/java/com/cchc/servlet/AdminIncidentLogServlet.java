package com.cchc.servlet;

import com.cchc.dao.AuditLogDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminIncidentLogServlet extends HttpServlet {

    private AuditLogDAO auditDao = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!"ADMIN".equals(String.valueOf(session.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Map<String, Object>> issues = auditDao.getRecentIssues(100);
            List<Map<String, Object>> noShows = auditDao.getRepeatedNoShows(2);
            List<Map<String, Object>> cancellations = auditDao.getFrequentCancellations(2);

            request.setAttribute("issueList", issues);
            request.setAttribute("noShowList", noShows);
            request.setAttribute("cancelList", cancellations);
        } catch (Exception e) {
            request.setAttribute("msg", "Cannot load incident logs");
        }

        request.getRequestDispatcher("/admin/incident-logs.jsp").forward(request, response);
    }
}
