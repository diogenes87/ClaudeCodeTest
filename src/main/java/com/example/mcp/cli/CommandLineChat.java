package com.example.mcp.cli;

import com.example.mcp.config.JsonConfiguration;
import com.example.mcp.config.OracleConnectionManager;
import com.example.mcp.config.llm.LLMConfig;
import com.example.mcp.config.llm.LLMProvider;
import com.example.mcp.config.llm.LLMProviderFactory;
import com.example.mcp.tools.OracleDbTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

/**
 * Command-line interface for Oracle MCP Server
 * Allows interactive chat with LLM that can use Oracle DB tools
 * Supports multiple LLM providers: OpenAI, Google Gemini, and more
 */
public class CommandLineChat {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        Oracle MCP Server - Interactive Chat               ║");
        System.out.println("║        Powered by LangChain4J & Multiple LLM Providers    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        // Check for LLM provider configuration
        if (!LLMProviderFactory.isAnyProviderConfigured()) {
            System.err.println("ERROR: No LLM provider configured!");
            System.err.println();
            System.err.println("Please set one of the following environment variables:");
            System.err.println("  - OPENAI_API_KEY=your-openai-key (for OpenAI GPT models)");
            System.err.println("  - GEMINI_API_KEY=your-gemini-key (for Google Gemini)");
            System.err.println("  - ANTHROPIC_API_KEY=your-anthropic-key (coming soon)");
            System.err.println();
            System.err.println("Example:");
            System.err.println("  export OPENAI_API_KEY=sk-...");
            System.err.println("  mvn exec:java");
            System.err.println();
            System.err.println("Or set multiple keys and the system will auto-select:");
            System.err.println("  export OPENAI_API_KEY=sk-...");
            System.err.println("  export GEMINI_API_KEY=...");
            System.exit(1);
        }

        try {
            // Initialize components
            OracleConnectionManager connectionManager = new OracleConnectionManager();
            JsonConfiguration jsonConfig = new JsonConfiguration();
            ObjectMapper objectMapper = jsonConfig.objectMapper();

            OracleDbTools tools = new OracleDbTools();
            // Manually inject dependencies
            injectField(tools, "connectionManager", connectionManager);
            injectField(tools, "objectMapper", objectMapper);

            // Create ChatLanguageModel using factory (auto-detects provider)
            LLMConfig llmConfig = LLMConfig.fromEnvironment();
            ChatLanguageModel chatModel = LLMProviderFactory.create(llmConfig);

            // Create AI Service with tools
            OracleDatabaseChatService chatService = AiServices.builder(OracleDatabaseChatService.class)
                    .chatLanguageModel(chatModel)
                    .tools(tools)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                    .build();

            System.out.println("✓ Connected to " + llmConfig.getProvider().getDisplayName());
            System.out.println("✓ Using model: " + llmConfig.getModelName());
            System.out.println("✓ Oracle Database tools loaded and ready");
            System.out.println();
            System.out.println("Available commands:");
            System.out.println("  - Type your question to chat with the AI");
            System.out.println("  - Type 'help' to see available database tools");
            System.out.println("  - Type 'exit' or 'quit' to end the session");
            System.out.println();
            System.out.println("Example questions:");
            System.out.println("  - Connect to database omsdb1/omsdb1@indltel300:DIODB100");
            System.out.println("  - Show me all tables in the database");
            System.out.println("  - What are the columns in the EMPLOYEES table?");
            System.out.println("  - Execute query: SELECT * FROM departments");
            System.out.println();
            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println();

            // Start interactive chat
            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.print("You: ");
                String userInput = scanner.nextLine().trim();

                if (userInput.isEmpty()) {
                    continue;
                }

                // Handle exit commands
                if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("quit")) {
                    System.out.println();
                    System.out.println("Goodbye! Thanks for using Oracle MCP Server.");
                    running = false;
                    continue;
                }

                // Handle help command
                if (userInput.equalsIgnoreCase("help")) {
                    printHelp();
                    continue;
                }

                try {
                    System.out.println();
                    System.out.print("AI: ");

                    // Send message to AI service
                    String response = chatService.chat(userInput);

                    System.out.println(response);
                    System.out.println();

                } catch (Exception e) {
                    System.err.println();
                    System.err.println("Error: " + e.getMessage());
                    System.err.println();
                }
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("Failed to initialize Oracle MCP Server CLI:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Available Oracle Database Tools:");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
        System.out.println("1. connectToOracleDb");
        System.out.println("   - Connects to Oracle Database");
        System.out.println("   - Example: \"Connect to database omsdb1/omsdb1@server:SID\"");
        System.out.println();
        System.out.println("2. closeConnection");
        System.out.println("   - Closes a database connection");
        System.out.println("   - Example: \"Close connection <connection-id>\"");
        System.out.println();
        System.out.println("3. retrieveSchema");
        System.out.println("   - Gets all tables in the database");
        System.out.println("   - Example: \"Show me all tables\"");
        System.out.println();
        System.out.println("4. retrieveTablesAndColumns");
        System.out.println("   - Gets detailed table and column information");
        System.out.println("   - Example: \"What are the columns in all tables?\"");
        System.out.println();
        System.out.println("5. retrieveTableConstraints");
        System.out.println("   - Gets primary keys and foreign keys");
        System.out.println("   - Example: \"Show me all primary and foreign keys\"");
        System.out.println();
        System.out.println("6. retrieveProcedures");
        System.out.println("   - Gets stored procedures and functions");
        System.out.println("   - Example: \"List all stored procedures\"");
        System.out.println();
        System.out.println("7. executeQuery");
        System.out.println("   - Executes SELECT queries (read-only)");
        System.out.println("   - Example: \"Execute query: SELECT * FROM employees\"");
        System.out.println();
        System.out.println("8. retrieveDatabaseLinks");
        System.out.println("   - Gets all database links");
        System.out.println("   - Example: \"Show me all database links\"");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println();
    }

    /**
     * Simple reflection-based dependency injection for standalone mode
     */
    private static void injectField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject field: " + fieldName, e);
        }
    }

    /**
     * Chat service interface for AI interactions
     */
    public interface OracleDatabaseChatService {
        String chat(String message);
    }
}
