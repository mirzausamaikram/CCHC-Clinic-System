package com.cchc.dao;

import com.cchc.model.ClinicServiceBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClinicServiceDAO {

    public List<ClinicServiceBean> findByClinicId(int clinicId) throws SQLException {
        String sql = "SELECT * FROM clinic_services WHERE clinic_id = ? AND is_active = 1 ORDER BY service_id";
        List<ClinicServiceBean> list = new ArrayList<>();

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

    public ClinicServiceBean findById(int clinicServiceId) throws SQLException {
        String sql = "SELECT * FROM clinic_services WHERE clinic_service_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, clinicServiceId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    private ClinicServiceBean mapRow(ResultSet rs) throws SQLException {
        ClinicServiceBean bean = new ClinicServiceBean();
        bean.setClinicServiceId(rs.getInt("clinic_service_id"));
        bean.setClinicId(rs.getInt("clinic_id"));
        bean.setServiceId(rs.getInt("service_id"));
        bean.setDurationMinutes(rs.getInt("duration_minutes"));
        bean.setFee(rs.getBigDecimal("fee"));
        bean.setActive(rs.getBoolean("is_active"));
        bean.setCreatedAt(rs.getTimestamp("created_at"));
        return bean;
    }
}
