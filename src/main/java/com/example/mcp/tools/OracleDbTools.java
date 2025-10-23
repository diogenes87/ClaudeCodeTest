package com.example.mcp.tools;

import com.example.mcp.config.OracleConnectionManager;
import com.example.mcp.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

/**
 * Oracle Database Tools for LLM/AI interactions
 * Provides methods for database operations through LangChain4J tools
 */
@Slf4j
@ApplicationScoped
public class OracleDbTools {

    @Inject
    OracleConnectionManager connectionManager;

    @Inject
    ObjectMapper objectMapper;

    /**
     * Method 1: Create a connection with Oracle DB
     */
    @Tool("Creates a connection to Oracle Database using credentials in format: username/password@server:SID")
    public String connectToOracleDb(String username, String password, String server, String sid) {
        try {
            OracleConnectionRequest request = OracleConnectionRequest.builder()
                    .username(username)
                    .password(password)
                    .server(server)
                    .sid(sid)
                    .build();

            String connectionId = connectionManager.createConnection(request);
            ConnectionResponse response = ConnectionResponse.success(connectionId, request);

            return toJson(response);
        } catch (Exception e) {
            log.error("Failed to connect to Oracle DB", e);
            OracleConnectionRequest request = OracleConnectionRequest.builder()
                    .username(username)
                    .password(password)
                    .server(server)
                    .sid(sid)
                    .build();
            ConnectionResponse response = ConnectionResponse.failure(request, e.getMessage());
            return toJson(response);
        }
    }

