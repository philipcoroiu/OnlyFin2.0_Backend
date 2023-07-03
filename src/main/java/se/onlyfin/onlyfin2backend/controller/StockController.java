package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.onlyfin.onlyfin2backend.model.Stock;
import se.onlyfin.onlyfin2backend.repository.StockRepository;

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

    public StockController(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Returns all stocks in the database. (Not user stocks)
     *
     * @return all stocks in the database
     */
    @GetMapping("/all")
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        if (stocks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(stocks);
    }

    /**
     * Returns all stocks that match the search query. (Not user stocks)
     *
     * @param name the name of the stock to search for
     * @return stocks that match the search query
     */
    @GetMapping("/search")
    public ResponseEntity<?> findStocksByName(@RequestParam String name) {
        List<Stock> stocksFound = stockRepository.findByNameContainingIgnoreCase(name);
        if (stocksFound.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(stocksFound);
    }

    public Optional<Stock> getStock(Integer id) {
        return stockRepository.findById(id);
    }

}
