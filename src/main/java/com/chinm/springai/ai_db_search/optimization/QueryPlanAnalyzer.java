package com.chinm.springai.ai_db_search.optimization;

import org.springframework.stereotype.Service;

@Service
public class QueryPlanAnalyzer {
    public boolean needsOptimization(String plan) {

        if (plan.contains("TABLE SCAN"))
            return true;

        if (plan.contains("FULL SCAN"))
            return true;

        return false;
    }
}
