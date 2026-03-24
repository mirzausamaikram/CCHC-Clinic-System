package com.cchc.dao;

import com.cchc.model.QueueEntryBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueueEntryDAO {

    public int getNextTokenNo(int clinicId, Date queueDate) throws SQLException {
        String sql = "SELECT COALESCE(MAX(token_no), 0) + 1 AS next_token FROM queue_entries WHERE clinic_id = ? AND queue_date = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clinicId);
            statement.setDate(2, queueDate);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_token");
                }
            }
        }

        return 1;
    }

    public int create(QueueEntryBean entry) throws SQLException {
        String sql = "INSERT INTO queue_entries "
                + "(clinic_id, service_id, patient_id, appointment_id, queue_date, token_no, priority_level, queue_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, entry.getClinicId());
            statement.setInt(2, entry.getServiceId());
            statement.setInt(3, entry.getPatientId());
            if (entry.getAppointmentId() == null) {
                statement.setNull(4, java.sql.Types.INTEGER);
            } else {
                statement.setInt(4, entry.getAppointmentId());
            }
            statement.setDate(5, entry.getQueueDate());
            statement.setInt(6, entry.getTokenNo());
            statement.setInt(7, entry.getPriorityLevel());
            statement.setString(8, entry.getQueueStatus());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return 0;
    }

    // simple duplicate check
    public boolean hasActiveTicket(int userId, int clinicId, int serviceId, Date today) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM queue_entries q "
                + "JOIN patient_profiles p ON q.patient_id = p.patient_id "
                + "WHERE p.user_id = ? AND q.clinic_id = ? AND q.service_id = ? AND q.queue_date = ? "
                + "AND q.queue_status IN ('WAITING','CALLED','IN_SERVICE')";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, clinicId);
            ps.setInt(3, serviceId);
            ps.setDate(4, today);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c") > 0;
                }
            }
        }
        return false;
    }

    public List<QueueEntryBean> findWaitingByClinicAndDate(int clinicId, Date queueDate) throws SQLException {
        String sql = "SELECT * FROM queue_entries WHERE clinic_id = ? AND queue_date = ? "
                + "AND queue_status IN ('WAITING','CALLED','IN_SERVICE') ORDER BY priority_level DESC, token_no ASC";
        List<QueueEntryBean> list = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clinicId);
            statement.setDate(2, queueDate);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public QueueEntryBean findById(int queueId) throws SQLException {
        String sql = "SELECT * FROM queue_entries WHERE queue_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, queueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean updateQueueStatus(int queueId, String queueStatus) throws SQLException {
        String sql = "UPDATE queue_entries SET queue_status = ? WHERE queue_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, queueStatus);
            statement.setInt(2, queueId);
            return statement.executeUpdate() > 0;
        }
    }

    // simple queue management - get today waiting queue for clinic
    public List<QueueEntryBean> getWaitingQueue(int clinicId) throws SQLException {
        Date today = new Date(System.currentTimeMillis());
        String sql = "SELECT * FROM queue_entries WHERE clinic_id = ? AND queue_date = ? "
            + "AND queue_status IN ('WAITING','CALLED','IN_SERVICE','MISSED') ORDER BY priority_level DESC, token_no ASC";
        List<QueueEntryBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setDate(2, today);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // simple queue management - update status by queue id
    public boolean updateStatus(int queueId, String status) throws SQLException {
        return updateQueueStatus(queueId, status);
    }

    // simple queue management - get next waiting entry (lowest token_no, WAITING)
    public QueueEntryBean getNextQueueNumber(int clinicId) throws SQLException {
        Date today = new Date(System.currentTimeMillis());
        String sql = "SELECT * FROM queue_entries WHERE clinic_id = ? AND queue_date = ? "
                + "AND queue_status = 'WAITING' ORDER BY priority_level DESC, token_no ASC LIMIT 1";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setDate(2, today);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // patient queue status for today
    public List<QueueEntryBean> findByPatientAndDate(int patientId, Date queueDate) throws SQLException {
        String sql = "SELECT * FROM queue_entries WHERE patient_id = ? AND queue_date = ? ORDER BY token_no ASC";
        List<QueueEntryBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setDate(2, queueDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    private QueueEntryBean mapRow(ResultSet rs) throws SQLException {
        QueueEntryBean bean = new QueueEntryBean();
        bean.setQueueId(rs.getInt("queue_id"));
        bean.setClinicId(rs.getInt("clinic_id"));
        bean.setServiceId(rs.getInt("service_id"));
        bean.setPatientId(rs.getInt("patient_id"));
        int appointmentId = rs.getInt("appointment_id");
        bean.setAppointmentId(rs.wasNull() ? null : appointmentId);
        bean.setQueueDate(rs.getDate("queue_date"));
        bean.setTokenNo(rs.getInt("token_no"));
        bean.setPriorityLevel(rs.getInt("priority_level"));
        bean.setQueueStatus(rs.getString("queue_status"));
        bean.setCalledTime(rs.getTimestamp("called_time"));
        bean.setServiceStartTime(rs.getTimestamp("service_start_time"));
        bean.setServiceEndTime(rs.getTimestamp("service_end_time"));
        bean.setCreatedAt(rs.getTimestamp("created_at"));
        return bean;
    }
}
