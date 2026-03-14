package com.chinm.springai.ai_db_search.schema;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
        return docs;
    }
}
