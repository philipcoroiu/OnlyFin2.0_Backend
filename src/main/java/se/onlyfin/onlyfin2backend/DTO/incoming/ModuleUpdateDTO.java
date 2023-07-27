package se.onlyfin.onlyfin2backend.DTO.incoming;

import com.fasterxml.jackson.databind.JsonNode;

public record ModuleUpdateDTO(String type, JsonNode content) {
}