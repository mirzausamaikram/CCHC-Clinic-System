package com.cchc.dao;

import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {

    public int getUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) c FROM users";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("c");
            }
        }
        return 0;
    }

    public int getAppointmentCount() throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("c");
            }
        }
        return 0;
    }

    public int getTodayQueueCount() throws SQLException {
        String sql = "SELECT COUNT(*) c FROM queue_entries WHERE queue_date = CURDATE()";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("c");
            }
        }
        return 0;
    }

    public int getUnreadNotificationCount() throws SQLException {
        String sql = "SELECT COUNT(*) c FROM notifications WHERE is_read = 0";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("c");
            }
        }
        return 0;
    }

    // simple calculation for report - count booked (not cancelled) appointments for clinic+month+year(+service)
    private int getBookedCount(int clinicId, int serviceId, int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments a "
                + "WHERE a.clinic_id = ? AND MONTH(a.appointment_date) = ? AND YEAR(a.appointment_date) = ? "
                + "AND a.status NOT IN ('CANCELLED') "
                + "AND (? <= 0 OR a.service_id = ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ps.setInt(4, serviceId);
            ps.setInt(5, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c");
                }
            }
        }
        return 0;
    }

    // simple calculation for report - count no-show for clinic+month+year
    public int getNoShowSummary(int clinicId, int serviceId, int month, int year) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments a "
                + "WHERE a.clinic_id = ? AND MONTH(a.appointment_date) = ? AND YEAR(a.appointment_date) = ? "
                + "AND a.status = 'NO_SHOW' "
                + "AND (? <= 0 OR a.service_id = ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ps.setInt(4, serviceId);
            ps.setInt(5, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c");
                }
            }
        }
        return 0;
    }

    // simple calculation for report - total slots = sum(working_mins/duration) per service * days in month
    public int getTotalSlots(int clinicId, int serviceId, int month, int year) throws SQLException {
        // get clinic opening and closing time
        int workingMinutes = 0;
        String clinicSql = "SELECT opening_time, closing_time FROM clinics WHERE clinic_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(clinicSql)) {
            ps.setInt(1, clinicId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    java.sql.Time open = rs.getTime("opening_time");
                    java.sql.Time close = rs.getTime("closing_time");
                    // simple calculation for report - ms to minutes
                    long diff = close.getTime() - open.getTime();
                    workingMinutes = (int) (diff / 60000);
                }
            }
        }
        if (workingMinutes <= 0) {
            return 0;
        }

        // add up slots per day across all active services in this clinic
        int slotsPerDay = 0;
        String csSql = "SELECT duration_minutes FROM clinic_services WHERE clinic_id = ? AND is_active = 1 "
                + "AND (? <= 0 OR service_id = ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(csSql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setInt(3, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int dur = rs.getInt("duration_minutes");
                    if (dur > 0) {
                        slotsPerDay += workingMinutes / dur;
                    }
                }
            }
        }

        // get days in the given month/year
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month - 1, 1);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        return slotsPerDay * daysInMonth;
    }

    // simple calculation for report - utilisation rate = (booked / total slots) * 100
    public int getUtilisationRate(int clinicId, int serviceId, int month, int year) throws SQLException {
        int booked = getBookedCount(clinicId, serviceId, month, year);
        int total = getTotalSlots(clinicId, serviceId, month, year);
        if (total <= 0) {
            return 0;
        }
        // simple calculation for report
        return (booked * 100) / total;
    }

    // compatibility for old calls
    public int getNoShowSummary(int clinicId, int month, int year) throws SQLException {
        return getNoShowSummary(clinicId, 0, month, year);
    }

    public int getTotalSlots(int clinicId, int month, int year) throws SQLException {
        return getTotalSlots(clinicId, 0, month, year);
    }

    public int getUtilisationRate(int clinicId, int month, int year) throws SQLException {
        return getUtilisationRate(clinicId, 0, month, year);
    }

    public List<Map<String, Object>> getAppointmentRecords(int clinicId, int serviceId, int month, int year, String status) throws SQLException {
        String sql = "SELECT appointment_id, user_id, clinic_id, service_id, appointment_date, time_slot, status, notes "
                + "FROM appointments WHERE clinic_id = ? AND MONTH(appointment_date) = ? AND YEAR(appointment_date) = ? "
                + "AND (? <= 0 OR service_id = ?) "
                + "AND (? IS NULL OR ? = '' OR status = ?) "
                + "ORDER BY appointment_date DESC, appointment_id DESC";

        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            ps.setInt(4, serviceId);
            ps.setInt(5, serviceId);
            ps.setString(6, status);
            ps.setString(7, status);
            ps.setString(8, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("appointmentId", rs.getInt("appointment_id"));
                    row.put("userId", rs.getInt("user_id"));
                    row.put("clinicId", rs.getInt("clinic_id"));
                    row.put("serviceId", rs.getInt("service_id"));
                    row.put("appointmentDate", rs.getDate("appointment_date"));
                    row.put("timeSlot", rs.getString("time_slot"));
                    row.put("status", rs.getString("status"));
                    row.put("notes", rs.getString("notes"));
                    list.add(row);
                }
            }
        }
        return list;
    }
}
