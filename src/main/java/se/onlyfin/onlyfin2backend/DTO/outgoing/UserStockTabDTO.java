package se.onlyfin.onlyfin2backend.DTO.outgoing;

import se.onlyfin.onlyfin2backend.model.Stock;

import java.util.List;

public record UserStockTabDTO(Integer userStockId, Stock stock, List<UserCategoryTabDTO> categories) {
}