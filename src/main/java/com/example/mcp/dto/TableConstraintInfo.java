package com.example.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for table primary key and foreign key information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableConstraintInfo {

    @JsonProperty("tables")
    private Map<String, TableConstraints> tables;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableConstraints {
        @JsonProperty("primaryKeys")
        private List<PrimaryKeyInfo> primaryKeys;

        @JsonProperty("foreignKeys")
        private List<ForeignKeyInfo> foreignKeys;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrimaryKeyInfo {
        @JsonProperty("constraintName")
        private String constraintName;

        @JsonProperty("columnName")
        private String columnName;

        @JsonProperty("position")
        private Integer position;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForeignKeyInfo {
        @JsonProperty("constraintName")
        private String constraintName;

        @JsonProperty("columnName")
        private String columnName;

        @JsonProperty("referencedTableName")
        private String referencedTableName;

        @JsonProperty("referencedColumnName")
        private String referencedColumnName;

        @JsonProperty("position")
        private Integer position;
    }
}