    /**
     * Method 2: Close DB connection
     */
    @Tool("Closes an existing Oracle Database connection using the connection ID")
    public String closeConnection(String connectionId) {
        boolean closed = connectionManager.closeConnection(connectionId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", closed);
        result.put("connectionId", connectionId);
        result.put("message", closed ? "Connection closed successfully" : "Failed to close connection or connection not found");

        return toJson(result);
    }

    /**
     * Method 3: Retrieve DB Schema (all tables)
     */
    @Tool("Retrieves all tables in the database schema for a given connection")
    public String retrieveSchema(String connectionId) {
        try {
            Connection connection = connectionManager.getConnection(connectionId);
            DatabaseMetaData metaData = connection.getMetaData();

            List<String> tables = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    tables.add(tableName);
                }
            }

            SchemaInfo schemaInfo = SchemaInfo.builder()
                    .tables(tables)
                    .totalCount(tables.size())
                    .build();

            return toJson(schemaInfo);
        } catch (Exception e) {
            log.error("Failed to retrieve schema", e);
            return toJson(Map.of(
                    "success", false,
                    "message", "Failed to retrieve schema: " + e.getMessage()
            ));
        }
    }

    /**
     * Method 4: Return all tables and columns
     */
    @Tool("Retrieves all tables with their column details including data types and constraints")
    public String retrieveTablesAndColumns(String connectionId) {
        try {
            Connection connection = connectionManager.getConnection(connectionId);
            DatabaseMetaData metaData = connection.getMetaData();

            Map<String, List<TableColumnInfo.ColumnDetail>> tablesMap = new LinkedHashMap<>();

            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    List<TableColumnInfo.ColumnDetail> columns = new ArrayList<>();

                    try (ResultSet columnsRs = metaData.getColumns(null, null, tableName, "%")) {
                        while (columnsRs.next()) {
                            TableColumnInfo.ColumnDetail column = TableColumnInfo.ColumnDetail.builder()
                                    .columnName(columnsRs.getString("COLUMN_NAME"))
                                    .dataType(columnsRs.getString("TYPE_NAME"))
                                    .nullable(columnsRs.getString("IS_NULLABLE"))
                                    .dataLength(columnsRs.getInt("COLUMN_SIZE"))
                                    .dataPrecision(columnsRs.getObject("DECIMAL_DIGITS") != null ?
                                            columnsRs.getInt("DECIMAL_DIGITS") : null)
                                    .dataScale(columnsRs.getObject("NUM_PREC_RADIX") != null ?
                                            columnsRs.getInt("NUM_PREC_RADIX") : null)
                                    .build();
                            columns.add(column);
                        }
                    }
                    tablesMap.put(tableName, columns);
                }
            }

            TableColumnInfo info = TableColumnInfo.builder()
                    .tables(tablesMap)
                    .build();

            return toJson(info);
        } catch (Exception e) {
            log.error("Failed to retrieve tables and columns", e);
            return toJson(Map.of(
                    "success", false,
                    "message", "Failed to retrieve tables and columns: " + e.getMessage()
            ));
        }
    }

    /**
     * Method 5: Return all table Primary Keys and Foreign Keys
     */
    @Tool("Retrieves all primary keys and foreign keys for all tables in the database")
    public String retrieveTableConstraints(String connectionId) {
        try {
            Connection connection = connectionManager.getConnection(connectionId);
            DatabaseMetaData metaData = connection.getMetaData();

            Map<String, TableConstraintInfo.TableConstraints> tablesMap = new LinkedHashMap<>();

            try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");

                    // Get Primary Keys
                    List<TableConstraintInfo.PrimaryKeyInfo> primaryKeys = new ArrayList<>();
                    try (ResultSet pkRs = metaData.getPrimaryKeys(null, null, tableName)) {
                        while (pkRs.next()) {
                            TableConstraintInfo.PrimaryKeyInfo pk = TableConstraintInfo.PrimaryKeyInfo.builder()
                                    .constraintName(pkRs.getString("PK_NAME"))
                                    .columnName(pkRs.getString("COLUMN_NAME"))
                                    .position(pkRs.getInt("KEY_SEQ"))
                                    .build();
                            primaryKeys.add(pk);
                        }
                    }

                    // Get Foreign Keys
                    List<TableConstraintInfo.ForeignKeyInfo> foreignKeys = new ArrayList<>();
                    try (ResultSet fkRs = metaData.getImportedKeys(null, null, tableName)) {
                        while (fkRs.next()) {
                            TableConstraintInfo.ForeignKeyInfo fk = TableConstraintInfo.ForeignKeyInfo.builder()
                                    .constraintName(fkRs.getString("FK_NAME"))
                                    .columnName(fkRs.getString("FKCOLUMN_NAME"))
                                    .referencedTableName(fkRs.getString("PKTABLE_NAME"))
                                    .referencedColumnName(fkRs.getString("PKCOLUMN_NAME"))
                                    .position(fkRs.getInt("KEY_SEQ"))
                                    .build();
                            foreignKeys.add(fk);
                        }
                    }

                    TableConstraintInfo.TableConstraints constraints = TableConstraintInfo.TableConstraints.builder()
                            .primaryKeys(primaryKeys)
                            .foreignKeys(foreignKeys)
                            .build();

                    tablesMap.put(tableName, constraints);
                }
            }

            TableConstraintInfo info = TableConstraintInfo.builder()
                    .tables(tablesMap)
                    .build();

            return toJson(info);
        } catch (Exception e) {
            log.error("Failed to retrieve table constraints", e);
            return toJson(Map.of(
                    "success", false,
                    "message", "Failed to retrieve table constraints: " + e.getMessage()
            ));
        }
    }

    /**
     * Method 6: Retrieve all procedures and their details
     */
    @Tool("Retrieves all stored procedures and functions with their parameters to allow LLM to call them")
    public String retrieveProcedures(String connectionId) {
        try {
            Connection connection = connectionManager.getConnection(connectionId);
            DatabaseMetaData metaData = connection.getMetaData();

            List<ProcedureInfo.ProcedureDetail> procedures = new ArrayList<>();

            try (ResultSet procRs = metaData.getProcedures(null, null, "%")) {
                while (procRs.next()) {
                    String procedureName = procRs.getString("PROCEDURE_NAME");
                    String procedureType = procRs.getString("PROCEDURE_TYPE");

                    // Get procedure parameters
                    List<ProcedureInfo.ParameterDetail> parameters = new ArrayList<>();
                    try (ResultSet paramRs = metaData.getProcedureColumns(null, null, procedureName, "%")) {
                        while (paramRs.next()) {
                            ProcedureInfo.ParameterDetail param = ProcedureInfo.ParameterDetail.builder()
                                    .parameterName(paramRs.getString("COLUMN_NAME"))
                                    .position(paramRs.getInt("ORDINAL_POSITION"))
                                    .dataType(paramRs.getString("TYPE_NAME"))
                                    .inOut(getParameterMode(paramRs.getInt("COLUMN_TYPE")))
                                    .dataLength(paramRs.getInt("LENGTH"))
                                    .dataPrecision(paramRs.getObject("PRECISION") != null ?
                                            paramRs.getInt("PRECISION") : null)
                                    .dataScale(paramRs.getObject("SCALE") != null ?
                                            paramRs.getInt("SCALE") : null)
                                    .build();
                            parameters.add(param);
                        }
                    }

                    ProcedureInfo.ProcedureDetail procedure = ProcedureInfo.ProcedureDetail.builder()
                            .procedureName(procedureName)
                            .objectType(getProcedureType(procedureType))
                            .parameters(parameters)
                            .status("VALID")
                            .build();

                    procedures.add(procedure);
                }
            }

            ProcedureInfo info = ProcedureInfo.builder()
                    .procedures(procedures)
                    .build();

            return toJson(info);
        } catch (Exception e) {
            log.error("Failed to retrieve procedures", e);
            return toJson(Map.of(
                    "success", false,
                    "message", "Failed to retrieve procedures: " + e.getMessage()
            ));
        }
    }

    /**
     * Method 7: Execute queries on DB tables (SELECT only)
     */
    @Tool("Executes SELECT queries on database tables. Only SELECT queries are allowed, no UPDATE or DELETE operations")
    public String executeQuery(String connectionId, String query) {
        long startTime = System.currentTimeMillis();

        // Security check: only allow SELECT queries
        String normalizedQuery = query.trim().toUpperCase();
        if (!normalizedQuery.startsWith("SELECT") && !normalizedQuery.startsWith("WITH")) {
            return toJson(QueryResult.builder()
                    .success(false)
                    .message("Only SELECT queries are allowed. UPDATE, DELETE, INSERT, and other DML/DDL operations are forbidden.")
                    .build());
        }

        // Additional security: block dangerous keywords
        String[] forbiddenKeywords = {"UPDATE", "DELETE", "INSERT", "DROP", "ALTER", "CREATE", "TRUNCATE", "GRANT", "REVOKE"};
        for (String keyword : forbiddenKeywords) {
            if (normalizedQuery.contains(keyword)) {
                return toJson(QueryResult.builder()
                        .success(false)
                        .message("Query contains forbidden keyword: " + keyword)
                        .build());
            }
        }

        try {
            Connection connection = connectionManager.getConnection(connectionId);

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Get column names
                List<String> columns = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    columns.add(metaData.getColumnName(i));
                }

                // Get rows
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }

                long executionTime = System.currentTimeMillis() - startTime;

                QueryResult result = QueryResult.builder()
                        .success(true)
                        .columns(columns)
                        .rows(rows)
                        .rowCount(rows.size())
                        .executionTimeMs(executionTime)
                        .message("Query executed successfully")
                        .build();

                return toJson(result);
            }
        } catch (Exception e) {
            log.error("Failed to execute query", e);
            return toJson(QueryResult.builder()
                    .success(false)
                    .message("Failed to execute query: " + e.getMessage())
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build());
        }
    }

    /**
     * Method 8: Retrieve all DB_LINKS
     */
    @Tool("Retrieves all database links (DB_LINKS) to other databases")
    public String retrieveDatabaseLinks(String connectionId) {
        try {
            Connection connection = connectionManager.getConnection(connectionId);

            String query = "SELECT DB_LINK, USERNAME, HOST, CREATED FROM ALL_DB_LINKS ORDER BY DB_LINK";

            List<DatabaseLinkInfo.DbLinkDetail> dbLinks = new ArrayList<>();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                while (rs.next()) {
                    DatabaseLinkInfo.DbLinkDetail dbLink = DatabaseLinkInfo.DbLinkDetail.builder()
                            .dbLinkName(rs.getString("DB_LINK"))
                            .username(rs.getString("USERNAME"))
                            .host(rs.getString("HOST"))
                            .created(rs.getTimestamp("CREATED") != null ?
                                    rs.getTimestamp("CREATED").toString() : null)
                            .build();
                    dbLinks.add(dbLink);
                }
            }

            DatabaseLinkInfo info = DatabaseLinkInfo.builder()
                    .dbLinks(dbLinks)
                    .build();

            return toJson(info);
        } catch (Exception e) {
            log.error("Failed to retrieve database links", e);
            return toJson(Map.of(
                    "success", false,
                    "message", "Failed to retrieve database links: " + e.getMessage()
            ));
        }
    }

    // Helper methods

    private String getParameterMode(int columnType) {
        return switch (columnType) {
            case DatabaseMetaData.procedureColumnIn -> "IN";
            case DatabaseMetaData.procedureColumnOut -> "OUT";
            case DatabaseMetaData.procedureColumnInOut -> "IN/OUT";
            case DatabaseMetaData.procedureColumnReturn -> "RETURN";
            default -> "UNKNOWN";
        };
    }

    private String getProcedureType(String type) {
        if (type == null) return "PROCEDURE";
        return switch (type) {
            case "1" -> "PROCEDURE";
            case "2" -> "FUNCTION";
            default -> "PROCEDURE";
        };
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON", e);
            return "{\"error\": \"Failed to serialize response\"}";
        }
    }
}
