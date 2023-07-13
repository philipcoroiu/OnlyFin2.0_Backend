package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.incoming.AboutMeUpdateDTO;
import se.onlyfin.onlyfin2backend.DTO.incoming.PasswordChangeDTO;
import se.onlyfin.onlyfin2backend.DTO.incoming.UserDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ProfileDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ProfileSubInfoAboutMeDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ProfileSubInfoDTO;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: ADD MORE API CALLS FOR USER SERVICE FUNCTIONS
 * This class is responsible for handling requests related to user management.
 */
@RequestMapping("/users")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class UserController {
    private final UserService userService;
    private final SubscriptionController subscriptionController;

    public UserController(UserService userService, SubscriptionController subscriptionController) {
        this.userService = userService;
        this.subscriptionController = subscriptionController;
    }

    /**
     * Returns the logged-in user's username if logged in, otherwise returns a 204 NO CONTENT.
     *
     * @param principal The logged-in user
     * @return The logged-in user's username if logged in, otherwise returns a 204 NO CONTENT.
     */
    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI(Principal principal) {
        boolean loggedIn = (principal != null);

        if (!loggedIn) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok().body(principal.getName());
    }

    /**
     * Registers a new user. If the username or email is already registered, a bad request is returned.
     *
     * @param userDTO UserDTO containing username, password and email.
     * @return HTTP 200 OK and username if registration was successful. HTTP 400 BAD REQUEST if registration failed.
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerNewUser(@RequestBody UserDTO userDTO) {
        User registeredUser = userService.registerUser(userDTO).orElse(null);
        if (registeredUser == null) {
            return ResponseEntity.badRequest().body("Registration failed");
        }

        return ResponseEntity.ok(registeredUser.getUsername());
    }

    /**
     * Returns all analysts in the database except the logged-in user. Includes subscription information.
     *
     * @param principal The logged-in user
     * @return All analysts in the database except the logged-in user. If no analysts are found, a 204 NO CONTENT is returned.
     */
    @GetMapping("/search/all")
    public ResponseEntity<?> findAll(Principal principal) {
        boolean loggedIn = (principal != null);
        User loggedInUser = null;

        List<User> analysts = userService.getAllAnalysts();
        if (loggedIn) {
            loggedInUser = userService.getUserOrException(principal.getName());
            analysts.remove(loggedInUser);
        }
        if (analysts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users were found");
        }

        List<ProfileSubInfoDTO> profiles = usersToProfilesWithSubInfo(loggedInUser, analysts);
        return ResponseEntity.ok().body(profiles);
    }

    /**
     * Returns the details of a specific user. Includes subscription information.
     *
     * @param principal the logged-in user
     * @param username  The username of the analyst to be returned
     * @return The user with the given username. If no user is found, a 404 NOT FOUND is returned.
     */
    @GetMapping("/username")
    public ResponseEntity<?> findByUsername(Principal principal, @RequestParam String username) {
        //acting user is optional as it is only needed for sub check.
        // will fall back to subscribing=false if not logged in
        User actingUser = null;
        if (principal != null) {
            actingUser = userService.getUserOrNull(principal.getName());
        }

        User targetUser = userService.getAnalystOrNull(username);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        ProfileSubInfoAboutMeDTO profile = userToProfileWithSubInfoAboutMe(actingUser, targetUser);
        return ResponseEntity.ok().body(profile);
    }

    /**
     * Returns all analysts that match the search query. Excludes the logged-in user. Includes subscription information.
     *
     * @param username  The search query
     * @param principal The logged-in user
     * @return All analysts in the database except the logged-in user that match the search query.
     * If no analysts are found, a 204 NO CONTENT is returned.
     */
    @GetMapping("/search/username")
    public ResponseEntity<?> searchByUsername(Principal principal, @RequestParam String username) {
        boolean loggedIn = (principal != null);
        User loggedInUser = null;

        List<User> analystsFound = userService.findAnalystsByName(username);
        if (loggedIn) {
            loggedInUser = userService.getUserOrException(principal.getName());
            analystsFound.remove(loggedInUser);
        }
        if (analystsFound.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProfileSubInfoDTO> profiles = usersToProfilesWithSubInfo(loggedInUser, analystsFound);
        return ResponseEntity.ok().body(profiles);
    }

    /**
     * Returns the "about me" text of a specific user.
     *
     * @param targetUsername The username of the user to be fetched
     * @return The about me text of the user with the given username. If no user is found, a 404 NOT FOUND is returned.
     */
    @GetMapping("/about-me")
    public ResponseEntity<?> fetchAboutMe(@RequestParam String targetUsername) {
        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        String aboutMeText = targetUser.getAboutMe();

        return ResponseEntity.ok().body(aboutMeText);
    }

    /**
     * Updates the "about me" text of the logged-in user.
     *
     * @param principal  The logged-in user
     * @param newAboutMe The new "about me" text
     * @return The new "about me" text
     */
    @PutMapping("/update-about-me")
    public ResponseEntity<?> updateAboutMe(Principal principal, @RequestBody AboutMeUpdateDTO newAboutMe) {
        User actingUser = userService.getUserOrException(principal.getName());

        actingUser.setAboutMe(newAboutMe.newAboutMe());
        userService.saveUser(actingUser);

        return ResponseEntity.ok().body(newAboutMe);
    }

    /**
     * @param principal The logged-in user
     * @param passwordChangeDTO Old password confirmation and the new password
     * @return HTTP 200 OK if the password was changed successfully. HTTP 400 BAD REQUEST if the password change failed.
     */
    @PostMapping("/password-change")
    public ResponseEntity<String> changeUserPassword(Principal principal, @RequestBody PasswordChangeDTO passwordChangeDTO) {
        User targetUser = userService.getUserOrException(principal.getName());

        boolean successful = userService.passwordChange(targetUser, passwordChangeDTO.oldPassword(), passwordChangeDTO.newPassword());
        if (!successful) {
            return ResponseEntity.badRequest().body("Password change failed");
        }

        return ResponseEntity.ok().body("Updated password successfully");
    }

    /*
     ********************************
     *                              *
     * --- END OF API ENDPOINTS --- *
     *                              *
     ********************************
     */

    /**
     * @param targetUser The user to be converted to a ProfileDTO
     * @return A ProfileDTO containing the id and username of the given user.
     */
    public ProfileDTO userToProfile(User targetUser) {
        if (targetUser == null) {
            return null;
        }

        return new ProfileDTO(targetUser.getId(), targetUser.getUsername());
    }

    /**
     * @param targetUsers The users to be converted to ProfileDTOs
     * @return A list of ProfileDTOs containing the id and username of the given users.
     */
    public List<ProfileDTO> usersToProfiles(List<User> targetUsers) {
        List<ProfileDTO> profiles = new ArrayList<>();
        for (User currentUser : targetUsers) {
            profiles.add(new ProfileDTO(currentUser.getId(), currentUser.getUsername()));
        }

        return profiles;
    }

    public ProfileSubInfoDTO userToProfileWithSubInfo(@Nullable User actingUser, User targetUser) {
        boolean loggedIn = (actingUser != null);
        if (!loggedIn) {
            return new ProfileSubInfoDTO(targetUser.getId(), targetUser.getUsername(), false);
        }

        boolean isSubscribed = subscriptionController.subCheck(actingUser, targetUser);

        return new ProfileSubInfoDTO(targetUser.getId(), targetUser.getUsername(), isSubscribed);
    }

    public ProfileSubInfoAboutMeDTO userToProfileWithSubInfoAboutMe(@Nullable User actingUser, User targetUser) {
        boolean loggedIn = (actingUser != null);
        if (!loggedIn) {
            return new ProfileSubInfoAboutMeDTO(targetUser.getId(), targetUser.getUsername(), false, targetUser.getAboutMe());
        }

        boolean isSubscribed = subscriptionController.subCheck(actingUser, targetUser);

        return new ProfileSubInfoAboutMeDTO(targetUser.getId(), targetUser.getUsername(), isSubscribed, targetUser.getAboutMe());
    }

    public List<ProfileSubInfoDTO> usersToProfilesWithSubInfo(@Nullable User actingUser, List<User> targetUsers) {
        boolean loggedIn = (actingUser != null);
        if (!loggedIn) {
            List<ProfileSubInfoDTO> profilesWithFallbackSubInfo = new ArrayList<>();
            for (User currentUser : targetUsers) {
                profilesWithFallbackSubInfo.add(new ProfileSubInfoDTO(
                        currentUser.getId(),
                        currentUser.getUsername(),
                        false
                ));
            }

            return profilesWithFallbackSubInfo;
        }

        List<User> subscriptions = subscriptionController.subscriptionList(actingUser);
        List<ProfileSubInfoDTO> profilesWithSubInfo = new ArrayList<>();
        for (User currentUser : targetUsers) {
            boolean isSubscribed = subscriptions.contains(currentUser);
            profilesWithSubInfo.add(new ProfileSubInfoDTO(
                    currentUser.getId(),
                    currentUser.getUsername(),
                    isSubscribed
            ));
        }

        return profilesWithSubInfo;
    }

}
