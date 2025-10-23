# Oracle MCP Server

A Model Context Protocol (MCP) REST API Server for Oracle Database operations built with Java 17, Spring Boot 3, and LangChain4J.

## Overview

This application provides a professional MCP REST server that allows Large Language Models (LLMs) to interact with Oracle databases through a secure, structured RESTful API interface. It exposes 8 core database operations as AI-accessible tools that can be consumed by any external client application.

**Architecture:** Pure server implementation - clients are built separately and consume this REST API.

## Architecture

```
com.example.mcp/
├── OracleMcpServerApplication.java   # Spring Boot main class
├── ai/                                # AI Service interfaces
│   └── OracleDatabaseAIService.java
├── config/                            # Configuration classes
│   ├── LangChain4jConfiguration.java
│   ├── JsonConfiguration.java
│   └── OracleConnectionManager.java
├── config/llm/                        # Multi-LLM provider support
│   ├── LLMProvider.java
│   ├── LLMConfig.java
│   └── LLMProviderFactory.java
├── controller/                        # REST API controllers
│   └── OracleDbMcpController.java
├── dto/                               # Data Transfer Objects
│   ├── ConnectionResponse.java
│   ├── DatabaseLinkInfo.java
│   ├── OracleConnectionRequest.java
│   ├── ProcedureInfo.java
│   ├── QueryResult.java
│   ├── SchemaInfo.java
│   ├── TableColumnInfo.java
│   └── TableConstraintInfo.java
└── tools/                             # LangChain4J Tools
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
- **Spring Boot 3.2.0** - Framework with dependency injection
- **LangChain4J 0.35.0** - AI/LLM integration with multi-provider support
- **Oracle JDBC 23.3.0** - Database connectivity
- **Lombok 1.18.30** - Reduce boilerplate code
- **Jackson** - JSON serialization
- **Maven** - Build tool
- **Spring Boot Actuator** - Health checks and metrics
- **Spring Boot DevTools** - Development utilities

## LLM Provider Support

The Oracle MCP Server features a **transparent abstraction layer** that supports multiple Large Language Model providers. You can switch between providers simply by changing environment variables - no code changes required!

### Supported Providers

| Provider | Environment Variable | Default Model | Free Tier | Status |
|----------|---------------------|---------------|-----------|--------|
| **OpenAI** | `OPENAI_API_KEY` | `gpt-4` | Limited | ✅ Fully Supported |
| **Google Gemini** | `GEMINI_API_KEY` | `gemini-1.5-flash-8b-001` | 1,500 req/day | ✅ Fully Supported |
| **Anthropic Claude** | `ANTHROPIC_API_KEY` | `claude-3-sonnet` | Limited | 🔜 Coming Soon |

### Auto-Detection with Fallback

The application automatically detects which LLM provider to use and includes intelligent fallback:

1. **Priority Order**: OpenAI → Gemini → Anthropic
2. **No Configuration Needed**: Just set your API key
3. **Automatic Model Fallback**: If a Gemini model is unavailable, automatically tries alternative models
4. **Fallback Models** (Gemini): Tries 8 different model variations including:
   - `gemini-1.5-flash-8b-001` (Free tier optimized)
   - `gemini-1.5-flash-001`
   - `gemini-1.5-flash-002`
   - `gemini-1.0-pro-001`
   - `gemini-1.0-pro-002`
   - And more fallback options
5. **Extensible**: Easy to add new providers

### Architecture

The multi-provider support is implemented through a clean abstraction:

```
LLMProvider (Enum)
    ↓
LLMConfig (Configuration)
    ↓
LLMProviderFactory (Factory Pattern)
    ↓
ChatLanguageModel (LangChain4J Interface)
```

**Key Components:**
- `LLMProvider` - Enum defining supported providers with auto-detection
- `LLMConfig` - Configuration with temperature, timeout, model name
- `LLMProviderFactory` - Creates appropriate ChatLanguageModel instance with fallback logic

### Switching Providers

Simply change your environment variable:

```bash
# Use OpenAI
export OPENAI_API_KEY=sk-...
mvn spring-boot:run

