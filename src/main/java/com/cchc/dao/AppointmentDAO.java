package com.cchc.dao;

import com.cchc.model.AppointmentBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppointmentDAO {

    public int getTotalCount() throws SQLException {
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

    public int getCompletedCount() throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments WHERE status = 'COMPLETED'";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("c");
            }
        }
        return 0;
    }

    public int getNoShowCount() throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments WHERE status = 'NO_SHOW'";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("c");
            }
        }
        return 0;
    }

    public List<AppointmentBean> getMyAppointments(int userId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE user_id = ? ORDER BY appointment_date DESC, appointment_id DESC";

        List<AppointmentBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AppointmentBean appt = mapRow(rs);
                    list.add(appt);
                }
            }
        }

        return list;
    }

    public boolean cancelAppointment(int appointmentId, int userId) throws SQLException {
        String sql = "UPDATE appointments SET status = 'CANCELLED' "
            + "WHERE appointment_id = ? AND user_id = ? "
            + "AND status NOT IN ('COMPLETED','CANCELLED')";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ps.setInt(2, userId);
            int row = ps.executeUpdate();
            return row > 0;
        }
    }

    public boolean updateAppointmentDateAndTime(int appointmentId, int userId, Date newDate, Time newStart, Time newEnd) throws SQLException {
        String sql = "UPDATE appointments SET appointment_date = ?, time_slot = ? "
            + "WHERE appointment_id = ? AND user_id = ? "
            + "AND status NOT IN ('COMPLETED','CANCELLED')";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String s1 = newStart == null ? "" : newStart.toString();
            String s2 = newEnd == null ? "" : newEnd.toString();
            if (s1.length() >= 5) {
                s1 = s1.substring(0, 5);
            }
            if (s2.length() >= 5) {
                s2 = s2.substring(0, 5);
            }
            String slot = s1;
            if (!s2.isEmpty()) {
                slot = s1 + "-" + s2;
            }

            ps.setDate(1, newDate);
            ps.setString(2, slot);
            ps.setInt(3, appointmentId);
            ps.setInt(4, userId);
            int row = ps.executeUpdate();
            return row > 0;
        }
    }

    public int create(AppointmentBean appointment) throws SQLException {
        String sql = "INSERT INTO appointments "
                + "(user_id, clinic_id, service_id, appointment_date, time_slot, status, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, appointment.getUserId());
            statement.setInt(2, appointment.getClinicId());
            statement.setInt(3, appointment.getServiceId());
            statement.setDate(4, appointment.getAppointmentDate() == null ? null : Date.valueOf(appointment.getAppointmentDate()));
            statement.setString(5, appointment.getTimeSlot());
            statement.setString(6, appointment.getStatus());
            statement.setString(7, appointment.getNotes());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return 0;
    }

    public int bookAppointment(AppointmentBean appointment) throws SQLException {
        return create(appointment);
    }

    public List<AppointmentBean> findByPatientId(int patientId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE user_id = ? ORDER BY appointment_date DESC, appointment_id DESC";
        List<AppointmentBean> list = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, patientId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public AppointmentBean findById(int appointmentId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE appointment_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<AppointmentBean> findByClinicAndDate(int clinicId, Date appointmentDate) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE clinic_id = ? AND appointment_date = ? ORDER BY appointment_id";
        List<AppointmentBean> list = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clinicId);
            statement.setDate(2, appointmentDate);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public List<AppointmentBean> getAllBookingsForStaff(int clinicId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE clinic_id = ? AND status IN ('BOOKED', 'PENDING') ORDER BY appointment_date ASC, appointment_id ASC";
        List<AppointmentBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public List<AppointmentBean> getTodayAppointments(int clinicId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE clinic_id = ? AND appointment_date = CURDATE() ORDER BY appointment_id ASC";
        List<AppointmentBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public List<AppointmentBean> findPendingByClinic(int clinicId) throws SQLException {
        return getAllBookingsForStaff(clinicId);
    }

    public boolean approveBooking(int appointmentId) throws SQLException {
        String sql = "UPDATE appointments SET status = 'CONFIRMED' WHERE appointment_id = ? AND status = 'BOOKED'";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean rejectBooking(int appointmentId, String reason) throws SQLException {
        String sql = "UPDATE appointments SET status = 'REJECTED', notes = CONCAT(IFNULL(notes,''), ' [Rejected: ', ?, ']') "
            + "WHERE appointment_id = ? AND status = 'BOOKED'";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reason == null ? "No reason" : reason);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countActiveBookingsByPatient(int patientId) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments WHERE user_id = ? "
                + "AND status IN ('BOOKED','CONFIRMED','PENDING')";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c");
                }
            }
        }
        return 0;
    }

    public int countByClinicServiceAndDate(int clinicServiceId, Date d) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments WHERE service_id = ? AND appointment_date = ? "
                + "AND status IN ('BOOKED','CONFIRMED','PENDING')";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicServiceId);
            ps.setDate(2, d);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c");
                }
            }
        }
        return 0;
    }

    public int countByClinicAndServiceAndDate(int clinicId, int serviceId, Date d) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments WHERE clinic_id = ? AND service_id = ? AND appointment_date = ? "
                + "AND status IN ('BOOKED','CONFIRMED','PENDING')";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setDate(3, d);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c");
                }
            }
        }
        return 0;
    }

    public int countByClinicServiceDateAndSlotExcluding(int clinicId, int serviceId, Date d, String timeSlot, int excludeAppointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM appointments WHERE clinic_id = ? AND service_id = ? "
                + "AND appointment_date = ? AND time_slot = ? AND appointment_id <> ? "
                + "AND status IN ('BOOKED','CONFIRMED','ARRIVED','COMPLETED')";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setInt(2, serviceId);
            ps.setDate(3, d);
            ps.setString(4, timeSlot);
            ps.setInt(5, excludeAppointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c");
                }
            }
        }
        return 0;
    }

    public boolean updateStatus(int appointmentId, String status) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, appointmentId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean cancelByClinic(int appointmentId, String reason) throws SQLException {
        String sql = "UPDATE appointments SET status = 'CANCELLED', notes = CONCAT(IFNULL(notes,''), ' [Cancelled by clinic: ', ?, ']') "
                + "WHERE appointment_id = ? AND status NOT IN ('COMPLETED','CANCELLED')";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, reason == null || reason.isEmpty() ? "No reason" : reason);
            ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        }
    }

    public Set<String> getBookedStartTimes(int clinicId, Date appointmentDate) throws SQLException {
        String sql = "SELECT time_slot FROM appointments WHERE clinic_id = ? AND appointment_date = ? "
                + "AND status IN ('BOOKED','CONFIRMED','PENDING','ARRIVED')";
        Set<String> set = new HashSet<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setDate(2, appointmentDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String slot = rs.getString("time_slot");
                    if (slot != null && slot.contains("-")) {
                        set.add(slot.split("-")[0]);
                    } else if (slot != null && !slot.isEmpty()) {
                        set.add(slot);
                    }
                }
            }
        }

        return set;
    }

    public List<AppointmentBean> getTomorrowAppointments(int patientId) throws SQLException {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        Date tomorrow = new Date(cal.getTimeInMillis());

        String sql = "SELECT * FROM appointments WHERE user_id = ? AND appointment_date = ? "
                + "AND status NOT IN ('CANCELLED','COMPLETED','NO_SHOW')";
        List<AppointmentBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setDate(2, tomorrow);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private AppointmentBean mapRow(ResultSet rs) throws SQLException {
        AppointmentBean bean = new AppointmentBean();
        bean.setAppointmentId(rs.getInt("appointment_id"));
        bean.setUserId(rs.getInt("user_id"));
        bean.setClinicId(rs.getInt("clinic_id"));
        bean.setServiceId(rs.getInt("service_id"));
        Date d = rs.getDate("appointment_date");
        if (d != null) {
            bean.setAppointmentDate(d.toLocalDate());
        }
        bean.setTimeSlot(rs.getString("time_slot"));
        bean.setStatus(rs.getString("status"));
        bean.setNotes(rs.getString("notes"));
        bean.setCreatedAt(rs.getTimestamp("created_at"));
        return bean;
    }
}
