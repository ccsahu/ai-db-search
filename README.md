
# Local Database RAG System
Spring Boot + Spring AI + Ollama + Chroma + HSQLDB

This project demonstrates how to build a **fully local AI-powered database assistant**.  
Users can ask natural language questions about database tables, and the system converts them into SQL queries.

Everything runs locally with **no dependency on cloud AI services**.

---

# Architecture

User Question
↓
Vector Search (Schema Retrieval)
↓
LLM Generates SQL
↓
SQL Validation
↓
Execute Query
↓
Return Results

Stack:

- Spring Boot 3.5
- Spring AI
- Ollama (local LLM)
- Chroma Vector Database (Docker)
- HSQLDB (in-memory database)

---

# Step 1: Install Ollama

Download:

https://ollama.com/download

or use powershell command: irm https://ollama.com/install.ps1 | iex

Verify installation:

```
ollama --version
```

Download models:

```
ollama pull llama3
ollama pull nomic-embed-text
```

Verify models:
```
ollama run llama3
>>> what is Java
>>> /bye
```

Purpose:

| Model | Role |
|------|------|
| llama3 | reasoning + SQL generation |
| nomic-embed-text | vector embeddings |

---

# Step 2: Run Chroma Vector Database

Install Docker Desktop.

Run Chroma:

```
docker run -p 8000:8000 -v ./chroma_data:/chroma/chroma chromadb/chroma
```

Verify:

```
http://localhost:8000/api/v2/heartbeat
```

---

# Step 3: Create Vector Collection

Newer Chroma versions require manually creating the collection.

```
curl.exe -X POST "http://localhost:8000/api/v2/tenants/default_tenant/databases/default_database/collections" -H "Content-Type: application/json" -d '{\"name\": \"db-schema-v1\"}'
```

Verify:

```
http://localhost:8000/api/v2/tenants/default_tenant/databases/default_database/collections
```

---

# Step 4: Create Spring Boot Project

Dependencies:

```
spring-ai-ollama-spring-boot-starter
spring-ai-chroma-store-spring-boot-starter
spring-boot-starter-jdbc
hsqldb
```

---

# Step 5: application.yml

```
spring:

 datasource:
  url: jdbc:hsqldb:mem:testdb
  driverClassName: org.hsqldb.jdbcDriver
  username: sa
  password:

 ai:

  ollama:
   base-url: http://localhost:11434

   chat:
    model: llama3

   embedding:
    model: nomic-embed-text

  vectorstore:
   chroma:
    url: http://localhost:8000
    collection-name: db-schema-v1
```

---

# Step 6: Create Database Schema

schema.sql

```
CREATE TABLE EMPLOYEE(
 ID INT PRIMARY KEY,
 NAME VARCHAR(100),
 DEPARTMENT VARCHAR(50),
 SALARY INT,
 CITY VARCHAR(50)
);
```

---

# Step 7: Insert Test Data

data.sql

```
INSERT INTO EMPLOYEE VALUES (1,'Rahul','Finance',90000,'Bangalore');
INSERT INTO EMPLOYEE VALUES (2,'Anita','HR',70000,'Mumbai');
INSERT INTO EMPLOYEE VALUES (3,'Kiran','Finance',120000,'Delhi');
```

---

# Step 8: Extract Database Schema

Use DatabaseMetaData to read table and column details.

Example:

```
DatabaseMetaData meta = connection.getMetaData();
ResultSet tables = meta.getTables(null,null,"%",new String[]{"TABLE"});
```

Convert schema into text documents.

---

# Step 9: Store Schema Embeddings

Convert schema documents into embeddings and store in Chroma.

Example:

```
vectorStore.add(documents);
```

---

# Step 10: Generate SQL Using LLM

Prompt the LLM with:

- database schema
- user question

Example prompt:

```
You are a SQL generator.
Use only tables present in the schema.
Return only SQL.
```

---

# Step 11: Execute SQL

Use JdbcTemplate:

```
jdbcTemplate.queryForList(sql);
```

---

# Step 12: REST API

Example endpoint:

```
GET /ai/ask?question=Which employees earn more than 80000
```

Example response:

```
[
 { "NAME": "Rahul" },
 { "NAME": "Kiran" }
]
```

---

# Running the System

1 Start Ollama

```
ollama serve
```

2 Start Chroma

```
docker run -p 8000:8000 -v ./chroma_data:/chroma/chroma chromadb/chroma
```

3 Run Spring Boot application

4 Embed schema once

5 Ask questions using REST API

---

# Example Questions

```
Which employees work in finance?

Who earns more than 80000?

List employee names.

Which city has finance employees?
```

---

# Project Structure

```
rag-database-ai

config
schema
embedding
sql
rag
controller
```

---

# Notes

This project demonstrates a **local AI database assistant**.

Advantages:

- No cloud dependency
- Fully local AI models
- Works offline
- Easy experimentation with RAG pipelines

---

