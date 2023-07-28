package se.onlyfin.onlyfin2backend.DTO.incoming;

public record ModuleLayoutUpdateBatchDTO(Integer moduleId,
                                         Integer height,
                                         Integer width,
                                         Integer xAxis,
                                         Integer yAxis) {
}