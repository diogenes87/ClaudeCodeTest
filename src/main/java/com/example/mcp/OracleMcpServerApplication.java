package com.example.mcp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Oracle MCP Server - Spring Boot Application
 *
 * Model Context Protocol REST Server for Oracle Database operations
 * Features:
 * - 8 Oracle DB tools (connect, schema, queries, procedures, etc.)
 * - AI/LLM integration with LangChain4J
 * - Support for multiple LLM providers (OpenAI, Gemini)
 * - REST API for remote access
 * - Secure read-only query execution
 *
 * @author Claude Code
 * @version 1.0.0
 */
@SpringBootApplication
public class OracleMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OracleMcpServerApplication.class, args);
    }
}
