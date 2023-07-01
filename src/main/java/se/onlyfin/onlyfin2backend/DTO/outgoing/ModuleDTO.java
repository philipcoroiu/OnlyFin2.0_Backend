package se.onlyfin.onlyfin2backend.DTO.outgoing;

public record ModuleDTO(Integer targetCategoryId,
                        Integer height,
                        Integer width,
                        Integer xAxis,
                        Integer yAxis,
                        String type,
                        String content) {
}
