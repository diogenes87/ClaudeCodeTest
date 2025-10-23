package com.example.mcp.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service for Oracle Database operations
 * This service integrates with LLMs to provide natural language access to Oracle DB operations
 *
 * Note: This interface can be used with LangChain4J's AiServices.builder()
 * For Quarkus integration, add @RegisterAiService annotation
 */
public interface OracleDatabaseAIService {

    @SystemMessage("""
            You are an Oracle Database assistant. You have access to various database tools to help users
            interact with Oracle databases.

            Available tools:
            1. connectToOracleDb - Connect to an Oracle database
            2. closeConnection - Close a database connection
            3. retrieveSchema - Get all tables in the schema
            4. retrieveTablesAndColumns - Get detailed information about tables and columns
            5. retrieveTableConstraints - Get primary keys and foreign keys
            6. retrieveProcedures - Get stored procedures and functions with their parameters
            7. executeQuery - Execute SELECT queries (read-only)
            8. retrieveDatabaseLinks - Get database links to other databases

            Always prioritize security:
            - Only SELECT queries are allowed for executeQuery
            - Never allow UPDATE, DELETE, INSERT, or DDL operations
            - Handle credentials securely

            Provide clear, structured responses in JSON format when returning data.
            """)
    String chat(@UserMessage String userMessage);

    @SystemMessage("""
            You are an Oracle Database assistant specialized in querying databases.
            Use the available tools to help users get information from Oracle databases.
            Always validate that queries are SELECT-only before execution.
            """)
    String query(@UserMessage String userMessage);
}
