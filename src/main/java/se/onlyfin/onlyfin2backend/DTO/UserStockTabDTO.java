package se.onlyfin.onlyfin2backend.DTO;

import java.util.List;

public record UserStockTabDTO(Integer userStockId, List<UserCategoryTabDTO> categories) {
}
