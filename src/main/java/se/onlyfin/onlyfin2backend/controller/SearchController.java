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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is responsible for handling requests related to searching for users and stocks.
 */
@RequestMapping("/search")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class SearchController {
    private final StockController stockController;
    private final UserStockRepository userStockRepository;

    public SearchController(StockController stockController, UserStockRepository userStockRepository) {
        this.stockController = stockController;
        this.userStockRepository = userStockRepository;
    }

    /**
     * Finds analysts that cover the target stock.
     *
     * @param targetStockId The id of the stock to find analysts for.
     * @return A list of analysts that cover the target stock.
     * If no analysts cover the target stock, a 204 NO CONTENT is returned.
     * If the target stock does not exist, a 404 NOT FOUND is returned.
     */
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
