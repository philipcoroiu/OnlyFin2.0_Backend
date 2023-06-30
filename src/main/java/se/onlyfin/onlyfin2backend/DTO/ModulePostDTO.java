package se.onlyfin.onlyfin2backend.DTO;

import com.fasterxml.jackson.databind.JsonNode;

public record ModulePostDTO(Integer targetCategoryId,
                            Integer height,
                            Integer width,
                            Integer xAxis,
                            Integer yAxis,
                            String type,
                            JsonNode content) {
}