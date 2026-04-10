package com.cchc.dao;

import com.cchc.model.ServiceBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public List<ServiceBean> getAllServices() throws SQLException {
        return findAllActive();
    }

    public int addService(ServiceBean service) throws SQLException {
        String sql = "INSERT INTO services (service_code, service_name, service_description, default_duration_minutes, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, service.getServiceCode());
            ps.setString(2, service.getServiceName());
            ps.setString(3, service.getServiceDescription());
            ps.setInt(4, service.getDefaultDurationMinutes());
            ps.setInt(5, service.isActive() ? 1 : 0);
            return ps.executeUpdate();
        }
    }

    public List<ServiceBean> findAllActive() throws SQLException {
        String sql = "SELECT * FROM services WHERE is_active = 1 ORDER BY service_name";
        List<ServiceBean> services = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                services.add(mapRow(rs));
            }
        }

        return services;
    }

    public List<ServiceBean> findAll() throws SQLException {
        String sql = "SELECT * FROM services ORDER BY service_name";
        List<ServiceBean> services = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                services.add(mapRow(rs));
            }
        }

        return services;
    }

    public ServiceBean findById(int serviceId) throws SQLException {
        String sql = "SELECT * FROM services WHERE service_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }

        return null;
    }

    public boolean setActive(int serviceId, boolean active) throws SQLException {
        String sql = "UPDATE services SET is_active = ? WHERE service_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, serviceId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getQuotaPerSlot(int serviceId) {
        String sql = "SELECT quota_per_slot FROM services WHERE service_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int q = rs.getInt("quota_per_slot");
                    if (q > 0) {
                        return q;
                    }
                }
            }
        } catch (Exception e) {
        }
        return 5;
    }

    private ServiceBean mapRow(ResultSet rs) throws SQLException {
        ServiceBean service = new ServiceBean();
        service.setServiceId(rs.getInt("service_id"));
        service.setServiceCode(rs.getString("service_code"));
        service.setServiceName(rs.getString("service_name"));
        service.setServiceDescription(rs.getString("service_description"));
        service.setDefaultDurationMinutes(rs.getInt("default_duration_minutes"));
        try {
            int quota = rs.getInt("quota_per_slot");
            if (!rs.wasNull() && quota > 0) {
                service.setQuotaPerSlot(quota);
            }
        } catch (Exception e) {
            service.setQuotaPerSlot(5);
        }
        service.setActive(rs.getBoolean("is_active"));
        service.setCreatedAt(rs.getTimestamp("created_at"));
        return service;
    }
}
