package com.cchc.dao;

import com.cchc.model.AuditLogBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditLogDAO {

    public void create(AuditLogBean auditLog) throws SQLException {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (auditLog.getUserId() == null) {
                statement.setNull(1, java.sql.Types.INTEGER);
            } else {
                statement.setInt(1, auditLog.getUserId());
            }
            statement.setString(2, auditLog.getAction());
            statement.setString(3, auditLog.getEntityType());
            statement.setString(4, auditLog.getEntityId());
            statement.setString(5, auditLog.getDetails());
            statement.executeUpdate();
        }
    }

    public List<Map<String, Object>> getRecentIssues(int limit) throws SQLException {
        String sql = "SELECT a.audit_id, a.user_id, u.username, a.action, a.entity_type, a.entity_id, a.details, a.created_at "
                + "FROM audit_logs a LEFT JOIN users u ON a.user_id = u.user_id "
                + "WHERE a.action = 'OPERATIONAL_ISSUE' ORDER BY a.audit_id DESC LIMIT ?";
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("auditId", rs.getLong("audit_id"));
                    row.put("userId", rs.getInt("user_id"));
                    row.put("username", rs.getString("username"));
                    row.put("action", rs.getString("action"));
                    row.put("entityType", rs.getString("entity_type"));
                    row.put("entityId", rs.getString("entity_id"));
                    row.put("details", rs.getString("details"));
                    row.put("createdAt", rs.getTimestamp("created_at"));
                    list.add(row);
                }
            }
        }

        return list;
    }

    public List<Map<String, Object>> getRepeatedNoShows(int minCount) throws SQLException {
        String sql = "SELECT user_id, COUNT(*) c FROM appointments WHERE status = 'NO_SHOW' GROUP BY user_id HAVING COUNT(*) >= ? ORDER BY c DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minCount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("userId", rs.getInt("user_id"));
                    row.put("count", rs.getInt("c"));
                    list.add(row);
                }
            }
        }

        return list;
    }

    public List<Map<String, Object>> getFrequentCancellations(int minCount) throws SQLException {
        String sql = "SELECT user_id, COUNT(*) c FROM appointments WHERE status = 'CANCELLED' GROUP BY user_id HAVING COUNT(*) >= ? ORDER BY c DESC";
        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minCount);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("userId", rs.getInt("user_id"));
                    row.put("count", rs.getInt("c"));
                    list.add(row);
                }
            }
        }

        return list;
    }
}
