package se.onlyfin.onlyfin2backend.DTO.outgoing;

import com.fasterxml.jackson.databind.JsonNode;

public record ModuleDTO(Integer targetCategoryId,
                        Integer height,
                        Integer width,
                        Integer xAxis,
                        Integer yAxis,
                        String type,
                        JsonNode content) {
}