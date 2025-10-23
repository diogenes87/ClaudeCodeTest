package com.example.mcp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for stored procedure information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureInfo {

    @JsonProperty("procedures")
    private List<ProcedureDetail> procedures;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcedureDetail {
        @JsonProperty("procedureName")
        private String procedureName;

        @JsonProperty("objectType")
        private String objectType;

        @JsonProperty("parameters")
        private List<ParameterDetail> parameters;

        @JsonProperty("status")
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParameterDetail {
        @JsonProperty("parameterName")
        private String parameterName;

        @JsonProperty("position")
        private Integer position;

        @JsonProperty("dataType")
        private String dataType;

        @JsonProperty("inOut")
        private String inOut;

        @JsonProperty("dataLength")
        private Integer dataLength;

        @JsonProperty("dataPrecision")
        private Integer dataPrecision;

        @JsonProperty("dataScale")
        private Integer dataScale;
    }
}
