package se.onlyfin.onlyfin2backend.DTO.outgoing;

import java.util.List;

public record DashboardDTO(List<UserStockTabDTO> stocks) {
}
