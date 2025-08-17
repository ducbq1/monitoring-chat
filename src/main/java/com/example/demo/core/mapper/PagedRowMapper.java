package com.example.demo.core.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public record PagedRowMapper<T> (RowMapper<T> delegate, int offset, int limit) implements RowMapper<T> {

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        if (rowNum < offset || rowNum >= offset + limit) {
            return null;
        }
        return delegate.mapRow(rs, rowNum);
    }
}
