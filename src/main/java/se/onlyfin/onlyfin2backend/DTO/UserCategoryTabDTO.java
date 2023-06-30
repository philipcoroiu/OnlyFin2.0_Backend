package se.onlyfin.onlyfin2backend.DTO;

import java.util.List;

public record UserCategoryTabDTO(Integer userCategoryId, List<ModuleDTO> modules) {
}
