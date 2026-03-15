package com.chinm.springai.ai_db_search.rag;

import com.chinm.springai.ai_db_search.optimization.ExplainPlanService;
import com.chinm.springai.ai_db_search.optimization.QueryPlanAnalyzer;
import com.chinm.springai.ai_db_search.optimization.SqlPerformanceOptimizer;
import com.chinm.springai.ai_db_search.sql.*;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DatabaseRagService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseRagService.class);

    private final VectorStore vectorStore;
    private final SqlGeneratorService sqlGenerator;
    private final SqlExecutorService executor;
    private final ExplainPlanService explainService;
    private final QueryPlanAnalyzer planAnalyzer;
    private final SqlPerformanceOptimizer optimizer;

    public DatabaseRagService(
            VectorStore vectorStore,
            SqlGeneratorService sqlGenerator,
            SqlExecutorService executor,
            ExplainPlanService explainService,
            QueryPlanAnalyzer planAnalyzer,
            SqlPerformanceOptimizer optimizer) {

        this.vectorStore = vectorStore;
        this.sqlGenerator = sqlGenerator;
        this.executor = executor;
        this.explainService = explainService;
        this.planAnalyzer = planAnalyzer;
        this.optimizer = optimizer;
    }

    public Object ask(String question) {
        logger.info("Received question: {}", question);

        List<Document> docs =
                vectorStore.similaritySearch(SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        //.similarityThreshold(0.1)
                        .build());
        
        logger.info("Retrieved {} documents from vector store", docs.size());

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
        
        logger.info("Schema context length: {} characters", schemaContext.length());

        String sql =
                sqlGenerator.generateSql(schemaContext, question);
        
        logger.info("Generated SQL: {}", sql);

        //optional
       // String plan = explainService.explain(sql);

        //optional
       /* if (planAnalyzer.needsOptimization(plan)) {
            sql = optimizer.optimizeSql(sql, plan, schemaContext);
        }*/
        Object result = executor.execute(sql);
        logger.info("Query execution completed successfully");
        
        return result;
    }
}
