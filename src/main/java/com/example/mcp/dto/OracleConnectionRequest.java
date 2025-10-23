package com.example.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Oracle Database connection request
 * Example: omsdb1/omsdb1@indltel300:DIODB100
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OracleConnectionRequest {

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("server")
    private String server;

    @JsonProperty("sid")
    private String sid;

    @JsonProperty("port")
    @Builder.Default
    private Integer port = 1521;

    /**
     * Parses connection string in format: username/password@server:SID
     */
    public static OracleConnectionRequest fromConnectionString(String connectionString) {
        try {
            String[] parts = connectionString.split("@");
            String[] credentials = parts[0].split("/");
            String[] serverParts = parts[1].split(":");

            return OracleConnectionRequest.builder()
                    .username(credentials[0])
                    .password(credentials[1])
                    .server(serverParts[0])
                    .sid(serverParts[1])
                    .build();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid connection string format. Expected: username/password@server:SID");
        }
    }

    public String getJdbcUrl() {
        return String.format("jdbc:oracle:thin:@%s:%d:%s", server, port, sid);
    }
}
