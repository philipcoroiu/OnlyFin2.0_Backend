package se.onlyfin.onlyfin2backend.DTO.outgoing;

import java.util.List;

public record UserStockTabDTO(Integer userStockId, List<UserCategoryTabDTO> categories) {
}
