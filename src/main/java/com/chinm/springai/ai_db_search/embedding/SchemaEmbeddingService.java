package com.chinm.springai.ai_db_search.embedding;

import com.chinm.springai.ai_db_search.schema.SchemaExtractor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.List;

@Service
public class SchemaEmbeddingService {

    private final SchemaExtractor extractor;
    private final VectorStore vectorStore;

    public SchemaEmbeddingService(
            SchemaExtractor extractor,
            VectorStore vectorStore) {

        this.extractor = extractor;
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void initializeSchema() throws Exception {
        embedSchema();
    }

    public void embedSchema() throws Exception {
        List<String> schemaDocs = extractor.extractSchema();
        List<Document> docs =
                schemaDocs.stream()
                        .map(Document::new)
                        .toList();
        vectorStore.add(docs);
    }
}
