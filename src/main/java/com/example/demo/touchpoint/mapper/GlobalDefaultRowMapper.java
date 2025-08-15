package com.example.demo.touchpoint.mapper;

import com.example.demo.touchpoint.entity.GlobalDefault;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GlobalDefaultRowMapper implements RowMapper<GlobalDefault> {
    @Override
    public GlobalDefault mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new GlobalDefault(
                rs.getString("name"),
                rs.getString("value")
        );
    }
}
