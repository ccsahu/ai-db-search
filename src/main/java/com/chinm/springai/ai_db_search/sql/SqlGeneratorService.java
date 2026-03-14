package com.chinm.springai.ai_db_search.sql;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SqlGeneratorService {

    private final ChatClient chatClient;

    public SqlGeneratorService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateSql(String schema, String question) {

        String prompt = """
  You are a SQL generator.
        You must follow these rules strictly:
            1. Use ONLY tables and columns present in the schema
            2. Do NOT invent table names
            3. Do NOT pluralize table names
            4. Return ONLY SQL
            5. Refer the table schema while preparing sql
            5. DO NOT mention other characters like Here is the SQL to retrieve. provide the sql which can be executed directly

  Database schema:
  %s

  Question:
  %s
  """.formatted(schema, question);

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}