# Switch to Gemini (unset OpenAI first or it takes priority)
unset OPENAI_API_KEY
export GEMINI_API_KEY=...
mvn spring-boot:run
```

### Custom Models

You can specify custom models for each provider:

- **OpenAI**: `gpt-4`, `gpt-4-turbo`, `gpt-3.5-turbo`
- **Gemini**: `gemini-1.5-flash-8b-001`, `gemini-1.5-flash-001`, `gemini-1.0-pro-001`

## Installation & Setup

### Prerequisites

Before you begin, ensure you have the following installed on your local machine:

1. **Java Development Kit (JDK) 17 or higher**
   ```bash
   # Check Java version
   java -version

   # Should show: java version "17.x.x" or higher
   ```

   Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/

2. **Apache Maven 3.8.x or higher**
   ```bash
   # Check Maven version
   mvn -version

   # Should show: Apache Maven 3.8.x or higher
   ```

   Download from: https://maven.apache.org/download.cgi

3. **Git** (for cloning the repository)
   ```bash
   git --version
   ```

4. **LLM API Key** (Choose one or more)

   The application supports multiple LLM providers. You need at least one:

   **Option A: OpenAI (GPT-4, GPT-3.5)**
   - Sign up at: https://platform.openai.com/
   - Generate an API key from your account dashboard
   - Set environment variable: `OPENAI_API_KEY`

   **Option B: Google Gemini (Recommended for Free Tier)**
   - Sign up at: https://ai.google.dev/ or https://aistudio.google.com/
   - Generate an API key from Google AI Studio
   - **Free Tier**: 1,500 requests per day
   - Set environment variable: `GEMINI_API_KEY`

   **Option C: Anthropic Claude** (Coming Soon)
   - Sign up at: https://www.anthropic.com/
   - Set environment variable: `ANTHROPIC_API_KEY`

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd ClaudeCodeTest
```

### Step 2: Configure Environment Variables

Choose your preferred LLM provider and set the corresponding API key:

#### Option 1: Using OpenAI

**Linux/macOS:**
```bash
export OPENAI_API_KEY=sk-your-api-key-here
```

**Windows (Command Prompt):**
```cmd
set OPENAI_API_KEY=sk-your-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="sk-your-api-key-here"
```

#### Option 2: Using Google Gemini (Recommended for Free Tier)

**Linux/macOS:**
```bash
export GEMINI_API_KEY=your-gemini-api-key-here
```

**Windows (Command Prompt):**
```cmd
set GEMINI_API_KEY=your-gemini-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:GEMINI_API_KEY="your-gemini-api-key-here"
```

#### Using Multiple Providers

You can set multiple API keys. The application will auto-select in this priority order:
1. OpenAI (if `OPENAI_API_KEY` is set)
2. Gemini (if `GEMINI_API_KEY` is set)
3. Anthropic (if `ANTHROPIC_API_KEY` is set)

**Example with multiple providers:**
```bash
export OPENAI_API_KEY=sk-...
export GEMINI_API_KEY=...
# OpenAI will be used (higher priority)
```

**Permanent Configuration (Linux/macOS):**

Add to `~/.bashrc` or `~/.zshrc`:
```bash
# Choose your preferred provider
export OPENAI_API_KEY=sk-your-api-key-here
# or
export GEMINI_API_KEY=your-gemini-api-key-here
# or both
```

Then reload:
```bash
source ~/.bashrc  # or source ~/.zshrc
```

### Step 3: Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the source code
- Run tests (if any)
- Package the application

## Running the MCP Server

You have multiple options to run the Oracle MCP Server:

### Option 1: Spring Boot Development Mode (Recommended)

Start the server with hot reload for development:

```bash
mvn spring-boot:run
```

The server will start at: `http://localhost:8080`

**From IntelliJ IDEA Terminal:**
1. Open Terminal in IntelliJ (View → Tool Windows → Terminal)
2. Run: `mvn spring-boot:run`

**From VS Code Terminal:**
1. Open integrated terminal (Ctrl+` or Cmd+`)
2. Run: `mvn spring-boot:run`

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.2.0)

2025-10-23 10:00:00 - Starting OracleMcpServerApplication
2025-10-23 10:00:01 - Creating LLM client for provider: Google Gemini with model: gemini-1.5-flash-8b-001
2025-10-23 10:00:02 - Started OracleMcpServerApplication in 2.5 seconds
2025-10-23 10:00:02 - Server running at: http://localhost:8080
```

### Option 2: Run from IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Navigate to `src/main/java/com/example/mcp/OracleMcpServerApplication.java`
3. Right-click on the file
4. Select **"Run 'OracleMcpServerApplication.main()'"**

Or:
1. Open `OracleMcpServerApplication.java`
2. Click the green play button (▶) next to the `main` method
3. Select **"Run 'OracleMcpServerApplication.main()'"**

**Set environment variables in IntelliJ:**
1. Go to **Run** → **Edit Configurations...**
2. Select the `OracleMcpServerApplication` configuration
3. Add Environment Variable: `GEMINI_API_KEY=your-api-key-here`
4. Click **OK**
5. Run the configuration

### Option 3: Run from VS Code

1. Open the project in VS Code
2. Install the **"Extension Pack for Java"** (if not already installed)
3. Open `src/main/java/com/example/mcp/OracleMcpServerApplication.java`
4. Click **"Run"** above the `main` method

Or use the integrated terminal:
```bash
mvn spring-boot:run
```

**Set environment variables in VS Code:**

The project includes `.vscode/launch.json` with pre-configured Spring Boot run configurations. Edit it to add your API key:

```json
{
    "type": "java",
    "name": "Spring Boot - Oracle MCP Server",
    "request": "launch",
    "mainClass": "com.example.mcp.OracleMcpServerApplication",
    "projectName": "oracle-mcp-server",
    "env": {
        "GEMINI_API_KEY": "your-api-key-here"
    }
}
```

### Option 4: Run Packaged JAR (Production)

Package and run as a standalone JAR:

```bash
# Build the application
mvn clean package -DskipTests

