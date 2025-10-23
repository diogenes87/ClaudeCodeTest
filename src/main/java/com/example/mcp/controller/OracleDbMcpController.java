package com.example.mcp.controller;

import com.example.mcp.ai.OracleDatabaseAIService;
import com.example.mcp.tools.OracleDbTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for Oracle Database MCP Server
 * Provides HTTP endpoints for database operations and AI interactions
 */
@Slf4j
@RestController
@RequestMapping("/api/oracle")
@RequiredArgsConstructor
public class OracleDbMcpController {

    private final OracleDbTools oracleDbTools;
    private final OracleDatabaseAIService aiService;

    @PostMapping("/connect")
    public ResponseEntity<String> connect(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            String server = credentials.get("server");
            String sid = credentials.get("sid");

            String result = oracleDbTools.connectToOracleDb(username, password, server, sid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in connect endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/connection/{connectionId}")
    public ResponseEntity<String> disconnect(@PathVariable String connectionId) {
        try {
            String result = oracleDbTools.closeConnection(connectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in disconnect endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/schema/{connectionId}")
    public ResponseEntity<String> getSchema(@PathVariable String connectionId) {
        try {
            String result = oracleDbTools.retrieveSchema(connectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in getSchema endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/tables/{connectionId}")
    public ResponseEntity<String> getTablesAndColumns(@PathVariable String connectionId) {
        try {
            String result = oracleDbTools.retrieveTablesAndColumns(connectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in getTablesAndColumns endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/constraints/{connectionId}")
    public ResponseEntity<String> getConstraints(@PathVariable String connectionId) {
        try {
            String result = oracleDbTools.retrieveTableConstraints(connectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in getConstraints endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/procedures/{connectionId}")
    public ResponseEntity<String> getProcedures(@PathVariable String connectionId) {
        try {
            String result = oracleDbTools.retrieveProcedures(connectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in getProcedures endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/query/{connectionId}")
    public ResponseEntity<String> executeQuery(@PathVariable String connectionId, @RequestBody Map<String, String> queryRequest) {
        try {
            String query = queryRequest.get("query");
            String result = oracleDbTools.executeQuery(connectionId, query);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in executeQuery endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/dblinks/{connectionId}")
    public ResponseEntity<String> getDatabaseLinks(@PathVariable String connectionId) {
        try {
            String result = oracleDbTools.retrieveDatabaseLinks(connectionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in getDatabaseLinks endpoint", e);
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/ai/chat")
    public ResponseEntity<Map<String, String>> chatWithAI(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String response = aiService.chat(message);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            log.error("Error in AI chat endpoint", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/ai/query")
    public ResponseEntity<Map<String, String>> queryWithAI(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String response = aiService.query(message);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            log.error("Error in AI query endpoint", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Oracle MCP Server",
                "version", "1.0.0"
        ));
    }
}
