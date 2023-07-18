package se.onlyfin.onlyfin2backend.model;

/**
 * This enum represents possible responses that can be received by the client when attempting a user registration.
 */
public enum RegistrationResponse {

    OK,
    EMAIL_TAKEN,
    USERNAME_TAKEN,
    INVALID,
}