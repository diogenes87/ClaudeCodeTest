package com.example.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for database connection operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("connectionId")
    private String connectionId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("credentials")
    private ConnectionCredentials credentials;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionCredentials {
        @JsonProperty("username")
        private String username;

        @JsonProperty("server")
        private String server;

        @JsonProperty("sid")
        private String sid;

        @JsonProperty("port")
        private Integer port;
    }

    public static ConnectionResponse success(String connectionId, OracleConnectionRequest request) {
        return ConnectionResponse.builder()
                .success(true)
                .connectionId(connectionId)
                .message("Successfully connected to Oracle Database")
                .credentials(ConnectionCredentials.builder()
                        .username(request.getUsername())
                        .server(request.getServer())
                        .sid(request.getSid())
                        .port(request.getPort())
                        .build())
                .build();
    }

    public static ConnectionResponse failure(OracleConnectionRequest request, String errorMessage) {
        return ConnectionResponse.builder()
                .success(false)
                .message("Not possible to connect to DB with given credentials. Error: " + errorMessage)
                .credentials(ConnectionCredentials.builder()
                        .username(request.getUsername())
                        .server(request.getServer())
                        .sid(request.getSid())
                        .port(request.getPort())
                        .build())
                .build();
    }
}
