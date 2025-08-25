package com.example.demo.core.repository;

import com.example.demo.config.DynamicDataSourceConfig;
import com.example.demo.core.annotation.Column;
import com.example.demo.core.entity.BaseEntity;
import com.example.demo.core.listener.ErrorEvent;
import com.example.demo.core.mapper.AnnotationBasedRowMapper;
import com.example.demo.core.mapper.PagedRowMapper;
import com.example.demo.core.model.*;
import com.example.demo.exception.AppException;
import com.example.demo.helper.DbMetadataHelper;
import com.example.demo.helper.FieldUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class JdbcBaseRepository<T extends BaseEntity> implements JdbcRepository<T> {
    private final ApplicationEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(JdbcBaseRepository.class);
    private final DynamicDataSourceConfig dynamicDataSourceConfig;
    private final Class<T> entityClass;
    private final FieldUtil fieldUtil;
    private final TableInfoDTO tableInfo;
    private final PrimaryKeyInfoDTO primaryKeyInfo;

    private final String tableName;
    private final String primaryKey;

    public JdbcBaseRepository(ApplicationEventPublisher eventPublisher, Class<T> entityClass, DynamicDataSourceConfig dynamicDataSourceConfig, FieldUtil fieldUtil) {
        this.eventPublisher = eventPublisher;
        this.fieldUtil = fieldUtil;
        this.entityClass = entityClass;

        this.tableInfo = fieldUtil.getTable(entityClass);
        this.primaryKeyInfo = fieldUtil.getPrimaryKey(entityClass);

        this.tableName = tableInfo.name();
        this.primaryKey = primaryKeyInfo.name();

        this.dynamicDataSourceConfig = dynamicDataSourceConfig;
    }

    @Override
    public List<T> findAll() {
        List<String> columns = fieldUtil.getColumns(entityClass, true);
        String columnList = String.join(", ", columns);
        String sql = "SELECT " + columnList + " FROM " + tableName + " ORDER BY " + primaryKey;
        return jdbcTemplate().query(sql, new AnnotationBasedRowMapper<>(entityClass));
    }

    @Override
    public List<T> paginateSkip(int page, int limit) {
        List<String> columns = fieldUtil.getColumns(entityClass, true);
        String columnList = String.join(", ", columns);
        String sql = "SELECT " + columnList + " FROM " + tableName + " ORDER BY " + primaryKey;
        return jdbcTemplate().query(sql, new PagedRowMapper<>(new AnnotationBasedRowMapper<>(entityClass), page * limit, limit))
                .stream().filter(Objects::nonNull).toList();
    }

    @Override
    public List<T> paginate(int page, int limit) {
        List<String> columns = fieldUtil.getColumns(entityClass, true);
        String columnList = String.join(", ", columns);
        String sql = "SELECT " + columnList + " FROM " + tableName + " ORDER BY " + primaryKey;

        return jdbcTemplate().query(con -> {
            PreparedStatement ps = con.prepareStatement(
                    sql,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            return ps;
        }, (ResultSetExtractor<? extends List<T>>) rs -> {
            List<T> results = new ArrayList<>();
            int startRow = page * limit + 1;
            if (rs.absolute(startRow)) {
                int count = 0;
                do {
                    T row = new AnnotationBasedRowMapper<>(entityClass).mapRow(rs, rs.getRow());
                    results.add(row);
                    count++;
                } while (count < limit && rs.next());
            }
            return results;
        });
    }


    @Override
    public List<T> paginate(int page, int limit, Map<String, Object> filters) {
        List<String> columns = fieldUtil.getColumns(entityClass, true);
        String columnList = String.join(", ", columns);

        StringBuilder sql = new StringBuilder("SELECT " + columnList + " FROM " + tableName);

        // Build WHERE clause từ filters
        List<Object> params = new ArrayList<>();
        if (filters != null && !filters.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(filters.keySet().stream()
                    .map(key -> key + " = ?")
                    .collect(Collectors.joining(" AND ")));
            params.addAll(filters.values());
        }

        sql.append(" ORDER BY ").append(primaryKey);

        return jdbcTemplate().query(
                sql.toString(),
                new PagedRowMapper<>(new AnnotationBasedRowMapper<>(entityClass), page * limit, limit),
                params.toArray()
        ).stream().filter(Objects::nonNull).toList();
    }

    @Override
    public int insert(T entity) {
        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        List<Object> values = new ArrayList<>();

        try {
            for (Field field : entityClass.getDeclaredFields()) {
                Column colAnno = field.getAnnotation(Column.class);
                if (colAnno == null) {
                    continue;
                }

                var columnStatus = DbMetadataHelper.getColumnStatus(jdbcTemplate(), tableName, colAnno.name());

                if (columnStatus.isPrimaryKey() && (columnStatus.isAutoGenerated() || columnStatus.isAutoIncrement())) {
                    continue;
                }

                if (!primaryKeyInfo.sequence().isEmpty()) {
                    String sequenceQuery = "SELECT " + primaryKeyInfo.sequence() + ".NEXTVAL FROM DUAL";
                    String id = jdbcTemplate().queryForObject(sequenceQuery, String.class);
                    field.setAccessible(true);
                    field.set(entity, id);  // Gán giá trị ID cho entity
                }

                columns.add(colAnno.name());
                placeholders.add("?");
                field.setAccessible(true);
                values.add(field.get(entity));
            }

            String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
            return jdbcTemplate().update(sql, values.toArray());

        } catch (Exception e) {
            throw new AppException("Error inserting " + tableName, e);
        }
    }


    @Override
    public int update(T entity) {
        StringJoiner setClauses = new StringJoiner(", ");
        try {
            for (Field field : entityClass.getDeclaredFields()) {
                Column colAnno = field.getAnnotation(Column.class);
                if (colAnno != null && !primaryKey.equalsIgnoreCase(colAnno.name())) {
                    setClauses.add(colAnno.name() + " = ?");
                }
            }

            String sql = "UPDATE " + tableName + " SET " + setClauses + " WHERE " + primaryKey + " = ?";
            return jdbcTemplate().update(sql, fieldUtil.getFieldValues(entity, true));

        } catch (Exception e) {
            throw new AppException("Error updating " + tableName, e);
        }
    }

    @Override
    public T findById(Object id) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + primaryKey + " = ?";
        return jdbcTemplate().queryForObject(sql, new AnnotationBasedRowMapper<>(entityClass), id);
    }

    @Override
    public List<T> findByColumn(String columnName, Object value) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
        return jdbcTemplate().query(sql, new AnnotationBasedRowMapper<>(entityClass), value);
    }

    @Override
    public List<T> findByColumns(Map<String, Object> conditions) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
        List<Object> values = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            if (i > 0) sql.append(" AND ");
            sql.append(entry.getKey()).append(" = ?");
            values.add(entry.getValue());
            i++;
        }

        return jdbcTemplate().query(sql.toString(), new AnnotationBasedRowMapper<>(entityClass), values.toArray());
    }

    @Override
    public int deleteById(Object id) {
        String sql = "DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?";
        return jdbcTemplate().update(sql, id);
    }

    @Override
    public int deleteByColumn(String columnName, Object value) {
        String sql = "DELETE FROM " + tableName + " WHERE " + columnName + " = ?";
        return jdbcTemplate().update(sql, value);
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        return jdbcTemplate().queryForObject(sql, Integer.class);
    }

    @Override
    public int count(Map<String, Object> filters) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM " + tableName);

        List<Object> params = new ArrayList<>();
        if (filters != null && !filters.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(filters.keySet().stream()
                    .map(key -> key + " = ?")
                    .collect(Collectors.joining(" AND ")));
            params.addAll(filters.values());
        }

        return jdbcTemplate().queryForObject(sql.toString(), Integer.class, params.toArray());
    }

    @Override
    public DatabaseDTO getDatabaseInfo() {
        try {
            return DbMetadataHelper.getDatabaseInfo(jdbcTemplate());
        } catch (SQLException e) {
            return DatabaseDTO.of(tableInfo.datasource());
        }
    }

    @Override
    public DatabaseDTO getDatabaseInfo(String tableName, Callback callback) {
        try {
            return DbMetadataHelper.getDatabaseInfo(jdbcTemplate(), tableName, callback);
        } catch (SQLException e) {
            return DatabaseDTO.of(tableInfo.datasource());
        }
    }

    @Override
    public List<ColumnDataDTO> getRecordWithMetadata(String tableName, Object idValue) throws SQLException {
        List<ColumnDataDTO> result = new ArrayList<>();
        List<String> columnNames = new ArrayList<>();
        Map<String, ColumnDataDTO> metaMap = new HashMap<>();
        String idColumn;

        // 1. Lấy metadata (primary key + columns)
        try (Connection conn = Objects.requireNonNull(jdbcTemplate().getDataSource()).getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            // Lấy primary key
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

            idColumn = primaryKeys.get(0);

            // Lấy column info
            try (ResultSet columns = metaData.getColumns(conn.getCatalog(), conn.getSchema(), tableName.toUpperCase(), null)) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    ColumnDataDTO col = new ColumnDataDTO();
                    col.setColumnName(columnName);
                    col.setTypeName(columns.getString("TYPE_NAME"));
                    col.setSize(columns.getInt("COLUMN_SIZE"));
                    col.setNullable(columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    col.setRemarks(columns.getString("REMARKS"));
                    col.setDefaultValue(columns.getString("COLUMN_DEF"));
                    col.setPrimaryKey(primaryKeys.contains(columnName));

                    columnNames.add(columnName);
                    metaMap.put(columnName, col);
                }
            }
        } // conn đóng ngay sau khi lấy metadata

        // 2. Query dữ liệu theo batch
        int batchSize = 30;
        for (int i = 0; i < columnNames.size(); i += batchSize) {
            List<String> batch = columnNames.subList(i, Math.min(i + batchSize, columnNames.size()));
            String sql = "SELECT " + String.join(", ", batch) +
                    " FROM " + tableName +
                    " WHERE " + idColumn + " = ?";

            try {
                Map<String, Object> row = jdbcTemplate().queryForMap(sql, idValue);
                for (String colName : batch) {
                    ColumnDataDTO col = metaMap.get(colName);
                    if (col != null) {
                        col.setValue(row.get(colName));
                    }
                }
            } catch (DataAccessException e) {
                log.error("No data found for table {} with {}={}: {}", tableName, idColumn, idValue, e.getMessage());
                eventPublisher.publishEvent(new ErrorEvent(this, "No Data Available", "There are no records to display at the moment."));
            }
        }

        result.addAll(metaMap.values());
        return result;
    }

    @Override
    public List<ColumnDataDTO> getRecordWithMetadata(String tableName, String primaryKey, Object idValue) throws SQLException {
        List<ColumnDataDTO> result = new ArrayList<>();

        DataSource dataSource = Objects.requireNonNull(jdbcTemplate().getDataSource());
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            ResultSet columns = metaData.getColumns(
                    conn.getCatalog(),
                    conn.getSchema(),
                    tableName.toUpperCase(),
                    null
            );

            List<String> columnNames = new ArrayList<>();
            Map<String, ColumnDataDTO> metaMap = new HashMap<>();

            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                int size = columns.getInt("COLUMN_SIZE");
                int nullable = columns.getInt("NULLABLE");
                String remarks = columns.getString("REMARKS");
                String defaultValue = columns.getString("COLUMN_DEF");

                ColumnDataDTO col = new ColumnDataDTO();
                col.setColumnName(columnName);
                col.setTypeName(typeName);
                col.setSize(size);
                col.setNullable(nullable == DatabaseMetaData.columnNullable);
                col.setRemarks(remarks);
                col.setDefaultValue(defaultValue);
                col.setPrimaryKey(primaryKey.equalsIgnoreCase(columnName));

                columnNames.add(columnName);
                metaMap.put(columnName, col);
            }

            if (idValue != null) {
                int batchSize = 30;
                for (int i = 0; i < columnNames.size(); i += batchSize) {
                    List<String> batch = columnNames.subList(i, Math.min(i + batchSize, columnNames.size()));
                    String sql = "SELECT " + String.join(", ", batch) +
                            " FROM " + tableName +
                            " WHERE " + primaryKey + " = ?";

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setObject(1, idValue);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                for (String colName : batch) {
                                    if (metaMap.containsKey(colName)) {
                                        ColumnDataDTO col = metaMap.get(colName);
                                        col.setValue(rs.getObject(colName));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (String colName : columnNames) {
                result.add(metaMap.get(colName));
            }
        }

        return result;
    }

    private JdbcTemplate jdbcTemplate() {
        return dynamicDataSourceConfig.getJdbcTemplate(tableInfo.datasource());
    }
}
