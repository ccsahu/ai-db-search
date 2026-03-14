# Smart DB Search - End-to-End Architecture

This is a **Spring Boot RAG (Retrieval-Augmented Generation) application** that enables natural language querying of a database using AI.

## Core Architecture

**Technology Stack:**
- **Spring Boot 3.5.11** with Java 21
- **Spring AI 1.1.2** for AI integration
- **Ollama** (llama3 model) for chat completion
- **ChromaDB** for vector storage
- **HSQLDB** as the embedded database

## End-to-End Flow

### 1. **Database Setup**
- Uses in-memory HSQLDB with sample `EMPLOYEE` and `LOAN` tables
- Schema defined in `src/main/resources/DB/schema.sql`
- Sample data loaded from `data.sql`

### 2. **Schema Embedding Process** (Startup)
`SchemaEmbeddingService` runs automatically on startup:

- **SchemaExtractor** reads database metadata via JDBC
- Extracts table names, column names, and data types
- Creates text documents describing each table structure
- **VectorStore** converts these to embeddings using `nomic-embed-text` model
- Stores in ChromaDB collection `db-schema-v1`

### 3. **Query Processing Flow**
When a user asks a question via `/ai/ask?question=...`:

1. **Vector Search**: `DatabaseRagService` searches ChromaDB for relevant schema documents based on semantic similarity to the question

2. **SQL Generation**: `SqlGeneratorService` uses Llama3 model with:
   - Retrieved schema context
   - User's natural language question
   - Generates SQL query following strict rules (SELECT only, no invented tables)

3. **SQL Execution**: `SqlExecutorService` validates and executes:
   - Ensures query starts with "SELECT"
   - Executes via Spring's `JdbcTemplate`
   - Returns results or handles errors

### 4. **API Endpoint**
- **REST Controller**: `RagController` exposes `/ai/ask` endpoint
- Accepts natural language questions as query parameters
- Returns query results as JSON

## Key Components

- **VectorStore**: ChromaDB for semantic search of database schema
- **ChatClient**: Spring AI's interface to Ollama Llama3 model
- **EmbeddingModel**: Nomic-embed-text for text vectorization
- **JdbcTemplate**: For safe SQL execution

## External Dependencies

- **Ollama server** (localhost:11434) - must be running with llama3 and nomic-embed-text models
- **ChromaDB server** (localhost:8000) - for vector storage

The system essentially converts natural language questions into SQL queries by first finding relevant database schema information through vector search, then using that context to generate appropriate SQL, and finally executing it safely.