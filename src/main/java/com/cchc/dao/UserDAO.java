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
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate();
        }
    }

    public List<UserBean> getAllUsers() throws SQLException {
        String sql = "SELECT user_id, role_id, username, email, full_name, phone, password_hash, is_active, created_at FROM users ORDER BY user_id DESC";
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
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setActive(rs.getBoolean("is_active"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(user);
            }
        }

        return list;
    }

    public UserBean getUserById(int userId) throws SQLException {
        String sql = "SELECT user_id, role_id, username, email, full_name, phone, password_hash, is_active, created_at FROM users WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserBean user = new UserBean();
                    user.setUserId(rs.getInt("user_id"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setActive(rs.getBoolean("is_active"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
        }
        return null;
    }

    public boolean setUserActive(int userId, boolean active) throws SQLException {
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

    public int getRoleIdByName(String roleName) throws SQLException {
        String sql = "SELECT role_id FROM roles WHERE role_name = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, roleName);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("role_id");
                }
            }
        }
        return 0;
    }

    public int createUser(String username, String email, String password, int roleId) throws SQLException {
        String sql = "INSERT INTO users (role_id, username, email, password_hash, is_active) VALUES (?, ?, ?, ?, 1)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, roleId);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<UserBean> findActiveByRoleName(String roleName) throws SQLException {
        String sql = "SELECT u.user_id, u.role_id, u.username, u.email, u.full_name, u.phone, u.password_hash, u.is_active, u.created_at "
                + "FROM users u JOIN roles r ON r.role_id = u.role_id "
                + "WHERE r.role_name = ? AND u.is_active = 1 ORDER BY u.username";
        List<UserBean> list = new ArrayList<>();

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserBean user = new UserBean();
                    user.setUserId(rs.getInt("user_id"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setFullName(rs.getString("full_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setActive(rs.getBoolean("is_active"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(user);
                }
            }
        }

        return list;
    }

    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login = NOW() WHERE user_id = ?";
        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public boolean updateUser(int userId, String fullName, String email, String phone, String newPassword) throws SQLException {
        if (newPassword != null && !newPassword.isEmpty()) {
            String sql = "UPDATE users SET full_name = ?, email = ?, phone = ?, password_hash = ? WHERE user_id = ?";
            try (Connection con = DBConnectionUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setString(4, newPassword);
                ps.setInt(5, userId);
                return ps.executeUpdate() > 0;
            }
        } else {
            String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE user_id = ?";
            try (Connection con = DBConnectionUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, fullName);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setInt(4, userId);
                return ps.executeUpdate() > 0;
            }
        }
    }

    public boolean updateProfile(UserBean user) throws SQLException {
        String sql = "UPDATE users SET email = ?, username = ? WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getUsername());
            ps.setInt(3, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean isPasswordMatch(String input, String storedPassword) {
        if (storedPassword == null || input == null) {
            return false;
        }
        return storedPassword.equals(input);
    }
}
