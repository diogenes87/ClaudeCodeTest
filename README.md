# Oracle MCP Server

A Model Context Protocol (MCP) Server for Oracle Database operations built with Java 17, Quarkus, and LangChain4J.

## Overview

This application provides a professional MCP server that allows Large Language Models (LLMs) to interact with Oracle databases through a secure, structured interface. It exposes 8 core database operations as AI-accessible tools.

## Architecture

```
com.example.mcp/
├── ai/                     # AI Service interfaces
│   └── OracleDatabaseAIService.java
├── config/                 # Configuration classes
│   ├── JsonConfiguration.java
│   └── OracleConnectionManager.java
├── dto/                    # Data Transfer Objects
│   ├── ConnectionResponse.java
│   ├── DatabaseLinkInfo.java
│   ├── OracleConnectionRequest.java
│   ├── ProcedureInfo.java
│   ├── QueryResult.java
│   ├── SchemaInfo.java
│   ├── TableColumnInfo.java
│   └── TableConstraintInfo.java
├── service/                # REST endpoints
│   └── OracleDbMcpResource.java
└── tools/                  # LangChain4J Tools
    └── OracleDbTools.java
```

## Features

### 8 Core Database Tools

1. **connectToOracleDb** - Create connection to Oracle Database
   - Parameters: username, password, server, SID
   - Example: `omsdb1/omsdb1@indltel300:DIODB100`
   - Returns: Connection ID or error message in JSON format

2. **closeConnection** - Close database connection
   - Parameters: connectionId
   - Returns: Success/failure status

3. **retrieveSchema** - Get all tables in the database schema
   - Parameters: connectionId
   - Returns: List of all tables

4. **retrieveTablesAndColumns** - Get detailed table and column information
   - Parameters: connectionId
   - Returns: All tables with column details (name, type, nullable, length, precision, scale)

5. **retrieveTableConstraints** - Get Primary Keys and Foreign Keys
   - Parameters: connectionId
   - Returns: All PKs and FKs for each table

6. **retrieveProcedures** - Get stored procedures and functions
   - Parameters: connectionId
   - Returns: All procedures with parameters for LLM to call them

7. **executeQuery** - Execute SELECT queries (read-only)
   - Parameters: connectionId, query
   - Security: Only SELECT queries allowed, no UPDATE/DELETE/INSERT
   - Returns: Query results with execution time

8. **retrieveDatabaseLinks** - Get all DB_LINKS
   - Parameters: connectionId
   - Returns: All database links to other databases

## Technology Stack

- **Java 17** - Programming language
- **Quarkus 3.6.0** - Framework with CDI (@ApplicationScoped)
- **LangChain4J 0.34.0** - AI/LLM integration
- **Oracle JDBC 23.3.0** - Database connectivity
- **Lombok** - Reduce boilerplate code
- **Jackson** - JSON serialization
- **Maven** - Build tool

## REST API Endpoints

### Connection Management
- `POST /api/oracle/connect` - Connect to database
- `DELETE /api/oracle/connection/{connectionId}` - Disconnect

### Database Metadata
- `GET /api/oracle/schema/{connectionId}` - Get all tables
- `GET /api/oracle/tables/{connectionId}` - Get tables and columns
- `GET /api/oracle/constraints/{connectionId}` - Get PKs and FKs
- `GET /api/oracle/procedures/{connectionId}` - Get stored procedures
- `GET /api/oracle/dblinks/{connectionId}` - Get database links

### Query Execution
- `POST /api/oracle/query/{connectionId}` - Execute SELECT query

### AI Integration
- `POST /api/oracle/ai/chat` - Chat with AI about database
- `POST /api/oracle/ai/query` - Query database through AI

### Health Check
- `GET /api/oracle/health` - Service health status

## Usage Examples

### Connect to Database

```bash
curl -X POST http://localhost:8080/api/oracle/connect \
  -H "Content-Type: application/json" \
  -d '{
    "username": "omsdb1",
    "password": "omsdb1",
    "server": "indltel300",
    "sid": "DIODB100"
  }'
```

Response:
```json
{
  "success": true,
  "connectionId": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
  "message": "Successfully connected to Oracle Database",
  "credentials": {
    "username": "omsdb1",
    "server": "indltel300",
    "sid": "DIODB100",
    "port": 1521
  }
}
```

### Execute Query

```bash
curl -X POST http://localhost:8080/api/oracle/query/{connectionId} \
  -H "Content-Type: application/json" \
  -d '{
    "query": "SELECT * FROM employees WHERE department = 'IT'"
  }'
```

### Get Schema

```bash
curl http://localhost:8080/api/oracle/schema/{connectionId}
```

### Chat with AI

```bash
curl -X POST http://localhost:8080/api/oracle/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Show me all tables in the database"
  }'
```

## Security Features

- **Read-Only Queries**: Only SELECT queries are allowed
- **Query Validation**: Blocks UPDATE, DELETE, INSERT, DROP, ALTER, CREATE, TRUNCATE
- **Connection Management**: Secure connection pooling with UUID-based IDs
- **Credential Handling**: Passwords not exposed in responses
- **Error Handling**: Detailed error messages without exposing sensitive data

## Building and Running

### Development Mode

```bash
mvn quarkus:dev
```

The application will start on `http://localhost:8080`

### Package Application

```bash
mvn clean package
```

### Run in Production

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# HTTP Port
quarkus.http.port=8080

# LangChain4J OpenAI Configuration
quarkus.langchain4j.openai.api-key=${OPENAI_API_KEY}
quarkus.langchain4j.openai.chat-model.model-name=gpt-4

# Logging
quarkus.log.category."com.example.mcp".level=DEBUG
```

## Environment Variables

Set these environment variables for LLM integration:

```bash
export OPENAI_API_KEY=your-api-key-here
```

## LangChain4J Integration

The `OracleDatabaseAIService` interface uses `@RegisterAIService` annotation to:
- Automatically register all methods from `OracleDbTools` as AI tools
- Enable LLMs to call database operations naturally
- Provide system prompts for AI context

## Testing

### Health Check

```bash
curl http://localhost:8080/api/oracle/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "Oracle MCP Server",
  "version": "1.0.0"
}
```

## Database Connection Format

Supports two formats:

1. **Individual parameters**:
   - username: `omsdb1`
   - password: `omsdb1`
   - server: `indltel300`
   - sid: `DIODB100`

2. **Connection string**: `omsdb1/omsdb1@indltel300:DIODB100`

## Error Handling

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": "Not possible to connect to DB with given credentials. Error: IO Error: Connection refused",
  "credentials": {
    "username": "omsdb1",
    "server": "indltel300",
    "sid": "DIODB100",
    "port": 1521
  }
}
```

## Future Enhancements

- [ ] Connection pooling optimization
- [ ] Query result caching
- [ ] Rate limiting for queries
- [ ] Audit logging for all operations
- [ ] Support for multiple LLM providers
- [ ] WebSocket support for streaming results
- [ ] Procedure execution support

## License

This project is provided as-is for professional use.

## Author

Generated with Claude Code - Oracle MCP Server v1.0.0
