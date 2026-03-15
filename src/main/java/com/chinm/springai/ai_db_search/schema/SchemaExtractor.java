package com.chinm.springai.ai_db_search.schema;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SchemaExtractor {

    private final DataSource dataSource;

    public SchemaExtractor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> extractSchema() throws Exception {
        List<String> docs = new ArrayList<>();
        Connection conn = dataSource.getConnection();
        DatabaseMetaData meta = conn.getMetaData();

        ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            ResultSet columns = meta.getColumns(null, null, tableName, "%");
            StringBuilder doc = new StringBuilder();
            doc.append("Table ").append(tableName).append("\nColumns:\n");
            while (columns.next()) {
                doc.append(columns.getString("COLUMN_NAME")).append(" type ").append(columns.getString("TYPE_NAME")).append("\n");
            }
            docs.add(doc.toString());
        }

        // Get foreign keys for a specific table
        //ResultSet foreignKeys = meta.getImportedKeys(null, null, "LOAN");
        // Get foreign keys for all tables in one go
        ResultSet foreignKeys = meta.getCrossReference(null, null, "%", null, null, "%");
        while (foreignKeys.next()) {
            String pkTable = foreignKeys.getString("PKTABLE_NAME");      // EMPLOYEE
            String pkColumn = foreignKeys.getString("PKCOLUMN_NAME");    // ID
            String fkTable = foreignKeys.getString("FKTABLE_NAME");      // LOAN
            String fkColumn = foreignKeys.getString("FKCOLUMN_NAME");    // EMPLOYEE_ID

            // Create relationship documentation
            String relationship = String.format(
                    "FOREIGN KEY: %s.%s REFERENCES %s.%s",
                    fkTable, fkColumn, pkTable, pkColumn
            );

            // Document each relationship as a separate vector document
            Document relationshipDoc = new Document(
                    String.format("Table %s is related to table %s via %s.%s = %s.%s",
                            fkTable, pkTable, fkTable, fkColumn, pkTable, pkColumn),
                    Map.of("type", "relationship", "tables", fkTable + "," + pkTable)
            );

            docs.add(relationshipDoc.toString());
        }

        return docs;
    }
}
