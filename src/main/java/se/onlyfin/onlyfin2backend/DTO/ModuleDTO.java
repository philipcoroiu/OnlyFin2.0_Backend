package se.onlyfin.onlyfin2backend.DTO;

public record ModuleDTO(Integer targetCategoryId,
                        Integer height,
                        Integer width,
                        Integer xAxis,
                        Integer yAxis,
                        String type,
                        String content) {
}
