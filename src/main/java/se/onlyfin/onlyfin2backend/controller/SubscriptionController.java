package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ProfileDTO;
import se.onlyfin.onlyfin2backend.model.Subscription;
import se.onlyfin.onlyfin2backend.model.SubscriptionId;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.repository.SubscriptionRepository;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for handling requests related to subscriptions.
 */
@RequestMapping("/subscriptions")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class SubscriptionController {
    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionController(UserService userService, SubscriptionRepository subscriptionRepository) {
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Adds a subscription for the logged-in user to a user with the given username.
     *
     * @param principal      The logged-in user.
     * @param targetUsername The username of the user to subscribe to.
     * @return 200 OK if the subscription was successful, 404 Not Found if the target user does not exist.
     */
    @PutMapping("/add")
    public ResponseEntity<?> subscribe(Principal principal, @RequestParam String targetUsername) {
        User actingUser = userService.getUserOrException(principal.getName());

        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (actingUser == targetUser) {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
        }

        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptionId.setSubscriber(actingUser);
        subscriptionId.setSubscribedTo(targetUser);
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);

        subscriptionRepository.save(subscription);

        return ResponseEntity.ok().build();
    }

    /**
     * Removes a subscription for the logged-in user from a user with the given username.
     *
     * @param principal      The logged-in user.
     * @param targetUsername The username of the user to unsubscribe from.
     * @return 200 OK if the unsubscription was successful, 404 Not Found if the target user does not exist.
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> unsubscribe(Principal principal, @RequestParam String targetUsername) {
        User actingUser = userService.getUserOrException(principal.getName());

        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptionId.setSubscriber(actingUser);
        subscriptionId.setSubscribedTo(targetUser);

        subscriptionRepository.deleteById(subscriptionId);

        return ResponseEntity.ok().build();
    }

    /**
     * @param principal      The logged-in user.
     * @param targetUsername The username of the user to check if the logged-in user is subscribed to.
     * @return If the logged-in user is subscribed to the user with the given username.
     * 404 Not Found if the target user does not exist.
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkSubscription(Principal principal, @RequestParam String targetUsername) {
        User actingUser = userService.getUserOrException(principal.getName());

        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptionId.setSubscriber(actingUser);
        subscriptionId.setSubscribedTo(targetUser);

        boolean isSubscribed = subscriptionRepository.existsById(subscriptionId);

        return ResponseEntity.ok(isSubscribed);
    }

    /**
     * Returns a list of users that the logged-in user is subscribed to.
     *
     * @param principal The logged-in user.
     * @return A list of users that the logged-in user is subscribed to. 204 No Content if no subscriptions exists.
     */
    @GetMapping("/list")
    public ResponseEntity<?> getSubscriptionList(Principal principal) {
        User actingUser = userService.getUserOrException(principal.getName());

        List<User> subscriptions = subscriptionList(actingUser);
        if (subscriptions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProfileDTO> profiles = userService.usersToProfiles(subscriptions);
        return ResponseEntity.ok().body(profiles);
    }

    /**
     * Returns the subscription count of a specified user
     *
     * @param targetUsername username of the target user
     * @return the number of users subscribed to the target user
     */
    @GetMapping("/count")
    public ResponseEntity<Number> getUserSubscriptionCount(@RequestParam String targetUsername) {
        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        Long subscriptionCount = subscriptionRepository.countByIdSubscribedTo(targetUser);

        return ResponseEntity.ok().body(subscriptionCount);
    }

    public boolean subCheck(User subscribingUser, User subscribedToUser) {
        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptionId.setSubscriber(subscribingUser);
        subscriptionId.setSubscribedTo(subscribedToUser);

        return subscriptionRepository.existsById(subscriptionId);
    }

    public List<User> subscriptionList(User subscribingUser) {
        List<Subscription> subscriptions = new ArrayList<>(subscriptionRepository.findByIdSubscriber(subscribingUser));

        return subscriptions.stream()
                .map(currentSubscription -> currentSubscription.getId().getSubscribedTo())
                .toList();
    }

}
