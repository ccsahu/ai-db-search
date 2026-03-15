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
        6. Return ONLY executable SQL. No explanations. do not use ```
        7. Return only valid sql, no text on the front
  
        Join Rules:
            - Always use primary key / foreign key relationships
            - Example: EMPLOYEE.ID = LOAN.EMPLOYEE_ID

        Query Optimization Rules:
            - Prefer filtering using indexed columns when available
            - Prefer joins using indexed columns
            - Avoid full table scans when indexed filters exist
            - Use equality comparisons on indexed columns when possible
            
        CASE-INSENSITIVE COMPARISON RULE:
             - Apply UPPER() ONLY on STRING / VARCHAR columns
             - NEVER apply UPPER() on numeric columns like ID
             - NEVER apply UPPER() on boolean columns like STATUS
               
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