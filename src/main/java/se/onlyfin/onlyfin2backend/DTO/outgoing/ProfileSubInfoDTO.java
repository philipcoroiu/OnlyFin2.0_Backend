package se.onlyfin.onlyfin2backend.DTO.outgoing;

public record ProfileSubInfoDTO(Integer id, String username, Boolean isSubscribed, Long subscriptionCount) {
}