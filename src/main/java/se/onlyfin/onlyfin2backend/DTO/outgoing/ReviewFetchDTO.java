package se.onlyfin.onlyfin2backend.DTO.outgoing;

public record ReviewFetchDTO(Integer id, String reviewText, ProfileDTO target, ProfileDTO author, Boolean isAuthor) {
}
