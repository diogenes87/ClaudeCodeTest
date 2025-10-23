package com.example.mcp.service;

import com.example.mcp.ai.OracleDatabaseAIService;
import com.example.mcp.tools.OracleDbTools;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * REST Resource for Oracle Database MCP Server
 * Provides HTTP endpoints for database operations
 */
@Slf4j
@Path("/api/oracle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OracleDbMcpResource {

    @Inject
    OracleDbTools oracleDbTools;

    // Note: AI Service requires Quarkus LangChain4J extension
    // For CLI usage, use CommandLineChat instead
    // @Inject
    // OracleDatabaseAIService aiService;

    @POST
    @Path("/connect")
    public Response connect(Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            String server = credentials.get("server");
            String sid = credentials.get("sid");

            String result = oracleDbTools.connectToOracleDb(username, password, server, sid);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in connect endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/connection/{connectionId}")
    public Response disconnect(@PathParam("connectionId") String connectionId) {
        try {
            String result = oracleDbTools.closeConnection(connectionId);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in disconnect endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/schema/{connectionId}")
    public Response getSchema(@PathParam("connectionId") String connectionId) {
        try {
            String result = oracleDbTools.retrieveSchema(connectionId);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in getSchema endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/tables/{connectionId}")
    public Response getTablesAndColumns(@PathParam("connectionId") String connectionId) {
        try {
            String result = oracleDbTools.retrieveTablesAndColumns(connectionId);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in getTablesAndColumns endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/constraints/{connectionId}")
    public Response getConstraints(@PathParam("connectionId") String connectionId) {
        try {
            String result = oracleDbTools.retrieveTableConstraints(connectionId);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in getConstraints endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/procedures/{connectionId}")
    public Response getProcedures(@PathParam("connectionId") String connectionId) {
        try {
            String result = oracleDbTools.retrieveProcedures(connectionId);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in getProcedures endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/query/{connectionId}")
    public Response executeQuery(@PathParam("connectionId") String connectionId, Map<String, String> queryRequest) {
        try {
            String query = queryRequest.get("query");
            String result = oracleDbTools.executeQuery(connectionId, query);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in executeQuery endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/dblinks/{connectionId}")
    public Response getDatabaseLinks(@PathParam("connectionId") String connectionId) {
        try {
            String result = oracleDbTools.retrieveDatabaseLinks(connectionId);
            return Response.ok(result).build();
        } catch (Exception e) {
            log.error("Error in getDatabaseLinks endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // Note: AI endpoints require Quarkus LangChain4J extension with @RegisterAIService
    // For AI chat functionality, use the CLI (CommandLineChat) instead
    // These endpoints are disabled to avoid dependency conflicts

    /*
    @POST
    @Path("/ai/chat")
    public Response chatWithAI(Map<String, String> request) {
        try {
            String message = request.get("message");
            String response = aiService.chat(message);
            return Response.ok(Map.of("response", response)).build();
        } catch (Exception e) {
            log.error("Error in AI chat endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/ai/query")
    public Response queryWithAI(Map<String, String> request) {
        try {
            String message = request.get("message");
            String response = aiService.query(message);
            return Response.ok(Map.of("response", response)).build();
        } catch (Exception e) {
            log.error("Error in AI query endpoint", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    */

    @GET
    @Path("/health")
    public Response health() {
        return Response.ok(Map.of(
                "status", "UP",
                "service", "Oracle MCP Server",
                "version", "1.0.0"
        )).build();
    }
}
