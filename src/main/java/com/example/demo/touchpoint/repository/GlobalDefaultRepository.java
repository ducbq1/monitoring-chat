package com.example.demo.touchpoint.repository;

import com.example.demo.touchpoint.entity.GlobalDefault;
import com.example.demo.touchpoint.mapper.GlobalDefaultRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GlobalDefaultRepository {

    private final JdbcTemplate jdbcTemplate;

    public GlobalDefaultRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GlobalDefault> findAll() {
        return jdbcTemplate.query("SELECT name, value FROM global_defaults", new GlobalDefaultRowMapper());
    }

    public GlobalDefault findByName(String name) {
        return jdbcTemplate.queryForObject(
                "SELECT name, value FROM global_defaults WHERE name = ?",
                new GlobalDefaultRowMapper(),
                name
        );
    }

    public int insert(GlobalDefault globalDefault) {
        return jdbcTemplate.update(
                "INSERT INTO global_defaults (name, value) VALUES (?, ?)",
                globalDefault.getName(), globalDefault.getValue()
        );
    }

    public int update(GlobalDefault globalDefault) {
        return jdbcTemplate.update(
                "UPDATE global_defaults SET value = ? WHERE name = ?",
                globalDefault.getValue(), globalDefault.getName()
        );
    }

    public int deleteByName(String name) {
        return jdbcTemplate.update(
                "DELETE FROM global_defaults WHERE name = ?",
                name
        );
    }
}
