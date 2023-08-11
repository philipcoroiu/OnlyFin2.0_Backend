package se.onlyfin.onlyfin2backend.DTO.incoming;

import jakarta.validation.constraints.Email;

/**
 * DTO used for sending user registration form data.
 *
 * @param email    the email of the user
 * @param username the username of the user
 * @param password the password of the user
 * @param turnstileToken the turnstile token from the frontend
 */
public record UserDTO(@Email String email, String username, String password, String turnstileToken) {
}