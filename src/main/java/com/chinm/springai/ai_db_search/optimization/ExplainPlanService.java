package com.chinm.springai.ai_db_search.optimization;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExplainPlanService {
    private final JdbcTemplate jdbc;

    public ExplainPlanService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String explain(String sql) {
        String explainQuery = "EXPLAIN PLAN FOR " + sql;
        List<String> rows = jdbc.query(
                explainQuery,
                (rs, i) -> rs.getString(1)
        );
        return String.join("\n", rows);
    }
}
