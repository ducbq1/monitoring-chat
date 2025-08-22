package com.example.demo.helper;

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

            ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName.toUpperCase(), null);

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
            ResultSet rs = metaData.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase());

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

            ResultSet rs = metaData.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase());

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

            String dbType = metaData.getDatabaseProductName();
            String version = metaData.getDatabaseProductVersion();
            String driver = metaData.getDriverName();
            String databaseName = conn.getCatalog();

            return DatabaseDTO.of(databaseName, dbType, version, driver);
        }
    }

    public static DatabaseDTO getDatabaseInfo(JdbcTemplate jdbcTemplate, String tableName) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) throw new IllegalStateException("No datasource found");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            String dbType = metaData.getDatabaseProductName();
            String version = metaData.getDatabaseProductVersion();
            String driver = metaData.getDriverName();
            String databaseName = conn.getCatalog();

            List<String> primaryKeys = new ArrayList<>();
            try (ResultSet pkRs = metaData.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase())) {
                while (pkRs.next()) {
                    primaryKeys.add(pkRs.getString("COLUMN_NAME"));
                }
            }

            if (primaryKeys.isEmpty()) {
                throw new SQLException("Table " + tableName + " does not have a primary key.");
            }
            if (primaryKeys.size() > 1) {
                throw new SQLException("Table " + tableName + " has multiple primary keys (composite PK not supported).");
            }

            String pkColumn = primaryKeys.get(0);

            ResultSet columns = metaData.getColumns(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase(), pkColumn);
            if (columns.next()) {
                String remarks = columns.getString("REMARKS");
                if (Objects.nonNull(remarks)) {
                    pkColumn = remarks;
                }
            }

            return DatabaseDTO.of(databaseName, dbType, version, driver, pkColumn);
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

            ResultSet rs = metaData.getColumns(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase(), null);
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                if (columnName.equalsIgnoreCase(colName)) {
                    isAutoIncrement = rs.getBoolean("IS_AUTOINCREMENT");
                    isNullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                    break;
                }
            }

            if (!isAutoIncrement) {
                rs = metaData.getColumns(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase(), null);
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

            rs = metaData.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase());
            while (rs.next()) {
                String pkColumn = rs.getString("COLUMN_NAME");
                if (columnName.equalsIgnoreCase(pkColumn)) {
                    isPrimaryKey = true;
                    break;
                }
            }

            rs = metaData.getImportedKeys(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase());
            while (rs.next()) {
                String fkColumn = rs.getString("FKCOLUMN_NAME");
                if (columnName.equalsIgnoreCase(fkColumn)) {
                    isForeignKey = true;
                    break;
                }
            }

            rs = metaData.getIndexInfo(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase(), true, false);
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
