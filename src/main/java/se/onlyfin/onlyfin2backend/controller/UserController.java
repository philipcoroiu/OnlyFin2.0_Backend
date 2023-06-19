package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.UserDTO;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.util.Optional;

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
        Optional<User> userOptional = userService.registerUser(userDTO);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Registration failed");
        }

        User registeredUser = userOptional.get();
        return ResponseEntity.ok(registeredUser.getUsername());
    }

}
