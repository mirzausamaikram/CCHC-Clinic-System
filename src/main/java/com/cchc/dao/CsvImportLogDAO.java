package com.cchc.dao;

import com.cchc.model.CsvImportLogBean;
import com.cchc.util.DBConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CsvImportLogDAO {

    public int create(CsvImportLogBean importLog) throws SQLException {
        String sql = "INSERT INTO csv_import_logs "
                + "(import_type, file_name, total_rows, success_rows, failed_rows, status, imported_by_user_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, importLog.getImportType());
            statement.setString(2, importLog.getFileName());
            statement.setInt(3, importLog.getTotalRows());
            statement.setInt(4, importLog.getSuccessRows());
            statement.setInt(5, importLog.getFailedRows());
            statement.setString(6, importLog.getStatus());
            statement.setInt(7, importLog.getImportedByUserId());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return 0;
    }
}
