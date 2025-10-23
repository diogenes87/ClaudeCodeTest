package com.example.mcp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Welcome controller providing API information and navigation.
 */
@RestController
public class WelcomeController {

    @GetMapping("/")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "Oracle MCP Server");
        response.put("version", "1.0.0");
        response.put("description", "Model Context Protocol REST API Server for Oracle Database operations");
        response.put("status", "UP");

        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("Health Check", "/api/oracle/health");
        endpoints.put("API Documentation", "/api/oracle");
        endpoints.put("Actuator Health", "/actuator/health");
        endpoints.put("Actuator Info", "/actuator/info");
        endpoints.put("Actuator Metrics", "/actuator/metrics");

        response.put("endpoints", endpoints);

        Map<String, String> apiGroups = new LinkedHashMap<>();
        apiGroups.put("Connection Management", "/api/oracle/connect, /api/oracle/connection/{id}");
        apiGroups.put("Database Metadata", "/api/oracle/schema/{id}, /api/oracle/tables/{id}, /api/oracle/constraints/{id}");
        apiGroups.put("Query Execution", "/api/oracle/query/{id}");
        apiGroups.put("AI Integration", "/api/oracle/ai/chat, /api/oracle/ai/query");

        response.put("apiGroups", apiGroups);

        Map<String, String> documentation = new LinkedHashMap<>();
        documentation.put("GitHub", "https://github.com/yourusername/oracle-mcp-server");
        documentation.put("README", "See README.md for detailed API documentation");

        response.put("documentation", documentation);

        return response;
    }

    @GetMapping("/api/oracle")
    public Map<String, Object> apiInfo() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "Oracle MCP Server API");
        response.put("version", "1.0.0");

        Map<String, Object> endpoints = new LinkedHashMap<>();

        // Connection Management
        Map<String, String> connectionEndpoints = new LinkedHashMap<>();
        connectionEndpoints.put("POST /api/oracle/connect", "Connect to Oracle Database");
        connectionEndpoints.put("DELETE /api/oracle/connection/{connectionId}", "Close database connection");
        endpoints.put("Connection Management", connectionEndpoints);

        // Database Metadata
        Map<String, String> metadataEndpoints = new LinkedHashMap<>();
        metadataEndpoints.put("GET /api/oracle/schema/{connectionId}", "Get all tables in schema");
        metadataEndpoints.put("GET /api/oracle/tables/{connectionId}", "Get tables and columns");
        metadataEndpoints.put("GET /api/oracle/constraints/{connectionId}", "Get primary keys and foreign keys");
        metadataEndpoints.put("GET /api/oracle/procedures/{connectionId}", "Get stored procedures");
        metadataEndpoints.put("GET /api/oracle/dblinks/{connectionId}", "Get database links");
        endpoints.put("Database Metadata", metadataEndpoints);

        // Query Execution
        Map<String, String> queryEndpoints = new LinkedHashMap<>();
        queryEndpoints.put("POST /api/oracle/query/{connectionId}", "Execute SELECT query (read-only)");
        endpoints.put("Query Execution", queryEndpoints);

        // AI Integration
        Map<String, String> aiEndpoints = new LinkedHashMap<>();
        aiEndpoints.put("POST /api/oracle/ai/chat", "Chat with AI about database");
        aiEndpoints.put("POST /api/oracle/ai/query", "Query database through AI");
        endpoints.put("AI Integration", aiEndpoints);

        // Health & Monitoring
        Map<String, String> healthEndpoints = new LinkedHashMap<>();
        healthEndpoints.put("GET /api/oracle/health", "Service health status");
        healthEndpoints.put("GET /actuator/health", "Spring Boot health check");
        healthEndpoints.put("GET /actuator/metrics", "Application metrics");
        endpoints.put("Health & Monitoring", healthEndpoints);

        response.put("endpoints", endpoints);

        Map<String, String> examples = new LinkedHashMap<>();
        examples.put("Connect", "curl -X POST http://localhost:8080/api/oracle/connect -H 'Content-Type: application/json' -d '{\"username\":\"user\",\"password\":\"pass\",\"server\":\"host\",\"sid\":\"SID\"}'");
        examples.put("Health Check", "curl http://localhost:8080/api/oracle/health");
        examples.put("Get Schema", "curl http://localhost:8080/api/oracle/schema/{connectionId}");

        response.put("examples", examples);

        return response;
    }
}