# Run the JAR
java -jar target/oracle-mcp-server-1.0.0.jar
```

**With environment variables:**
```bash
export GEMINI_API_KEY=your-api-key
java -jar target/oracle-mcp-server-1.0.0.jar
```

### Option 5: Deploy to Tomcat

Build a WAR file and deploy to external Tomcat:

```bash
# Build WAR file
mvn clean package -DskipTests

# Copy to Tomcat webapps
cp target/oracle-mcp-server-1.0.0.war $TOMCAT_HOME/webapps/
```

Set environment variables in Tomcat's `setenv.sh` or `setenv.bat`:

```bash
export GEMINI_API_KEY=your-api-key
```

### Option 6: Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/oracle-mcp-server-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
# Build Docker image
docker build -t oracle-mcp-server .

# Run container
docker run -p 8080:8080 \
  -e GEMINI_API_KEY=your-api-key \
  oracle-mcp-server
```

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

## API Usage Examples

All examples assume the server is running at `http://localhost:8080`.

### 1. Health Check

Check if the server is running:

```bash
curl http://localhost:8080/api/oracle/health
```

**Response:**
```json
{
  "status": "UP",
  "service": "Oracle MCP Server",
  "version": "1.0.0",
  "llmProvider": "Google Gemini (model: gemini-1.5-flash-8b-001)"
}
```

### 2. Connect to Database

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

**Response:**
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

**Save the `connectionId` for subsequent requests.**

### 3. Get Database Schema

```bash
curl http://localhost:8080/api/oracle/schema/a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6
```

**Response:**
```json
{
  "tables": [
    "EMPLOYEES",
    "DEPARTMENTS",
    "JOBS",
    "LOCATIONS"
  ],
  "totalCount": 4
}
```

### 4. Get Tables and Columns

```bash
curl http://localhost:8080/api/oracle/tables/a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6
```

**Response:**
```json
{
  "tables": {
    "EMPLOYEES": [
      {
        "columnName": "EMPLOYEE_ID",
        "dataType": "NUMBER",
        "nullable": "NO",
        "dataLength": 22,
        "dataPrecision": 6,
        "dataScale": 0
      },
      {
        "columnName": "FIRST_NAME",
        "dataType": "VARCHAR2",
        "nullable": "YES",
        "dataLength": 20,
        "dataPrecision": null,
        "dataScale": null
      }
    ]
  }
}
```

### 5. Execute Query

```bash
curl -X POST http://localhost:8080/api/oracle/query/a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6 \
  -H "Content-Type: application/json" \
  -d '{
    "query": "SELECT * FROM employees WHERE department_id = 10"
  }'
```

**Response:**
```json
{
  "success": true,
  "columns": ["EMPLOYEE_ID", "FIRST_NAME", "LAST_NAME", "SALARY"],
  "rows": [
    {
      "EMPLOYEE_ID": 200,
      "FIRST_NAME": "Jennifer",
      "LAST_NAME": "Whalen",
      "SALARY": 4400
    }
  ],
  "rowCount": 1,
  "executionTimeMs": 45,
  "message": "Query executed successfully"
}
```

### 6. Chat with AI

```bash
curl -X POST http://localhost:8080/api/oracle/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Connect to database omsdb1/omsdb1@indltel300:DIODB100 and show me all tables"
  }'
```

**Response:**
```json
{
  "response": "I've connected to the database and here are all the tables: EMPLOYEES, DEPARTMENTS, JOBS, LOCATIONS, JOB_HISTORY"
}
```

### 7. Close Connection

```bash
curl -X DELETE http://localhost:8080/api/oracle/connection/a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6
```

