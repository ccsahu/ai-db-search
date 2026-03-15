package com.chinm.springai.ai_db_search.controller;

import com.chinm.springai.ai_db_search.rag.DatabaseRagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/ai")
public class RagController {

    private static final Logger logger = LoggerFactory.getLogger(RagController.class);
    private final DatabaseRagService service;

    public RagController(DatabaseRagService service) {
        this.service = service;
    }

    @GetMapping("/ask")
    public Object ask(@RequestParam String question) {
        logger.info("Received request to /ai/ask endpoint with question: {}", question);
        
        try {
            Object result = service.ask(question);
            logger.info("Successfully processed question and returned result");
            return result;
        } catch (Exception e) {
            logger.error("Error processing question: {}", e.getMessage(), e);
            throw e;
        }
    }
}
