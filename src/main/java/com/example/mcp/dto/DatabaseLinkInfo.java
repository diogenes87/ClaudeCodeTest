package com.example.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for database link information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseLinkInfo {

    @JsonProperty("dbLinks")
    private List<DbLinkDetail> dbLinks;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DbLinkDetail {
        @JsonProperty("dbLinkName")
        private String dbLinkName;

        @JsonProperty("username")
        private String username;

        @JsonProperty("host")
        private String host;

        @JsonProperty("created")
        private String created;
    }
}
