package com.cchc.dao;

import com.cchc.model.RoleBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<RoleBean> findAll() throws SQLException {
        String sql = "SELECT role_id, role_name, role_description FROM roles ORDER BY role_name";
        List<RoleBean> roles = new ArrayList<>();

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                RoleBean role = new RoleBean();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setRoleDescription(rs.getString("role_description"));
                roles.add(role);
            }
        }

        return roles;
    }

    public RoleBean findById(int roleId) throws SQLException {
        String sql = "SELECT role_id, role_name, role_description FROM roles WHERE role_id = ?";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roleId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    RoleBean role = new RoleBean();
                    role.setRoleId(rs.getInt("role_id"));
                    role.setRoleName(rs.getString("role_name"));
                    role.setRoleDescription(rs.getString("role_description"));
                    return role;
                }
            }
        }

        return null;
    }
}
