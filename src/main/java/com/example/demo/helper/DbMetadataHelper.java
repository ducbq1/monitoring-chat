package com.example.demo.helper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbMetadataHelper {

    private final JdbcTemplate jdbcTemplate;

    public DbMetadataHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ColumnInfo> getColumns(String tableName) throws SQLException {
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

    public List<String> getPrimaryKeys(String tableName) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) throw new IllegalStateException("No datasource found");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet rs = metaData.getPrimaryKeys(conn.getCatalog(), null, tableName.toUpperCase());

            List<String> pks = new ArrayList<>();
            while (rs.next()) {
                pks.add(rs.getString("COLUMN_NAME"));
            }
            rs.close();
            return pks;
        }
    }

    public record ColumnInfo(String name, String type, int size, boolean nullable) {}
}
