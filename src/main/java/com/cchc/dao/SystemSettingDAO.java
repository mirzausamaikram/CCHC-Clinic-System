package com.cchc.dao;

import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemSettingDAO {

    public String getValue(String settingKey) throws SQLException {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, settingKey);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        }

        return null;
    }

    public boolean updateValue(String settingKey, String settingValue, int updatedByUserId) throws SQLException {
        String sql = "UPDATE system_settings SET setting_value = ?, updated_by_user_id = ? WHERE setting_key = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, settingValue);
            statement.setInt(2, updatedByUserId);
            statement.setString(3, settingKey);
            return statement.executeUpdate() > 0;
        }
    }

    // simple save setting (update first, then insert)
    public boolean setValue(String settingKey, String settingValue, int updatedByUserId) throws SQLException {
        boolean ok = updateValue(settingKey, settingValue, updatedByUserId);
        if (ok) {
            return true;
        }

        String sql = "INSERT INTO system_settings (setting_key, setting_value, updated_by_user_id) VALUES (?, ?, ?)";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, settingKey);
            statement.setString(2, settingValue);
            statement.setInt(3, updatedByUserId);
            return statement.executeUpdate() > 0;
        }
    }
}
