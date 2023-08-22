package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.incoming.CustomStockPostDTO;
import se.onlyfin.onlyfin2backend.model.Stock;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.model.UserStock;
import se.onlyfin.onlyfin2backend.repository.StockRepository;
import se.onlyfin.onlyfin2backend.repository.UserStockRepository;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * This class is responsible for handling requests related to stocks.
 */
@RequestMapping("/stocks")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class StockController {
    private final StockRepository stockRepository;
    private final UserService userService;
    private final UserStockRepository userStockRepository;

    public StockController(StockRepository stockRepository, UserService userService, UserStockRepository userStockRepository) {
        this.stockRepository = stockRepository;
        this.userService = userService;
        this.userStockRepository = userStockRepository;
    }

    /**
     * Returns all stocks in the database. (Not user stocks)
     *
     * @return all stocks in the database. If no stocks exist, a 204 NO CONTENT is returned.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockRepository.findAllByOwnerIsNull();
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(stocks);
    }

    /**
     * Returns all stocks that match the search query. (Not user stocks)
     *
     * @param name the name of the stock to search for
     * @return stocks that match the search query. If no stocks match the search query, a 204 NO CONTENT is returned.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Stock>> findStocksByName(@RequestParam String name) {
        List<Stock> stocksFound = stockRepository.findByNameContainingIgnoreCaseAndOwnerIsNull(name);
        if (stocksFound.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(stocksFound);
    }

    @PostMapping("/add-custom-stock")
    public ResponseEntity<Void> addCustomStock(Principal principal, @RequestBody CustomStockPostDTO customStockPostDTO) {
        User actingUser = userService.getUserOrException(principal.getName());

        if (customStockPostDTO.ticker() != null && customStockPostDTO.ticker().length() > 10) {
            return ResponseEntity.badRequest().build();
        }

        //Creates the custom stock.
        Stock customStock = new Stock();
        customStock.setOwner(actingUser);
        customStock.setName(customStockPostDTO.name());
        customStock.setTicker(customStockPostDTO.ticker());

        stockRepository.save(customStock);

        //Adds the stock to the users dashboard.
        UserStock userStock = new UserStock();
        userStock.setUser(actingUser);
        userStock.setStock(customStock);

        userStockRepository.save(userStock);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-custom-stock")
    public ResponseEntity<Void> deleteCustomStock(Principal principal, @RequestParam Integer customStockId) {
        User actingUser = userService.getUserOrException(principal.getName());

        Stock targetStock = stockRepository.findById(customStockId).orElse(null);
        if (targetStock == null) {
            return ResponseEntity.notFound().build();
        }
        if (actingUser != targetStock.getOwner()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        stockRepository.deleteById(customStockId);

        return ResponseEntity.ok().build();
    }

    public Optional<Stock> getStock(Integer id) {
        return stockRepository.findById(id);
    }

}
