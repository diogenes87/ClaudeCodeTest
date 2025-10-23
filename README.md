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

4. **OpenAI API Key** (for LLM integration)
   - Sign up at: https://platform.openai.com/
   - Generate an API key from your account dashboard

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd ClaudeCodeTest
```

### Step 2: Configure Environment Variables

Set your OpenAI API key:

**Linux/macOS:**
```bash
export OPENAI_API_KEY=your-api-key-here
```

**Windows (Command Prompt):**
```cmd
set OPENAI_API_KEY=your-api-key-here
```

**Windows (PowerShell):**
```powershell
$env:OPENAI_API_KEY="your-api-key-here"
```

**Permanent Configuration (Linux/macOS):**

Add to `~/.bashrc` or `~/.zshrc`:
```bash
export OPENAI_API_KEY=your-api-key-here
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

## Running the Application

You have multiple options to run the Oracle MCP Server:

### Option 1: Interactive CLI Chat (Recommended for Testing)

This starts an interactive chat session where you can talk to the AI and it will use the Oracle DB tools to answer your questions.

**From Command Line:**
```bash
mvn exec:java
```

Or with explicit main class:
```bash
mvn exec:java -Dexec.mainClass="com.example.mcp.cli.CommandLineChat"
```

**From IntelliJ IDEA:**

1. Open the project in IntelliJ IDEA
2. Navigate to `src/main/java/com/example/mcp/cli/CommandLineChat.java`
3. Right-click on the file
4. Select **"Run 'CommandLineChat.main()'"**

Or:
1. Open `CommandLineChat.java`
2. Click the green play button (▶) next to the `main` method
3. Select **"Run 'CommandLineChat.main()'"**

**Set environment variables in IntelliJ:**
1. Go to **Run** → **Edit Configurations...**
2. Select the `CommandLineChat` configuration
3. Add Environment Variable: `OPENAI_API_KEY=your-api-key-here`
4. Click **OK**
5. Run the configuration

**From VS Code:**

1. Open the project in VS Code
2. Install the **"Extension Pack for Java"** (if not already installed)
3. Open `src/main/java/com/example/mcp/cli/CommandLineChat.java`
4. Click **"Run"** above the `main` method

Or use the integrated terminal:
```bash
mvn exec:java
```

**Set environment variables in VS Code:**

Create a `.vscode/launch.json` file:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "CommandLineChat",
            "request": "launch",
            "mainClass": "com.example.mcp.cli.CommandLineChat",
            "projectName": "oracle-mcp-server",
            "env": {
                "OPENAI_API_KEY": "your-api-key-here"
            }
        }
    ]
}
```

### Option 2: Run as Quarkus REST Server

Start the full Quarkus application with REST API endpoints:

**Development Mode (with hot reload):**
```bash
mvn quarkus:dev
```

The server will start at: `http://localhost:8080`

**From IntelliJ IDEA:**
1. Open Terminal in IntelliJ (View → Tool Windows → Terminal)
2. Run: `mvn quarkus:dev`

**From VS Code:**
1. Open integrated terminal (Ctrl+` or Cmd+`)
2. Run: `mvn quarkus:dev`

**Production Mode:**
```bash
# Build the application
mvn clean package

# Run the JAR
java -jar target/quarkus-app/quarkus-run.jar
```

### Option 3: Run Packaged JAR

```bash
# Package the application
mvn clean package -DskipTests

# Run the CLI
java -cp target/quarkus-app/quarkus-run.jar com.example.mcp.cli.CommandLineChat
```

## Using the Interactive CLI Chat

When you run the CLI chat application, you'll see:

```
╔════════════════════════════════════════════════════════════╗
║        Oracle MCP Server - Interactive Chat               ║
║        Powered by LangChain4J & OpenAI                     ║
╚════════════════════════════════════════════════════════════╝

✓ Connected to OpenAI with model: gpt-4
✓ Oracle Database tools loaded and ready

Available commands:
  - Type your question to chat with the AI
  - Type 'help' to see available database tools
  - Type 'exit' or 'quit' to end the session

Example questions:
  - Connect to database omsdb1/omsdb1@indltel300:DIODB100
  - Show me all tables in the database
  - What are the columns in the EMPLOYEES table?
  - Execute query: SELECT * FROM departments

════════════════════════════════════════════════════════════

You:
```

