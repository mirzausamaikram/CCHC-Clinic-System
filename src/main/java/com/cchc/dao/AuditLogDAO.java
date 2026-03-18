package com.cchc.dao;

import com.cchc.model.AuditLogBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
