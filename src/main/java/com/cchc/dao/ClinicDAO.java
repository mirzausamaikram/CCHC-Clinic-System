package com.cchc.dao;

import com.cchc.model.ClinicBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClinicDAO {

    public List<ClinicBean> findAllActive() throws SQLException {
        String sql = "SELECT * FROM clinics WHERE is_active = 1 ORDER BY clinic_name";
        List<ClinicBean> clinics = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                clinics.add(mapRow(rs));
            }
        }

        return clinics;
    }

    public ClinicBean findById(int clinicId) throws SQLException {
        String sql = "SELECT * FROM clinics WHERE clinic_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clinicId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    private ClinicBean mapRow(ResultSet rs) throws SQLException {
        ClinicBean clinic = new ClinicBean();
        clinic.setClinicId(rs.getInt("clinic_id"));
        clinic.setClinicCode(rs.getString("clinic_code"));
        clinic.setClinicName(rs.getString("clinic_name"));
        clinic.setAddressLine1(rs.getString("address_line1"));
        clinic.setAddressLine2(rs.getString("address_line2"));
        clinic.setCity(rs.getString("city"));
        clinic.setState(rs.getString("state"));
        clinic.setPostalCode(rs.getString("postal_code"));
        clinic.setPhone(rs.getString("phone"));
        clinic.setEmail(rs.getString("email"));
        clinic.setOpeningTime(rs.getTime("opening_time"));
        clinic.setClosingTime(rs.getTime("closing_time"));
        clinic.setActive(rs.getBoolean("is_active"));
        clinic.setCreatedAt(rs.getTimestamp("created_at"));
        return clinic;
    }
}
