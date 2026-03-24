package com.cchc.dao;

import com.cchc.model.NotificationBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) c FROM notifications";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("c");
            }
        }
        return 0;
    }

    public int countUnreadByUserId(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM notifications WHERE user_id = ? AND is_read = 0";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }

        return 0;
    }

    public int create(NotificationBean notification) throws SQLException {
        String sql = "INSERT INTO notifications "
                + "(user_id, title, message, notification_type, related_appointment_id, is_read) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, notification.getUserId());
            statement.setString(2, notification.getTitle());
            statement.setString(3, notification.getMessage());
            statement.setString(4, notification.getNotificationType());
            if (notification.getRelatedAppointmentId() == null) {
                statement.setNull(5, java.sql.Types.INTEGER);
            } else {
                statement.setInt(5, notification.getRelatedAppointmentId());
            }
            statement.setBoolean(6, notification.isRead());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return 0;
    }

    public List<NotificationBean> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<NotificationBean> list = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    public boolean markAsRead(int notificationId, int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ? AND user_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, notificationId);
            statement.setInt(2, userId);
            return statement.executeUpdate() > 0;
        }
    }

    // simple notification system - get last 5 notifications for user
    // DATE_FORMAT avoids JDBC timezone conversion errors on created_at
    public List<NotificationBean> getRecentNotifications(int userId) throws SQLException {
        String sql = "SELECT notification_id, user_id, title, message, notification_type, "
                + "related_appointment_id, is_read, "
                + "DATE_FORMAT(created_at, '%Y-%m-%d %H:%i:%s') AS created_str "
                + "FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT 5";
        List<NotificationBean> list = new ArrayList<>();
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NotificationBean bean = new NotificationBean();
                    bean.setNotificationId(rs.getInt("notification_id"));
                    bean.setUserId(rs.getInt("user_id"));
                    bean.setTitle(rs.getString("title"));
                    bean.setMessage(rs.getString("message"));
                    bean.setNotificationType(rs.getString("notification_type"));
                    int rid = rs.getInt("related_appointment_id");
                    bean.setRelatedAppointmentId(rs.wasNull() ? null : rid);
                    bean.setRead(rs.getBoolean("is_read"));
                    // read timestamp as string - no JDBC conversion needed
                    String createdStr = rs.getString("created_str");
                    try {
                        bean.setCreatedAt(java.sql.Timestamp.valueOf(createdStr));
                    } catch (Exception ex) {
                        bean.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                    }
                    list.add(bean);
                }
            }
        }
        return list;
    }

    // simple notification system - quick helper to create a notification
    public void createNotification(int userId, String type, String message) throws SQLException {
        NotificationBean n = new NotificationBean();
        n.setUserId(userId);
        n.setTitle(type);
        n.setMessage(message);
        n.setNotificationType(type);
        n.setRelatedAppointmentId(null);
        n.setRead(false);
        create(n);
    }

    // simple notification system - check if reminder already exists for an appointment
    public boolean reminderExists(int userId, int appointmentId) throws SQLException {
        String sql = "SELECT COUNT(*) c FROM notifications "
            + "WHERE user_id = ? AND related_appointment_id = ? "
            + "AND notification_type IN ('Reminder', 'Reminder for upcoming appointment')";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("c") > 0;
                }
            }
        }
        return false;
    }

    private NotificationBean mapRow(ResultSet rs) throws SQLException {
        NotificationBean bean = new NotificationBean();
        bean.setNotificationId(rs.getInt("notification_id"));
        bean.setUserId(rs.getInt("user_id"));
        bean.setTitle(rs.getString("title"));
        bean.setMessage(rs.getString("message"));
        bean.setNotificationType(rs.getString("notification_type"));
        int relatedAppointmentId = rs.getInt("related_appointment_id");
        bean.setRelatedAppointmentId(rs.wasNull() ? null : relatedAppointmentId);
        bean.setRead(rs.getBoolean("is_read"));
        // read timestamp as string to avoid JDBC timezone conversion errors
        try {
            String s = rs.getString("created_at");
            bean.setCreatedAt(s != null ? java.sql.Timestamp.valueOf(s) : new java.sql.Timestamp(System.currentTimeMillis()));
        } catch (Exception e2) {
            bean.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        }
        return bean;
    }
}
