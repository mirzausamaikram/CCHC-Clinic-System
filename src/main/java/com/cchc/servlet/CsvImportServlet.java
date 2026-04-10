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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "CsvImportServlet", urlPatterns = {"/admin/csv-import"})
@MultipartConfig
public class CsvImportServlet extends HttpServlet {

    private ServiceDAO dao = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession s = request.getSession(false);
        if (s == null || s.getAttribute("loginUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (!"ADMIN".equals(String.valueOf(s.getAttribute("loginRole")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String act = request.getParameter("action");
        if ("sample".equals(act)) {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=sample_services.csv");
            try (PrintWriter out = response.getWriter()) {
                out.println("service_code,service_name,service_description,duration,clinic_id,quota_per_slot,walkin_enabled");
                out.println("SRV-100,General Checkup,Simple checkup,20,1,5,1");
            }
            return;
        }

        request.getRequestDispatcher("/admin/import_csv.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            p = request.getPart("csvFile");
        }
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
                    bad++;
                }
            }
        } catch (Exception e) {
            request.setAttribute("msg", "Import error");
            request.getRequestDispatcher("/admin/import_csv.jsp").forward(request, response);
            return;
        }

        if (ok > 0) {
            request.setAttribute("msg", ok + " services imported successfully!");
        } else {
            request.setAttribute("msg", "Import failed. bad rows = " + bad);
        }
        request.getRequestDispatcher("/admin/import_csv.jsp").forward(request, response);
    }
}
