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
import java.util.List;

public class AppointmentDAO {

    public int getTotalCount() throws SQLException {
        // simple count
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
        // simple count
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
        // simple count
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
        // get data from database
        String sql = "SELECT a.* FROM appointments a "
                + "JOIN patient_profiles p ON a.patient_id = p.patient_id "
                + "WHERE p.user_id = ? ORDER BY a.appointment_date DESC, a.start_time DESC";

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
        // simple update
        String sql = "UPDATE appointments a "
                + "JOIN patient_profiles p ON a.patient_id = p.patient_id "
                + "SET a.status = 'CANCELLED' "
                + "WHERE a.appointment_id = ? AND p.user_id = ? "
                + "AND a.status NOT IN ('COMPLETED','CANCELLED')";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            ps.setInt(2, userId);
            int row = ps.executeUpdate();
            return row > 0;
        }
    }

    public boolean updateAppointmentDateAndTime(int appointmentId, int userId, Date newDate, Time newStart, Time newEnd) throws SQLException {
        // simple reschedule for patient
        String sql = "UPDATE appointments a "
                + "JOIN patient_profiles p ON a.patient_id = p.patient_id "
                + "SET a.appointment_date = ?, a.start_time = ?, a.end_time = ? "
                + "WHERE a.appointment_id = ? AND p.user_id = ? "
                + "AND a.status NOT IN ('COMPLETED','CANCELLED')";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, newDate);
            ps.setTime(2, newStart);
            ps.setTime(3, newEnd);
            ps.setInt(4, appointmentId);
            ps.setInt(5, userId);
            int row = ps.executeUpdate();
            return row > 0;
        }
    }

    public int create(AppointmentBean appointment) throws SQLException {
        String sql = "INSERT INTO appointments "
                + "(patient_id, clinic_service_id, assigned_staff_id, appointment_date, start_time, end_time, booking_channel, status, notes, created_by_user_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, appointment.getPatientId());
            statement.setInt(2, appointment.getClinicServiceId());

            if (appointment.getAssignedStaffId() == null) {
                statement.setNull(3, java.sql.Types.INTEGER);
            } else {
                statement.setInt(3, appointment.getAssignedStaffId());
            }

            statement.setDate(4, appointment.getAppointmentDate());
            statement.setTime(5, appointment.getStartTime());
            statement.setTime(6, appointment.getEndTime());
            statement.setString(7, appointment.getBookingChannel());
            statement.setString(8, appointment.getStatus());
            statement.setString(9, appointment.getNotes());
            statement.setInt(10, appointment.getCreatedByUserId());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return 0;
    }

    public List<AppointmentBean> findByPatientId(int patientId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC, start_time DESC";
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

    public List<AppointmentBean> findByClinicAndDate(int clinicId, Date appointmentDate) throws SQLException {
        String sql = "SELECT a.* FROM appointments a "
                + "JOIN clinic_services cs ON a.clinic_service_id = cs.clinic_service_id "
                + "WHERE cs.clinic_id = ? AND a.appointment_date = ? "
                + "ORDER BY a.start_time";
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

    public boolean updateStatus(int appointmentId, String status) throws SQLException {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, appointmentId);
            return statement.executeUpdate() > 0;
        }
    }

    private AppointmentBean mapRow(ResultSet rs) throws SQLException {
        AppointmentBean bean = new AppointmentBean();
        bean.setAppointmentId(rs.getInt("appointment_id"));
        bean.setPatientId(rs.getInt("patient_id"));
        bean.setClinicServiceId(rs.getInt("clinic_service_id"));
        int assignedStaffId = rs.getInt("assigned_staff_id");
        bean.setAssignedStaffId(rs.wasNull() ? null : assignedStaffId);
        bean.setAppointmentDate(rs.getDate("appointment_date"));
        bean.setStartTime(rs.getTime("start_time"));
        bean.setEndTime(rs.getTime("end_time"));
        bean.setBookingChannel(rs.getString("booking_channel"));
        bean.setStatus(rs.getString("status"));
        bean.setNotes(rs.getString("notes"));
        bean.setCreatedByUserId(rs.getInt("created_by_user_id"));
        bean.setCreatedAt(rs.getTimestamp("created_at"));
        bean.setUpdatedAt(rs.getTimestamp("updated_at"));
        return bean;
    }
}
