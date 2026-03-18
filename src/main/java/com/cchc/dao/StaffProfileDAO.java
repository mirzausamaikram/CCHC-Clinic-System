package com.cchc.dao;

import com.cchc.model.StaffProfileBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffProfileDAO {

    public StaffProfileBean findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM staff_profiles WHERE user_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public List<StaffProfileBean> findByClinicId(int clinicId) throws SQLException {
        String sql = "SELECT * FROM staff_profiles WHERE clinic_id = ? ORDER BY full_name";
        List<StaffProfileBean> list = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clinicId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }

        return list;
    }

    private StaffProfileBean mapRow(ResultSet rs) throws SQLException {
        StaffProfileBean profile = new StaffProfileBean();
        profile.setStaffId(rs.getInt("staff_id"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setClinicId(rs.getInt("clinic_id"));
        profile.setEmployeeNo(rs.getString("employee_no"));
        profile.setFullName(rs.getString("full_name"));
        profile.setPhone(rs.getString("phone"));
        profile.setPositionTitle(rs.getString("position_title"));
        profile.setCreatedAt(rs.getTimestamp("created_at"));
        return profile;
    }
}
