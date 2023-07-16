package se.onlyfin.onlyfin2backend.service;

import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.onlyfin.onlyfin2backend.DTO.incoming.UserDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ProfileDTO;
import se.onlyfin.onlyfin2backend.model.RegistrationResponse;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO: MAKE LESS SHIT
 * This class is responsible for handling user management operations.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns a user with the given username if it exists.
     * Else throws a UsernameNotFound exception which causes a redirect to the login page to occur
     *
     * @param username The username of the user to be returned.
     * @return The user with the given username if it exists.
     */
    public User getUserOrException(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    /**
     * Returns a user with the given username if it exists.
     * Else throws a UsernameNotFound exception which causes a redirect to the login page to occur
     *
     * @param id The id of the user to be returned.
     * @return The user with the given id if it exists else throws an exception.
     */
    public User getUserOrException(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));
    }

    /**
     * Returns a user with the given username if it exists else null.
     *
     * @param username The username of the user to be returned.
     * @return The user with the given username if it exists.
     */
    public User getUserOrNull(@NonNull String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * @param id The id of the user to be returned.
     * @return The user with the given id if it exists else null.
     */
    public User getUserOrNull(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getAnalystOrNull(@NonNull String analystUsername) {
        return userRepository.findByisAnalystIsTrueAndUsernameEquals(analystUsername).orElse(null);
    }

    /**
     * Registers a user with the given details if the user is registrable.
     *
     * @param userDTO The details of the user to be registered.
     * @return The registered user if registration was successful else null.
     */
    @Transactional
    public RegistrationResponse registerUser(UserDTO userDTO) {
        RegistrationResponse registrationResponse = registrable(userDTO);
        if (!registrationResponse.equals(RegistrationResponse.OK)) {
            return registrationResponse;
        }

        User userToRegister = new User();
        userToRegister.setEnabled(true);

        userToRegister.setEmail(userDTO.email().toLowerCase());
        userToRegister.setUsername(userDTO.username().replaceAll("\\s", "").toLowerCase());
        userToRegister.setPassword(passwordEncoder.encode(userDTO.password()));

        userToRegister.setRoles("ROLE_USER");
        userToRegister.setAnalyst(true);

        userRepository.save(userToRegister);
        return registrationResponse;
    }

    /**
     * Checks if a userDTO is registrable
     *
     * @param user The user details DTO to be checked.
     * @return A registration response indicating if the user is registrable or not.
     */
    public RegistrationResponse registrable(UserDTO user) {
        if (user == null) {
            return RegistrationResponse.INVALID;
        }
        if (userRepository.existsByUsernameIgnoreCase(user.username())) {
            return RegistrationResponse.USERNAME_TAKEN;
        }
        if (userRepository.existsByEmailIgnoreCase(user.email())) {
            return RegistrationResponse.EMAIL_TAKEN;
        }
        /*
        Regex explanation:
        Usernames can contain characters a-z, 0-9, underscores and periods.
        The username cannot start with a period nor end with a period.
        It must also not have more than one period sequentially.
        Max length is 30 chars.
         */
        boolean validFormat = user.username().matches("^(?!.*\\.\\.)(?!.*\\.$)\\w[\\w.]{0,29}");
        if (!validFormat) {
            return RegistrationResponse.INVALID;
        }
        return RegistrationResponse.OK;
    }

    /**
     * @param rawPassword     The password to be checked.
     * @param encodedPassword The password to check against.
     * @return True if the passwords match
     */
    private boolean passwordMatch(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * @param targetUser                  The user to be updated.
     * @param currentPasswordConfirmation The old password to be checked.
     * @param newPassword                 The new password to be set.
     * @return If the password was changed
     */
    public boolean passwordChange(User targetUser, String currentPasswordConfirmation, String newPassword) {
        if (!passwordMatch(currentPasswordConfirmation, targetUser.getPassword())) {
            return false;
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        targetUser.setPassword(encodedNewPassword);
        saveUser(targetUser);
        return true;
    }

    /**
     * Enables analyst status for the given user if the user is not already an analyst.
     *
     * @param targetUser The user to become an analyst.
     * @return True if the user is now an analyst else false.
     */
    public boolean enableAnalyst(User targetUser) {
        if (targetUser.isAnalyst()) {
            return false;
        }

        targetUser.setAnalyst(true);
        userRepository.save(targetUser);

        return targetUser.isAnalyst();
    }

    /**
     * Disables analyst status for the given user if the user is an analyst.
     *
     * @param targetUser The user to no longer be an analyst.
     * @return True if the user is no longer an analyst else false.
     */
    public boolean disableAnalyst(User targetUser) {
        if (!targetUser.isAnalyst()) {
            return false;
        }

        targetUser.setAnalyst(false);
        userRepository.save(targetUser);

        return !targetUser.isAnalyst();
    }

    /**
     * Saves the given user to the database.
     *
     * @param targetUser The user to be saved.
     * @return The saved user.
     */
    public User saveUser(User targetUser) {
        return userRepository.save(targetUser);
    }

    public List<User> getAllAnalysts() {
        return userRepository.findAllByisAnalystIsTrue();
    }

    public List<User> findAnalystsByName(String searchQuery) {
        return userRepository.findAllByisAnalystIsTrueAndUsernameContainsIgnoreCase(searchQuery);
    }

    public List<ProfileDTO> usersToProfiles(List<User> users) {
        List<ProfileDTO> profiles = new ArrayList<>();

        for (User currentUser : users) {
            profiles.add(new ProfileDTO(currentUser.getId(), currentUser.getUsername()));
        }

        return profiles;
    }

    /**
     * THERE MAY BE SIDE EFFECTS WHEN DELETING USERS. PLZ FIX
     *
     * @param targetUser The user to be deleted.
     */
    @Deprecated
    public void deleteUser(User targetUser) {
        if (targetUser != null) {
            userRepository.delete(targetUser);
        }
    }

}