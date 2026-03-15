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
  You are a SQL generator. Generate a SQL query using ONLY the tables and columns listed below.
       STRICT RULES:
        1. Use ONLY table and column names listed in the schema.
        2. NEVER invent new columns.
        3. NEVER invent new tables.
        4. NEVER pluralize table names.
        5. Before returning SQL check:
              - every column exists in schema
              - every table exists in schema
              If any column does not exist, correct it.
        6. Return ONLY executable SQL. No explanations.
        7. Use upper case for string comparision.
        
        CASE-INSENSITIVE STRING COMPARISON RULE:
             For all string filters ALWAYS use:
                 UPPER(column) = UPPER('value')
               
        VALID TABLES AND COLUMNS:
             EMPLOYEE(ID, NAME, DEPARTMENT, SALARY CITY)
             LOAN(LOAN_ID, EMPLOYEE_ID, AMOUNT, STATUS)

  Schema context:
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