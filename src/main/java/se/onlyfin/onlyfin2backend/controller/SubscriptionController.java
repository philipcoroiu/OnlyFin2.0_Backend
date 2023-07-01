package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.model.Subscription;
import se.onlyfin.onlyfin2backend.model.SubscriptionId;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.repository.SubscriptionRepository;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.security.Principal;

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

    @PutMapping("/add")
    public ResponseEntity<?> subscribe(Principal principal, @RequestParam String targetUsername) {
        User actingUser = userService.getUserOrException(principal.getName());

        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        SubscriptionId subscriptionId = new SubscriptionId();
        subscriptionId.setSubscriber(actingUser);
        subscriptionId.setSubscribedTo(targetUser);
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);

        subscriptionRepository.save(subscription);

        return ResponseEntity.ok().build();
    }

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

}
