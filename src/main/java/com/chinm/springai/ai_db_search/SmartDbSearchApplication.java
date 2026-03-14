package com.chinm.springai.ai_db_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartDbSearchApplication {
	//create the chromadb collection manually
	//curl.exe -X GET "http://localhost:8000/api/v2/tenants/default_tenant/databases/default_database/collections"
	//curl.exe -X POST "http://localhost:8000/api/v2/tenants/default_tenant/databases/default_database/collections" -H "Content-Type: application/json" -d '{\"name\": \"db-schema-v1\"}'
	public static void main(String[] args) {
		SpringApplication.run(SmartDbSearchApplication.class, args);
	}

}
