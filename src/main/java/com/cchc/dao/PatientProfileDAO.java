package com.cchc.dao;

import com.cchc.model.PatientProfileBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientProfileDAO {

    public List<PatientProfileBean> findAll() throws SQLException {
        String sql = "SELECT * FROM patient_profiles ORDER BY full_name";
        List<PatientProfileBean> list = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    public PatientProfileBean findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM patient_profiles WHERE user_id = ?";

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

    public PatientProfileBean findById(int patientId) throws SQLException {
        String sql = "SELECT * FROM patient_profiles WHERE patient_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, patientId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public int create(PatientProfileBean profile) throws SQLException {
        String sql = "INSERT INTO patient_profiles "
                + "(user_id, ic_passport_no, full_name, date_of_birth, gender, phone, address, emergency_contact_name, emergency_contact_phone) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, profile.getUserId());
            statement.setString(2, profile.getIcPassportNo());
            statement.setString(3, profile.getFullName());
            statement.setDate(4, profile.getDateOfBirth());
            statement.setString(5, profile.getGender());
            statement.setString(6, profile.getPhone());
            statement.setString(7, profile.getAddress());
            statement.setString(8, profile.getEmergencyContactName());
            statement.setString(9, profile.getEmergencyContactPhone());

            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return 0;
    }

    private PatientProfileBean mapRow(ResultSet rs) throws SQLException {
        PatientProfileBean profile = new PatientProfileBean();
        profile.setPatientId(rs.getInt("patient_id"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setIcPassportNo(rs.getString("ic_passport_no"));
        profile.setFullName(rs.getString("full_name"));
        profile.setDateOfBirth(rs.getDate("date_of_birth"));
        profile.setGender(rs.getString("gender"));
        profile.setPhone(rs.getString("phone"));
        profile.setAddress(rs.getString("address"));
        profile.setEmergencyContactName(rs.getString("emergency_contact_name"));
        profile.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        profile.setCreatedAt(rs.getTimestamp("created_at"));
        return profile;
    }
}
