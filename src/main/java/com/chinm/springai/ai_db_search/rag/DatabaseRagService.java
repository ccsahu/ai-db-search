package com.chinm.springai.ai_db_search.rag;

import com.chinm.springai.ai_db_search.sql.SqlExecutorService;
import com.chinm.springai.ai_db_search.sql.SqlGeneratorService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseRagService {

    private final VectorStore vectorStore;
    private final SqlGeneratorService sqlGenerator;
    private final SqlExecutorService executor;

    public DatabaseRagService(
            VectorStore vectorStore,
            SqlGeneratorService sqlGenerator,
            SqlExecutorService executor) {

        this.vectorStore = vectorStore;
        this.sqlGenerator = sqlGenerator;
        this.executor = executor;
    }

    public Object ask(String question) {

        List<Document> docs =
                vectorStore.similaritySearch(question);

        /*
         * Extract and combine the text content from retrieved documents
         * to create a comprehensive schema context for SQL generation.
         * Each document contains database schema information that will be
         * used by the SQL generator to understand the database structure.
         */
        String schemaContext =
                docs.stream()
                        .map(Document::getText)
                        .collect(Collectors.joining("\n"));

        String sql =
                sqlGenerator.generateSql(schemaContext, question);

        return executor.execute(sql);
    }
}
