package com.chinm.springai.ai_db_search.controller;

import com.chinm.springai.ai_db_search.rag.DatabaseRagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class RagController {

    private final DatabaseRagService service;

    public RagController(DatabaseRagService service) {
        this.service = service;
    }

    @GetMapping("/ask")
    public Object ask(@RequestParam String question) {

        return service.ask(question);
    }
}
