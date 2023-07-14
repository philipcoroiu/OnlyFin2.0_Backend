package se.onlyfin.onlyfin2backend.controller;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.incoming.ReviewPostDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ProfileDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ReviewFetchDTO;
import se.onlyfin.onlyfin2backend.model.Review;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.repository.ReviewRepository;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/reviews")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    public ReviewController(ReviewRepository reviewRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.userService = userService;
    }

    /**
     * Pushes a review for a specified user. If the user already has a review, it will be overwritten.
     *
     * @param principal     The logged-in user
     * @param reviewPostDTO The review to push
     * @return 200 OK if successful, 400 Bad Request if the target user doesn't exist
     */
    @PutMapping("/push")
    @Transactional
    public ResponseEntity<?> pushReview(Principal principal, @RequestBody ReviewPostDTO reviewPostDTO) {
        User actingUser = userService.getUserOrException(principal.getName());

        User targetUser = userService.getUserOrNull(reviewPostDTO.targetUsername());
        if (targetUser == null) {
            return ResponseEntity.badRequest().build();
        }

        reviewRepository.deleteAllByAuthor(actingUser);

        Review review = new Review();
        review.setAuthor(actingUser);
        review.setTarget(targetUser);
        review.setReviewText(reviewPostDTO.reviewText());

        reviewRepository.save(review);

        return ResponseEntity.ok().body(review.getReviewText());
    }

    /**
     * Deletes all reviews written by the logged-in user for the specified user.
     *
     * @param principal The logged-in user
     * @return 200 OK if successful, 404 Not Found if the user doesn't exist
     */
    @DeleteMapping("/delete")
    @Transactional
    public ResponseEntity<?> deleteReview(Principal principal, @RequestParam String targetUsername) {
        User actingUser = userService.getUserOrException(principal.getName());

        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        reviewRepository.deleteAllByAuthorAndTarget(actingUser, targetUser);

        return ResponseEntity.ok().build();
    }

    /**
     * @param principal      The logged-in user
     * @param targetUsername The username of the user to get reviews for
     * @return 200 OK if successful, 204 No Content if the user has no reviews, 404 Not Found if the user doesn't exist
     */
    @GetMapping("/get")
    public ResponseEntity<List<ReviewFetchDTO>> getReviews(Principal principal, @RequestParam String targetUsername) {
        boolean loggedIn = (principal != null);

        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        List<Review> rawReviews = reviewRepository.findAllByTarget(targetUser);

        List<ReviewFetchDTO> reviews;
        if (loggedIn) {
            User actingUser = userService.getUserOrException(principal.getName());
            reviews = reviewToReviewFetchDTO(actingUser, rawReviews);
            reviews.removeIf(ReviewFetchDTO::isAuthor);
        } else {
            reviews = reviewToReviewFetchDTOUnauthenticated(rawReviews);
        }

        if (reviews.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(reviews);
    }

    /**
     * Converts a list of reviews to a list of ReviewFetchDTOs.
     * If the logged-in user is the author of a review, the ReviewFetchDTO will have the "isAuthor" boolean set to true.
     *
     * @param actingUser The logged-in user
     * @param rawReviews The reviews to convert
     * @return A list of ReviewFetchDTOs
     */
    public List<ReviewFetchDTO> reviewToReviewFetchDTO(User actingUser, List<Review> rawReviews) {
        return rawReviews.stream()
                .map(currentReview -> new ReviewFetchDTO(
                        currentReview.getId(),
                        currentReview.getReviewText(),
                        new ProfileDTO(currentReview.getTarget().getId(), currentReview.getTarget().getUsername()),
                        new ProfileDTO(currentReview.getAuthor().getId(), currentReview.getAuthor().getUsername()),
                        actingUser == currentReview.getAuthor()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Converts a list of reviews to a list of ReviewFetchDTOs.
     * The "isAuthor" boolean will always be set to false.
     *
     * @param rawReviews The reviews to convert
     * @return A list of ReviewFetchDTOs
     */
    public List<ReviewFetchDTO> reviewToReviewFetchDTOUnauthenticated(List<Review> rawReviews) {
        return rawReviews.stream()
                .map(currentReview -> new ReviewFetchDTO(
                        currentReview.getId(),
                        currentReview.getReviewText(),
                        new ProfileDTO(currentReview.getTarget().getId(), currentReview.getTarget().getUsername()),
                        new ProfileDTO(currentReview.getAuthor().getId(), currentReview.getAuthor().getUsername()),
                        false
                ))
                .collect(Collectors.toList());
    }

}
