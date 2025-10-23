package com.example.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for SQL query results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("columns")
    private List<String> columns;

    @JsonProperty("rows")
    private List<Map<String, Object>> rows;

    @JsonProperty("rowCount")
    private int rowCount;

    @JsonProperty("executionTimeMs")
    private long executionTimeMs;

    @JsonProperty("message")
    private String message;
}
