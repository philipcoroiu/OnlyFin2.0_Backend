package se.onlyfin.onlyfin2backend.DTO.outgoing;

import se.onlyfin.onlyfin2backend.model.Stock;

public record UserStockDTO(Integer id, Stock stock) {
}