**Response:**
```json
{
  "success": true,
  "connectionId": "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6",
  "message": "Connection closed successfully"
}
```

## Building Client Applications

This is a **pure REST API server** - clients are built separately. You can create clients in any language or framework:

### Example Client Types:

1. **Command-Line Interface (CLI)**
   - Java with LangChain4J for interactive chat
   - Python with requests library
   - Node.js with axios

2. **Web Application**
   - React/Vue/Angular frontend
   - Makes HTTP requests to this MCP server
   - Displays database information in UI

3. **Mobile Application**
   - iOS/Android apps
   - Consume REST API endpoints
   - Native database browsing experience

4. **Integration Scripts**
   - Python automation scripts
   - Shell scripts for DevOps
   - CI/CD pipeline integrations

### Client Example (Python):

```python
import requests

# Connect to database
response = requests.post('http://localhost:8080/api/oracle/connect', json={
    'username': 'omsdb1',
    'password': 'omsdb1',
    'server': 'indltel300',
    'sid': 'DIODB100'
})

connection_id = response.json()['connectionId']

# Get schema
schema = requests.get(f'http://localhost:8080/api/oracle/schema/{connection_id}')
print(schema.json())

# Close connection
requests.delete(f'http://localhost:8080/api/oracle/connection/{connection_id}')
```

### Client Example (JavaScript/Node.js):

```javascript
const axios = require('axios');

const baseUrl = 'http://localhost:8080/api/oracle';

async function main() {
  // Connect
  const connectResp = await axios.post(`${baseUrl}/connect`, {
    username: 'omsdb1',
    password: 'omsdb1',
    server: 'indltel300',
    sid: 'DIODB100'
  });

  const connectionId = connectResp.data.connectionId;

  // Get schema
  const schema = await axios.get(`${baseUrl}/schema/${connectionId}`);
  console.log(schema.data);

  // Close connection
  await axios.delete(`${baseUrl}/connection/${connectionId}`);
}

main();
```

### Client Example (Java):

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

public class OracleMcpClient {
    private static final String BASE_URL = "http://localhost:8080/api/oracle";
    private RestTemplate restTemplate = new RestTemplate();

    public String connect(String username, String password, String server, String sid) {
        OracleConnectionRequest request = new OracleConnectionRequest(username, password, server, sid);
        ResponseEntity<ConnectionResponse> response = restTemplate.postForEntity(
            BASE_URL + "/connect",
            request,
            ConnectionResponse.class
        );
        return response.getBody().getConnectionId();
    }

    public SchemaInfo getSchema(String connectionId) {
        return restTemplate.getForObject(
            BASE_URL + "/schema/" + connectionId,
            SchemaInfo.class
        );
    }
}
```

## Configuration

The server is configured via `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: oracle-mcp-server
  server:
    port: 8080
  jackson:
    default-property-inclusion: non_null
    serialization:
      indent_output: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

logging:
  level:
    root: INFO
    com.example.mcp: DEBUG
```

### Configuration Options:

**Change Server Port:**
```yaml
spring:
  server:
    port: 9090
