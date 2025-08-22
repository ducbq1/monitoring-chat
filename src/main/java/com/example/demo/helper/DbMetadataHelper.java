package com.example.demo.helper;

import com.example.demo.core.model.Callback;
import com.example.demo.core.model.ColumnStatusDTO;
import com.example.demo.core.model.DatabaseDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbMetadataHelper {

    public static List<ColumnInfo> getColumns(JdbcTemplate jdbcTemplate, String tableName) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) throw new IllegalStateException("No datasource found");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet rs = metaData.getColumns(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase(), null);

            List<ColumnInfo> columns = new ArrayList<>();
            while (rs.next()) {
                ColumnInfo col = new ColumnInfo(
                        rs.getString("COLUMN_NAME"),
                        rs.getString("TYPE_NAME"),
                        rs.getInt("COLUMN_SIZE"),
                        rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable
                );
                columns.add(col);
            }
            rs.close();
            return columns;
        }
    }

    public static boolean isPrimaryKeyValid(JdbcTemplate jdbcTemplate, String tableName, String primaryKeyColumn) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) {
            throw new IllegalStateException("No datasource found");
        }

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getPrimaryKeys(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase());

            while (rs.next()) {
                String pkColumn = rs.getString("COLUMN_NAME");
                if (pkColumn.equalsIgnoreCase(primaryKeyColumn)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static List<String> getPrimaryKeys(JdbcTemplate jdbcTemplate, String tableName) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) throw new IllegalStateException("No datasource found");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet rs = metaData.getPrimaryKeys(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase());

            List<String> pks = new ArrayList<>();
            while (rs.next()) {
                pks.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
            return pks;
        }
    }

    public static DatabaseDTO getDatabaseInfo(JdbcTemplate jdbcTemplate) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) throw new IllegalStateException("No datasource found");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            String catalog = conn.getCatalog();
            String dbType = metaData.getDatabaseProductName();
            String version = metaData.getDatabaseProductVersion();
            String driver = metaData.getDriverName();

            return DatabaseDTO.of(catalog, dbType, version, driver);
        }
    }

    public static DatabaseDTO getDatabaseInfo(JdbcTemplate jdbcTemplate, String tableName, Callback callback) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) throw new IllegalStateException("No datasource found");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            String dbType = metaData.getDatabaseProductName();
            String version = metaData.getDatabaseProductVersion();
            String driver = metaData.getDriverName();
            String catalog = conn.getCatalog();

            try {
                List<String> primaryKeys = new ArrayList<>();
                try (ResultSet pkRs = metaData.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase())) {
                    while (pkRs.next()) {
                        primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                    }
                }

                if (!primaryKeys.isEmpty()) {
                    if (primaryKeys.size() > 1) {
                        throw new SQLException("Table " + tableName + " has multiple primary keys (composite PK not supported).");
                    }

                    String pkColumn = primaryKeys.get(0);

                    try (ResultSet columns = metaData.getColumns(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase(), pkColumn)) {
                        if (columns.next()) {
                            String remarks = columns.getString("REMARKS");
                            if (remarks != null && !remarks.isBlank()) {
                                pkColumn = remarks;
                            }
                        }
                    }

                    callback.onCallback(pkColumn);
                }

            } catch (SQLException e) {
                System.err.println("Cannot retrieve primary key for table " + tableName + ": " + e.getMessage());
            }

            return DatabaseDTO.of(catalog, dbType, version, driver);
        }
    }


    public static ColumnStatusDTO getColumnStatus(JdbcTemplate jdbcTemplate, String tableName, String columnName) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) throw new IllegalStateException("No datasource found");

        boolean isAutoIncrement = false;
        boolean isAutoGenerated = false;
        boolean isNullable = false;
        boolean isUnique = false;
        boolean isPrimaryKey = false;
        boolean isForeignKey = false;

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet rs = metaData.getColumns(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase(), null);
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                if (columnName.equalsIgnoreCase(colName)) {
                    isAutoIncrement = rs.getBoolean("IS_AUTOINCREMENT");
                    isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                    break;
                }
            }

            if (!isAutoIncrement) {
                rs = metaData.getColumns(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase(), null);
                while (rs.next()) {
                    String colName = rs.getString("COLUMN_NAME");
                    if (columnName.equalsIgnoreCase(colName)) {
                        String isGenerated = rs.getString("IS_GENERATEDCOLUMN");
                        if ("YES".equalsIgnoreCase(isGenerated)) {
                            isAutoGenerated = true;
                        }
                        break;
                    }
                }
            }

            rs = metaData.getPrimaryKeys(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase());
            while (rs.next()) {
                String pkColumn = rs.getString("COLUMN_NAME");
                if (columnName.equalsIgnoreCase(pkColumn)) {
                    isPrimaryKey = true;
                    break;
                }
            }

            rs = metaData.getImportedKeys(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase());
            while (rs.next()) {
                String fkColumn = rs.getString("FKCOLUMN_NAME");
                if (columnName.equalsIgnoreCase(fkColumn)) {
                    isForeignKey = true;
                    break;
                }
            }

            rs = metaData.getIndexInfo(Objects.nonNull(conn.getCatalog()) ? conn.getCatalog() : null, Objects.nonNull(conn.getSchema()) ? conn.getSchema() : null, tableName.toUpperCase(), true, false);
            while (rs.next()) {
                String indexColumn = rs.getString("COLUMN_NAME");
                if (columnName.equalsIgnoreCase(indexColumn)) {
                    isUnique = true;
                    break;
                }
            }
        }

        return new ColumnStatusDTO(isAutoIncrement, isAutoGenerated, isNullable, isUnique, isPrimaryKey, isForeignKey);
    }

    public record ColumnInfo(String name, String type, int size, boolean nullable) {}
}
