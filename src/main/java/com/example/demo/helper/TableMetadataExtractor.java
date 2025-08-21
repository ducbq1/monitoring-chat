package com.example.demo.helper;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableMetadataExtractor {

    public List<ColumnMetadata> getTableColumns(JdbcTemplate jdbcTemplate, String tableName) throws SQLException {
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource == null) {
            throw new IllegalStateException("No DataSource found");
        }

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet columns = metaData.getColumns(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase(), null);
            ResultSet pkRs = metaData.getPrimaryKeys(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase());

            List<String> primaryKeys = new ArrayList<>();
            while (pkRs.next()) {
                primaryKeys.add(pkRs.getString("COLUMN_NAME"));
            }

            List<ColumnMetadata> columnList = new ArrayList<>();
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                int size = columns.getInt("COLUMN_SIZE");
                int nullable = columns.getInt("NULLABLE");
                String remarks = columns.getString("REMARKS");
                String defaultValue = columns.getString("COLUMN_DEF");

                boolean isPk = primaryKeys.contains(columnName);

                ColumnMetadata meta = new ColumnMetadata(
                        columnName,
                        typeName,
                        size,
                        nullable == DatabaseMetaData.columnNullable,
                        isPk,
                        defaultValue,
                        remarks
                );

                columnList.add(meta);
            }

            return columnList;
        }
    }

    public static class ColumnMetadata {
        private final String name;
        private final String type;
        private final int size;
        private final boolean nullable;
        private final boolean primaryKey;
        private final String defaultValue;
        private final String remarks;

        public ColumnMetadata(String name, String type, int size,
                              boolean nullable, boolean primaryKey,
                              String defaultValue, String remarks) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.nullable = nullable;
            this.primaryKey = primaryKey;
            this.defaultValue = defaultValue;
            this.remarks = remarks;
        }

        @Override
        public String toString() {
            return String.format("Column[name=%s, type=%s, size=%d, nullable=%s, pk=%s, default=%s, remarks=%s]",
                    name, type, size, nullable, primaryKey, defaultValue, remarks);
        }
    }
}