```

**Configure Profiles:**

Development profile (dev):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Production profile (prod):
```bash
java -jar target/oracle-mcp-server-1.0.0.jar --spring.profiles.active=prod
```

## Security Features

- **Read-Only Queries**: Only SELECT queries are allowed
- **Query Validation**: Blocks UPDATE, DELETE, INSERT, DROP, ALTER, CREATE, TRUNCATE, GRANT, REVOKE
- **Connection Management**: Secure connection pooling with UUID-based IDs
- **Credential Handling**: Passwords not exposed in responses
- **Error Handling**: Detailed error messages without exposing sensitive data
- **CORS Configuration**: Can be enabled for web clients

## IDE-Specific Setup

### IntelliJ IDEA Setup

1. **Open Project:**
   - File → Open → Select the project folder
   - IntelliJ will automatically detect it as a Maven project

2. **Configure JDK:**
   - File → Project Structure → Project
   - Set SDK to Java 17 or higher

3. **Enable Annotation Processing (for Lombok):**
   - File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Install Lombok Plugin:**
   - File → Settings → Plugins
   - Search for "Lombok"
   - Install and restart IntelliJ

5. **Run Configuration:**
   - Run → Edit Configurations → Add New → Spring Boot
   - Name: `Oracle MCP Server`
   - Main class: `com.example.mcp.OracleMcpServerApplication`
   - Environment variables: `GEMINI_API_KEY=your-api-key`
   - Active profiles: `dev`

6. **Maven Tool Window:**
   - View → Tool Windows → Maven
   - Use this to run Maven goals (clean, install, spring-boot:run)

### VS Code Setup

1. **Install Required Extensions:**
   - Extension Pack for Java (Microsoft)
   - Maven for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support for VS Code

2. **Open Project:**
   - File → Open Folder → Select project folder

3. **Configure Java:**
   - Open Command Palette (Ctrl+Shift+P or Cmd+Shift+P)
   - Type: "Java: Configure Java Runtime"
   - Set Java 17 as the project JDK

4. **Launch Configuration:**

   The project includes `.vscode/launch.json` with Spring Boot configurations. Customize if needed:
   ```json
   {
       "version": "0.2.0",
       "configurations": [
           {
               "type": "java",
               "name": "Spring Boot - Oracle MCP Server",
               "request": "launch",
               "mainClass": "com.example.mcp.OracleMcpServerApplication",
               "projectName": "oracle-mcp-server",
               "env": {
                   "GEMINI_API_KEY": "your-api-key-here"
               }
           }
       ]
   }
   ```

5. **Run the Application:**
   - Press F5 or use Debug → Start Debugging
   - Or use integrated terminal: `mvn spring-boot:run`

## Troubleshooting

### Common Issues:

1. **"No LLM provider configured" warning**
   - Make sure you've set at least one API key environment variable
   - Restart your terminal/IDE after setting it
   - The server will still run but AI endpoints will return errors

2. **Maven dependencies not downloading**
   ```bash
   mvn clean install -U
   ```

3. **Lombok not working**
   - Make sure Lombok plugin is installed in your IDE
   - Enable annotation processing
   - Reimport Maven project

4. **Port 8080 already in use**
   - Change port in `src/main/resources/application.yml`:
     ```yaml
     spring:
       server:
         port: 8081
     ```
   - Or use command line: `mvn spring-boot:run -Dserver.port=8081`

5. **Cannot connect to Oracle Database**
   - Verify database credentials
   - Check network connectivity
   - Ensure Oracle database is running and accessible
   - Check firewall rules

6. **Java version mismatch**
   ```bash
   # Check your Java version
   java -version

   # Should be 17 or higher
   ```

7. **Gemini API 404 errors**
   - The application automatically tries multiple model variations
   - If all models fail, check your API key is valid
   - Verify your region has access to Gemini API
   - Try using OpenAI instead: `export OPENAI_API_KEY=...`

8. **Spring Boot application won't start**
   ```bash
   # Check logs for detailed error messages
   mvn spring-boot:run

   # Try cleaning and rebuilding
   mvn clean package -DskipTests
   ```

## Monitoring and Health Checks

### Spring Boot Actuator Endpoints

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Metrics information

### Custom Health Check

```bash
curl http://localhost:8080/api/oracle/health
```

Returns:
```json
{
  "status": "UP",
  "service": "Oracle MCP Server",
  "version": "1.0.0",
  "llmProvider": "Google Gemini (model: gemini-1.5-flash-8b-001)"
}
```

## Performance and Scalability

- **Connection Pooling**: UUID-based connection management
- **Stateless REST API**: Can be horizontally scaled
- **JSON Responses**: Efficient data transfer
- **Query Execution Time**: Tracked and returned with results
- **Spring Boot Actuator**: Built-in metrics and monitoring

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

## LangChain4J Integration

The `OracleDatabaseAIService` interface uses LangChain4J's AI Services pattern:
- Automatically registers all methods from `OracleDbTools` as AI tools
- Enables LLMs to call database operations naturally through function calling
- Provides system prompts for AI context
- Supports multiple LLM providers transparently

## Testing with Postman

Import the following collection to test all endpoints:

1. Create a new Postman collection
2. Add environment variable: `baseUrl = http://localhost:8080`
3. Add the API endpoints as shown in "API Usage Examples" section
4. Test each endpoint in sequence

## Future Enhancements

- [ ] Connection pooling optimization
- [ ] Query result caching with Redis
- [ ] Rate limiting for queries
- [ ] Audit logging for all operations
- [ ] WebSocket support for streaming results
- [ ] Stored procedure execution support
- [ ] Support for multiple simultaneous database connections
- [ ] Query result pagination
- [ ] GraphQL API support
- [ ] Kubernetes deployment configurations

## Contributing

This is a professional MCP server designed for production use. Contributions for enhancements are welcome.

## License

This project is provided as-is for professional use.

## Author

Generated with Claude Code - Oracle MCP Server v1.0.0

---

**Questions or Issues?**

- Check the Troubleshooting section above
- Review Spring Boot documentation: https://spring.io/projects/spring-boot
- Review LangChain4J documentation: https://docs.langchain4j.dev/
