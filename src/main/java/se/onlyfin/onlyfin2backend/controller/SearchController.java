package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ProfileDTO;
import se.onlyfin.onlyfin2backend.model.Stock;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.repository.UserStockRepository;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequestMapping("/search")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class SearchController {
    private final StockController stockController;
    private final UserService userService;
    private final UserStockRepository userStockRepository;

    public SearchController(StockController stockController, UserService userService, UserStockRepository userStockRepository) {
        this.stockController = stockController;
        this.userService = userService;
        this.userStockRepository = userStockRepository;
    }

    @GetMapping("/covers-stock")
    public ResponseEntity<List<ProfileDTO>> findAnalystsThatCoverStock(@RequestParam Integer targetStockId) {
        Stock targetStock = stockController.getStock(targetStockId).orElse(null);
        if (targetStock == null) {
            return ResponseEntity.notFound().build();
        }

        Set<User> usersCoveringTargetStock = userStockRepository.findUsersByStockId(targetStockId);
        if (usersCoveringTargetStock.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProfileDTO> profiles = new ArrayList<>();
        for (User currentUser : usersCoveringTargetStock) {
            profiles.add(new ProfileDTO(currentUser.getId(), currentUser.getUsername()));
        }

        return ResponseEntity.ok().body(profiles);
    }

}
