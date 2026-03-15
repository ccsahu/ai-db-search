package com.chinm.springai.ai_db_search.optimization;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SqlPerformanceOptimizer {

    private final ChatClient chatClient;

    public SqlPerformanceOptimizer(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String optimizeSql(String sql, String plan, String schema) {
        String prompt = """
    The following SQL query is inefficient.
    SQL:
    %s

    Execution Plan:
    %s

    Database Schema:
    %s

    Improve this SQL query to:
    - avoid full table scans
    - use indexed columns
    - keep the same result

    Return only optimized SQL.
    """.formatted(sql, plan, schema);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
