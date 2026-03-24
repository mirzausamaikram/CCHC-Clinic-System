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

    public List<ClinicBean> findAll() throws SQLException {
        String sql = "SELECT * FROM clinics ORDER BY clinic_name";
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

    // simple method name
    public List<ClinicBean> getAllClinics() throws SQLException {
        return findAllActive();
    }

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

    public int addClinic(ClinicBean clinic) throws SQLException {
        String sql = "INSERT INTO clinics (clinic_code, clinic_name, address_line1, city, state, phone, opening_time, closing_time, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, clinic.getClinicCode());
            ps.setString(2, clinic.getClinicName());
            ps.setString(3, clinic.getAddressLine1());
            ps.setString(4, clinic.getCity());
            ps.setString(5, clinic.getState());
            ps.setString(6, clinic.getPhone());
            ps.setTime(7, clinic.getOpeningTime());
            ps.setTime(8, clinic.getClosingTime());
            ps.setInt(9, clinic.isActive() ? 1 : 0);
            return ps.executeUpdate();
        }
    }

    public boolean updateHours(int clinicId, java.sql.Time openingTime, java.sql.Time closingTime) throws SQLException {
        String sql = "UPDATE clinics SET opening_time = ?, closing_time = ? WHERE clinic_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTime(1, openingTime);
            ps.setTime(2, closingTime);
            ps.setInt(3, clinicId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setActive(int clinicId, boolean active) throws SQLException {
        String sql = "UPDATE clinics SET is_active = ? WHERE clinic_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, clinicId);
            return ps.executeUpdate() > 0;
        }
    }

    private ClinicBean mapRow(ResultSet rs) throws SQLException {
        ClinicBean clinic = new ClinicBean();
        clinic.setClinicId(rs.getInt("clinic_id"));
        clinic.setClinicCode(rs.getString("clinic_code"));
        clinic.setClinicName(rs.getString("clinic_name"));
        clinic.setAddressLine1(rs.getString("address_line1"));
        clinic.setCity(rs.getString("city"));
        clinic.setState(rs.getString("state"));
        clinic.setPhone(rs.getString("phone"));
        clinic.setOpeningTime(rs.getTime("opening_time"));
        clinic.setClosingTime(rs.getTime("closing_time"));
        clinic.setActive(rs.getBoolean("is_active"));
        clinic.setCreatedAt(rs.getTimestamp("created_at"));
        return clinic;
    }
}
