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

    public boolean upsertClinicForUser(int userId, int clinicId, String fullName) throws SQLException {
        StaffProfileBean existing = findByUserId(userId);
        if (existing == null) {
            return createSimple(userId, clinicId, fullName);
        }
        String sql = "UPDATE staff_profiles SET clinic_id = ?, full_name = ? WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setString(2, fullName == null || fullName.isEmpty() ? ("Staff #" + userId) : fullName);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean createSimple(int userId, int clinicId, String fullName) throws SQLException {
        String sql = "INSERT INTO staff_profiles (user_id, clinic_id, employee_no, full_name, phone, position_title) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, clinicId);
            ps.setString(3, "EMP-U" + userId);
            ps.setString(4, fullName == null || fullName.isEmpty() ? ("Staff #" + userId) : fullName);
            ps.setString(5, "-");
            ps.setString(6, "Staff");
            return ps.executeUpdate() > 0;
        }
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
