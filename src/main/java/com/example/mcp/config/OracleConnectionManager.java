package com.example.mcp.config;

import com.example.mcp.dto.OracleConnectionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages Oracle Database connections
 */
@Slf4j
@Component
public class OracleConnectionManager {

    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    /**
     * Creates a new database connection
     *
     * @param request Connection request with credentials
     * @return Connection ID
     * @throws SQLException if connection fails
     */
    public String createConnection(OracleConnectionRequest request) throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found", e);
        }

        String jdbcUrl = request.getJdbcUrl();
        log.info("Attempting to connect to Oracle DB: {}", jdbcUrl);

        Connection connection = DriverManager.getConnection(
                jdbcUrl,
                request.getUsername(),
                request.getPassword()
        );

        String connectionId = UUID.randomUUID().toString();
        connections.put(connectionId, connection);

        log.info("Successfully created connection with ID: {}", connectionId);
        return connectionId;
    }

    /**
     * Gets an existing connection by ID
     *
     * @param connectionId Connection ID
     * @return Connection object
     * @throws SQLException if connection not found or invalid
     */
    public Connection getConnection(String connectionId) throws SQLException {
        Connection connection = connections.get(connectionId);
        if (connection == null) {
            throw new SQLException("Connection not found for ID: " + connectionId);
        }
        if (connection.isClosed()) {
            connections.remove(connectionId);
            throw new SQLException("Connection is closed for ID: " + connectionId);
        }
        return connection;
    }

    /**
     * Closes a connection
     *
     * @param connectionId Connection ID
     * @return true if closed successfully
     */
    public boolean closeConnection(String connectionId) {
        Connection connection = connections.remove(connectionId);
        if (connection != null) {
            try {
                connection.close();
                log.info("Closed connection: {}", connectionId);
                return true;
            } catch (SQLException e) {
                log.error("Error closing connection: {}", connectionId, e);
                return false;
            }
        }
        log.warn("Connection not found for closing: {}", connectionId);
        return false;
    }

    /**
     * Gets all active connection IDs
     */
    public Map<String, Connection> getActiveConnections() {
        return new ConcurrentHashMap<>(connections);
    }
}