### Example Conversation:

```
You: Connect to database omsdb1/omsdb1@indltel300:DIODB100

AI: I'll connect to the Oracle database with those credentials...
[Shows connection result with connection ID]

You: Show me all tables in the database

AI: Here are all the tables in the database:
[Lists all tables]

You: What are the columns in the EMPLOYEES table?

AI: The EMPLOYEES table has the following columns:
[Lists columns with data types]

You: Execute this query: SELECT * FROM employees WHERE department = 'IT'

AI: Here are the results:
[Shows query results]

You: exit

Goodbye! Thanks for using Oracle MCP Server.
```

### CLI Commands:

- **help** - Display available database tools
- **exit** or **quit** - End the chat session
- **Any question** - Chat with AI (it will use tools automatically)

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
   - Run → Edit Configurations → Add New → Application
   - Name: `Oracle MCP CLI`
   - Main class: `com.example.mcp.cli.CommandLineChat`
   - Environment variables: `OPENAI_API_KEY=your-api-key`
   - Working directory: Project root

6. **Maven Tool Window:**
   - View → Tool Windows → Maven
   - Use this to run Maven goals (clean, install, quarkus:dev)

### VS Code Setup

1. **Install Required Extensions:**
   - Extension Pack for Java (Microsoft)
   - Maven for Java
   - Lombok Annotations Support for VS Code

2. **Open Project:**
   - File → Open Folder → Select project folder

3. **Configure Java:**
   - Open Command Palette (Ctrl+Shift+P or Cmd+Shift+P)
   - Type: "Java: Configure Java Runtime"
   - Set Java 17 as the project JDK

4. **Create Launch Configuration:**

   Create `.vscode/launch.json`:
   ```json
   {
       "version": "0.2.0",
       "configurations": [
           {
               "type": "java",
               "name": "Oracle MCP CLI",
               "request": "launch",
               "mainClass": "com.example.mcp.cli.CommandLineChat",
               "projectName": "oracle-mcp-server",
               "env": {
                   "OPENAI_API_KEY": "your-api-key-here"
               }
           },
           {
               "type": "java",
               "name": "Quarkus Dev",
               "request": "launch",
               "mainClass": "io.quarkus.runner.GeneratedMain",
               "projectName": "oracle-mcp-server",
               "preLaunchTask": "quarkus:dev"
           }
       ]
   }
   ```

5. **Create Tasks Configuration:**

   Create `.vscode/tasks.json`:
   ```json
   {
       "version": "2.0.0",
       "tasks": [
           {
               "label": "quarkus:dev",
               "type": "shell",
               "command": "mvn quarkus:dev",
               "isBackground": true,
               "problemMatcher": []
           }
       ]
   }
   ```

6. **Run the Application:**
   - Press F5 or use Debug → Start Debugging
   - Select the desired configuration

## Troubleshooting

### Common Issues:

1. **"OPENAI_API_KEY environment variable is not set"**
   - Make sure you've set the environment variable
   - Restart your terminal/IDE after setting it

2. **Maven dependencies not downloading**
   ```bash
   mvn clean install -U
   ```

3. **Lombok not working**
   - Make sure Lombok plugin is installed in your IDE
   - Enable annotation processing
   - Reimport Maven project

4. **Port 8080 already in use** (for Quarkus server)
   - Change port in `src/main/resources/application.properties`:
     ```properties
     quarkus.http.port=8081
     ```

5. **Cannot connect to Oracle Database**
   - Verify database credentials
   - Check network connectivity
   - Ensure Oracle database is running and accessible

6. **Java version mismatch**
   ```bash
   # Check your Java version
   java -version

   # Should be 17 or higher
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
