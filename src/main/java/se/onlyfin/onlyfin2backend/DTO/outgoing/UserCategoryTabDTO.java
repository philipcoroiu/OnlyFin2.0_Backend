package se.onlyfin.onlyfin2backend.DTO.outgoing;

import java.util.List;

public record UserCategoryTabDTO(Integer userCategoryId, String categoryName, List<ModuleDTO> modules) {
}
