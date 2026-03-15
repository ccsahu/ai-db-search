package com.chinm.springai.ai_db_search.schema;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SchemaExtractor {

    private static final Logger logger = LoggerFactory.getLogger(SchemaExtractor.class);

    private final DataSource dataSource;

    public SchemaExtractor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Map<String,String> extractSchema() throws Exception {
        logger.info("Starting schema extraction process");
        Map<String,String> schemaDocs = new HashMap<>();
        Connection conn = dataSource.getConnection();
        DatabaseMetaData meta = conn.getMetaData();
        
        logger.info("Database product name: {}, version: {}", 
                   meta.getDatabaseProductName(), meta.getDatabaseProductVersion());

        ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            logger.debug("Processing table: {}", tableName);
            ResultSet columns = meta.getColumns(null, null, tableName, "%");
            StringBuilder doc = new StringBuilder();
            doc.append("\n").append("Table ").append(tableName).append("\nColumns:\n");
            while (columns.next()) {
                doc.append(columns.getString("COLUMN_NAME")).append(" type ").append(columns.getString("TYPE_NAME")).append("\n");
            }
            addIndexInfo(doc, tableName, meta);
            addForeignKeyInfo(doc, tableName, meta);
            schemaDocs.put(tableName,doc.toString());
            logger.debug("Added schema document for table: {}, length: {}", tableName, doc.length());
        }
        logger.info("Schema extraction completed. Total documents created: {}", schemaDocs.size());
        //logger.info("Final schemadocs: {}",schemaDocs);
        return schemaDocs;
    }

    private void addForeignKeyInfo(StringBuilder doc, String tableName, DatabaseMetaData meta) throws SQLException {
        logger.debug("Extracting foreign key info for table: {}", tableName);
        ResultSet foreignKeys = meta.getImportedKeys(null, null, tableName);
        doc.append("foreign keys:").append("\n");
        boolean hasForeignKeys = false;
        while (foreignKeys.next()) {
            hasForeignKeys = true;
            String pkTable = foreignKeys.getString("PKTABLE_NAME");      // EMPLOYEE
            String pkColumn = foreignKeys.getString("PKCOLUMN_NAME");    // ID
            String fkTable = foreignKeys.getString("FKTABLE_NAME");      // LOAN
            String fkColumn = foreignKeys.getString("FKCOLUMN_NAME");    // EMPLOYEE_ID

            // Create relationship documentation
            String relationship = String.format(
                    "FOREIGN KEY: %s.%s REFERENCES %s.%s",
                    fkTable, fkColumn, pkTable, pkColumn
            );
            doc.append(relationship).append("\n");
            logger.debug("Found foreign key relationship: {}", relationship);

        }
        if (!hasForeignKeys) {
            logger.debug("No foreign keys found for table: {}", tableName);
        }
    }

    private void addIndexInfo(StringBuilder doc, String tableName, DatabaseMetaData meta) {
        logger.debug("Extracting index info for table: {}", tableName);
        ResultSet indexes = null;
        try {
            indexes = meta.getIndexInfo(null, null, tableName, false, false);
            doc.append("indexes:").append("\n");
            boolean hasIndexes = false;
            while(indexes.next()){
                hasIndexes = true;
                String indexName = indexes.getString("INDEX_NAME");
                String columnName = indexes.getString("COLUMN_NAME");
                doc.append(indexName).append("(").append(columnName).append(")").append("\n");
                logger.debug("Found index: {} on column: {}", indexName, columnName);
            }
            if (!hasIndexes) {
                logger.debug("No indexes found for table: {}", tableName);
            }
        } catch (SQLException e) {
            logger.error("Error extracting index info for table {}: {}", tableName, e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
