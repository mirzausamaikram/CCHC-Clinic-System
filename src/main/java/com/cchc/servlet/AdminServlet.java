package com.cchc.servlet;

import com.cchc.dao.UserDAO;
import com.cchc.dao.ClinicDAO;
import com.cchc.dao.StaffProfileDAO;
import com.cchc.model.ClinicBean;
import com.cchc.model.StaffProfileBean;
import com.cchc.model.UserBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin/users"})
public class AdminServlet extends HttpServlet {

    private UserDAO dao = new UserDAO();
    private ClinicDAO clinicDao = new ClinicDAO();
    private StaffProfileDAO staffDao = new StaffProfileDAO();

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
            Map<Integer, String> roleNameMap = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                UserBean x = list.get(i);
                if (!roleNameMap.containsKey(x.getRoleId())) {
                    roleNameMap.put(x.getRoleId(), dao.getRoleNameByRoleId(x.getRoleId()));
                }
            }
            request.setAttribute("list", list);
            request.setAttribute("roleNameMap", roleNameMap);

            Map<Integer, Integer> staffClinicMap = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                UserBean x = list.get(i);
                if (x.getRoleId() == 2) {
                    StaffProfileBean sp = staffDao.findByUserId(x.getUserId());
                    if (sp != null) {
                        staffClinicMap.put(x.getUserId(), sp.getClinicId());
                    }
                }
            }
            request.setAttribute("staffClinicMap", staffClinicMap);

            List<ClinicBean> clinics = clinicDao.findAllActive();
            request.setAttribute("clinicList", clinics);
        } catch (SQLException e) {
            request.setAttribute("list", new java.util.ArrayList<UserBean>());
        }

        request.getRequestDispatcher("/admin/user_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
                int clinicId = 0;
                try {
                    r = Integer.parseInt(request.getParameter("roleId"));
                } catch (Exception e) {
                    r = 1;
                }
                try {
                    clinicId = Integer.parseInt(request.getParameter("clinicId"));
                } catch (Exception e) {
                    clinicId = 0;
                }

                int newUserId = dao.createUser(un, em, pw, r);
                if (newUserId > 0 && r == 2 && clinicId > 0) {
                    staffDao.upsertClinicForUser(newUserId, clinicId, un);
                }
            } else if ("edit".equals(act)) {
                String un = request.getParameter("username");
                String em = request.getParameter("email");
                int r = 1;
                int clinicId = 0;
                try {
                    r = Integer.parseInt(request.getParameter("roleId"));
                } catch (Exception e) {
                    r = 1;
                }
                try {
                    clinicId = Integer.parseInt(request.getParameter("clinicId"));
                } catch (Exception e) {
                    clinicId = 0;
                }

                if (id > 0) {
                    dao.updateSimpleUser(id, un, em, r, true);
                    if (r == 2 && clinicId > 0) {
                        staffDao.upsertClinicForUser(id, clinicId, un);
                    }
                }
            } else if ("delete".equals(act)) {
                if (id > 0) {
                    try {
                        dao.deleteUser(id);
                    } catch (Exception ex) {
                        dao.setUserActive(id, false);
                    }
                }
            }
        } catch (Exception e) {
        }

        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
