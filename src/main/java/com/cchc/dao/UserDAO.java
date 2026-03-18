package com.cchc.dao;

import com.cchc.model.UserBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public int addSimpleUser(String username, String email, String password, int roleId, boolean active) throws SQLException {
        // simple insert
        String sql = "INSERT INTO users (role_id, username, email, password_hash, is_active) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setInt(5, active ? 1 : 0);
            return ps.executeUpdate();
        }
    }

    public int updateSimpleUser(int userId, String username, String email, int roleId, boolean active) throws SQLException {
        // TODO later
        String sql = "UPDATE users SET role_id = ?, username = ?, email = ?, is_active = ? WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setInt(4, active ? 1 : 0);
            ps.setInt(5, userId);
            return ps.executeUpdate();
        }
    }

    public int deleteUser(int userId) throws SQLException {
        // not sure if this works if fk exists
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        }
    }

    public List<UserBean> getAllUsers() throws SQLException {
        // get data from database
        String sql = "SELECT user_id, role_id, username, email, password_hash, is_active, last_login, created_at, updated_at FROM users ORDER BY user_id DESC";
        List<UserBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserBean user = new UserBean();
                user.setUserId(rs.getInt("user_id"));
                user.setRoleId(rs.getInt("role_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setActive(rs.getBoolean("is_active"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                user.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(user);
            }
        }

        return list;
    }

    public boolean setUserActive(int userId, boolean active) throws SQLException {
        // TODO: finish this later
        String sql = "UPDATE users SET is_active = ? WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setInt(2, userId);
            int row = ps.executeUpdate();
            return row > 0;
        }
    }

    public UserBean authenticate(String usernameOrEmail, String passwordInput) throws SQLException {
        String sql = "SELECT u.user_id, u.role_id, r.role_name, u.username, u.email, u.password_hash, u.is_active "
                + "FROM users u "
                + "JOIN roles r ON r.role_id = u.role_id "
                + "WHERE (u.username = ? OR u.email = ?) AND u.is_active = 1";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, usernameOrEmail);
            statement.setString(2, usernameOrEmail);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password_hash");
                    if (!isPasswordMatch(passwordInput, storedPassword)) {
                        return null;
                    }

                    UserBean user = new UserBean();
                    user.setUserId(rs.getInt("user_id"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(storedPassword);
                    user.setActive(rs.getBoolean("is_active"));
                    return user;
                }
            }
        }

        return null;
    }

    public String getRoleNameByRoleId(int roleId) throws SQLException {
        String sql = "SELECT role_name FROM roles WHERE role_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roleId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role_name");
                }
            }
        }
        return null;
    }

    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    private boolean isPasswordMatch(String input, String storedPassword) {
        if (storedPassword == null || input == null) {
            return false;
        }
        return storedPassword.equals(input);
    }
}
