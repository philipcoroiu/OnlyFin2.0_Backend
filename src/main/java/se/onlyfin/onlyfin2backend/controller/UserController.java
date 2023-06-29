package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.ProfileDTO;
import se.onlyfin.onlyfin2backend.DTO.UserDTO;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user. If the username or email is already registered, a bad request is returned.
     *
     * @param userDTO UserDTO containing username, password and email.
     * @return ResponseEntity with status code 200 and username if registration was successful.
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
     * @param principal The logged-in user
     * @return All analysts in the database except the logged-in user.
     */
    @GetMapping("/search/all")
    public ResponseEntity<?> findAll(Principal principal) {
        boolean loggedIn = (principal != null);

        List<User> analysts = userService.getAllAnalysts();
        if (loggedIn) {
            User loggedInUser = userService.getUserOrException(principal.getName());
            analysts.remove(loggedInUser);
        }
        if (analysts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No users were found");
        }

        List<ProfileDTO> profiles = usersToProfiles(analysts);
        return ResponseEntity.ok().body(profiles);
    }

    /**
     * @param username The username of the analyst to be returned
     * @return The user with the given username if it exists.
     */
    @GetMapping("/username")
    public ResponseEntity<?> findByUsername(@RequestParam String username) {
        User targetUser = userService.getAnalystOrNull(username);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        ProfileDTO profile = userToProfile(targetUser);
        return ResponseEntity.ok().body(profile);
    }

    /**
     * @param username  The search query
     * @param principal The logged-in user
     * @return All analysts in the database except the logged-in user that match the search query.
     */
    @GetMapping("/search/username")
    public ResponseEntity<?> searchByUsername(Principal principal, @RequestParam String username) {
        //TODO: add sub-status here or in another endpoint when subscriptions are implemented
        boolean loggedIn = (principal != null);

        List<User> analystsFound = userService.findAnalystsByName(username);
        if (loggedIn) {
            User loggedInUser = userService.getUserOrException(principal.getName());
            analystsFound.remove(loggedInUser);
        }
        if (analystsFound.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProfileDTO> profiles = usersToProfiles(analystsFound);
        return ResponseEntity.ok().body(profiles);
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
        if (targetUsers == null) {
            return null;
        }

        List<ProfileDTO> profiles = new ArrayList<>();
        targetUsers.forEach((currentUser) ->
                profiles.add(new ProfileDTO(currentUser.getId(), currentUser.getUsername())));

        return profiles;
    }

}
