package se.onlyfin.onlyfin2backend.DTO.outgoing;

public record ProfileExtendedDTO(Integer id, String username, Boolean isSubscribed, String aboutMe, Boolean self) {
}
