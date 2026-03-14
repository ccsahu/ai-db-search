package com.chinm.springai.ai_db_search.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SqlExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutorService.class);
    private final JdbcTemplate jdbcTemplate;

    public SqlExecutorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<List<Map<String, Object>>> execute(String sql) {
        logger.info("Executing SQL: {}", sql);
        if(!sql.toLowerCase().startsWith("select"))
            throw new RuntimeException("Only SELECT allowed");

        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            return Optional.of(result);
        } catch (Exception e) {
            logger.error("SQL execution failed: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
