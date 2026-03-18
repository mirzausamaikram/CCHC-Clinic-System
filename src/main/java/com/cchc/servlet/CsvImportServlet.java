package com.cchc.servlet;

import com.cchc.dao.ServiceDAO;
import com.cchc.model.ServiceBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "CsvImportServlet", urlPatterns = {"/admin/csv-import"})
@MultipartConfig
public class CsvImportServlet extends HttpServlet {

    private ServiceDAO dao = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // simple check
        HttpSession s = request.getSession(false);
        if (s == null || s.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!"ADMIN".equals(String.valueOf(s.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        request.getRequestDispatcher("/admin/import_csv.jsp").forward(request, response);
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
        if (!"ADMIN".equals(String.valueOf(s.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Part p = request.getPart("file");
        if (p == null || p.getSize() == 0) {
            request.setAttribute("msg", "Please choose csv file");
            request.getRequestDispatcher("/admin/import_csv.jsp").forward(request, response);
            return;
        }

        int ok = 0;
        int bad = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                if (first) {
                    first = false;
                    if (line.toLowerCase().contains("service_code")) {
                        continue;
                    }
                }

                String[] arr = line.split(",");
                if (arr.length < 4) {
                    bad++;
                    continue;
                }

                try {
                    ServiceBean sv = new ServiceBean();
                    sv.setServiceCode(arr[0].trim());
                    sv.setServiceName(arr[1].trim());
                    sv.setServiceDescription(arr[2].trim());

                    int mins = 10;
                    try { mins = Integer.parseInt(arr[3].trim()); } catch (Exception e) { mins = 10; }

                    sv.setDefaultDurationMinutes(mins);
                    sv.setActive(true);

                    int r = dao.addService(sv);
                    if (r > 0) {
                        ok++;
                    } else {
                        bad++;
                    }
                } catch (Exception e) {
                    // not sure if this works
                    bad++;
                }
            }
        }

        request.setAttribute("msg", "Import done. success=" + ok + ", failed=" + bad);
        request.getRequestDispatcher("/admin/import_csv.jsp").forward(request, response);
    }
}
