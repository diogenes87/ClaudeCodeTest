package com.example.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for table and column information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableColumnInfo {

    @JsonProperty("tables")
    private Map<String, List<ColumnDetail>> tables;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnDetail {
        @JsonProperty("columnName")
        private String columnName;

        @JsonProperty("dataType")
        private String dataType;

        @JsonProperty("nullable")
        private String nullable;

        @JsonProperty("dataLength")
        private Integer dataLength;

        @JsonProperty("dataPrecision")
        private Integer dataPrecision;

        @JsonProperty("dataScale")
        private Integer dataScale;
    }
}
