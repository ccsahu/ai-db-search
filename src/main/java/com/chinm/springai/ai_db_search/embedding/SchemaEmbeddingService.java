package com.chinm.springai.ai_db_search.embedding;

import com.chinm.springai.ai_db_search.schema.SchemaExtractor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchemaEmbeddingService {

    private static final Logger logger = LoggerFactory.getLogger(SchemaEmbeddingService.class);

    private final SchemaExtractor extractor;
    private final VectorStore vectorStore;
    EmbeddingModel embeddingModel;

    public SchemaEmbeddingService(
            SchemaExtractor extractor,
            VectorStore vectorStore,
            EmbeddingModel embeddingModel) {

        this.extractor = extractor;
        this.vectorStore = vectorStore;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void initializeSchema() throws Exception {
        logger.info("Initializing schema embedding process...");
        try {
            embedSchema();
            logger.info("Schema embedding process completed successfully");
            validateEmbedding();
        } catch (Exception e) {
            logger.error("Failed to initialize schema embedding: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void validateEmbedding() {
        float[] vector =
                embeddingModel.embed("employee table");
        logger.info("Embedding dimension: {}" , vector.length);
    }

    public void embedSchema() throws Exception {
        logger.info("Starting schema embedding process");
        
        Map<String, String> schemaDocs = extractor.extractSchema();
        logger.info("Extracted {} schema documents", schemaDocs.size());
        
        List<Document> docs =
                schemaDocs.entrySet().stream()
                        .map(s -> createSchemaDocument(s.getKey(),s.getValue()))
                        .toList();

        logger.info("Final Embeded docs: {}", docs.toString());
        logger.info("Converted {} schema documents to Spring AI Document objects", docs.size());
        
        try {
            vectorStore.add(docs);
            logger.info("Successfully added {} documents to vector store", docs.size());
        } catch (Exception e) {
            logger.error("Failed to add documents to vector store: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Document createSchemaDocument(String tableName, String rawSchema) {
        // 1. Create a Unique ID (Crucial so tables don't overwrite each other)
        String id = "schema_" + tableName.toLowerCase();

        // 2. Prepare Enriched Content (What the Vector Store "searches")
        String content = enrichContent(tableName, rawSchema);

        // 3. Prepare Metadata (What the LLM uses to write the final SQL)
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("table_name", tableName);
        metadata.put("content_type", "database_schema");
        metadata.put("database_type", "HSQLDB");
        // Adding versioning or timestamps can also help during debugging
        metadata.put("indexed_at", System.currentTimeMillis());

        return new Document(id, content, metadata);
    }

    private String enrichContent(String tableName, String rawSchema) {

        if (tableName.equalsIgnoreCase("employee")) {
            return "Database Table: " + tableName + ". " +
                    "Description: This table serves as the primary directory for all company employee, staff, personnel, and workers."+
                    "Purpose: Use this table when queries involve finding people, listing staff members, checking salaries, identifying departments, or filtering workers by their city location."+
                    "Search Keywords: staff, workers, people, personnel, team members, payroll, salary details, office location."+
                    "Schema: " + rawSchema+
                    "Notes: This is the parent table for loan records.";
        } else if (tableName.equalsIgnoreCase("loan")) {
            return "Database Table: " + tableName + ". " +
                    "Purpose: This table tracks financial borrowings, debts, loans and credit issued to employees." +
                    "Purpose: Use this table for questions regarding how much money is owed, the status of a loan (e.g., approved, pending), or to find financial assistance records."+
                    "Search Keywords: debt, credit, borrowing, lending, money owed, financial status, loan amount."+
                    "Schema: " + rawSchema+
                    "Relationships: \n" +
                    "- This table joins with the EMPLOYEE table using EMPLOYEE_ID = EMPLOYEE.ID.";
        }
        return "";
    }

}
