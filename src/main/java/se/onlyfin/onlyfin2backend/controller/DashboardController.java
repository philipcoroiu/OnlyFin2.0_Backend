package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.CategoryCreationDTO;
import se.onlyfin.onlyfin2backend.model.Stock;
import se.onlyfin.onlyfin2backend.model.User;
import se.onlyfin.onlyfin2backend.model.UserCategory;
import se.onlyfin.onlyfin2backend.model.UserStock;
import se.onlyfin.onlyfin2backend.repository.DashboardModuleRepository;
import se.onlyfin.onlyfin2backend.repository.StockRepository;
import se.onlyfin.onlyfin2backend.repository.UserCategoryRepository;
import se.onlyfin.onlyfin2backend.repository.UserStockRepository;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.security.Principal;
import java.util.Objects;

@RequestMapping("/dash")
@CrossOrigin(origins = "localhost:3000", allowCredentials = "true")
@Controller
public class DashboardController {
    private final UserService userService;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final DashboardModuleRepository dashboardModuleRepository;

    public DashboardController(UserService userService, StockRepository stockRepository, UserStockRepository userStockRepository, UserCategoryRepository userCategoryRepository, DashboardModuleRepository dashboardModuleRepository) {
        this.userService = userService;
        this.stockRepository = stockRepository;
        this.userStockRepository = userStockRepository;
        this.userCategoryRepository = userCategoryRepository;
        this.dashboardModuleRepository = dashboardModuleRepository;
    }

    @PostMapping("/add-stock")
    public ResponseEntity<?> addStock(Principal principal, @RequestParam Integer targetStockId) {
        User actingUser = userService.getUserOrException(principal.getName());

        Stock targetStock = stockRepository.findById(targetStockId).orElse(null);
        if (targetStock == null) {
            return ResponseEntity.notFound().build();
        }

        UserStock userStock = new UserStock();
        userStock.setUser(actingUser);
        userStock.setStock(targetStock);
        UserStock save = userStockRepository.save(userStock);

        return ResponseEntity.ok().body(targetStock.getName());
    }

    @DeleteMapping("/delete-stock")
    public ResponseEntity<?> deleteStock(Principal principal, @RequestParam Integer targetUserStockId) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserStock targetUserStock = userStockRepository.findById(targetUserStockId).orElse(null);
        if (targetUserStock == null) {
            return ResponseEntity.badRequest().build();
        }
        //permission check
        if (!Objects.equals(targetUserStock.getUser().getId(), actingUser.getId())) {
            return ResponseEntity.badRequest().build();
        }

        userStockRepository.deleteById(targetUserStockId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-category")
    public ResponseEntity<?> addCategory(Principal principal, @RequestBody CategoryCreationDTO categoryCreationDTO) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserStock targetUserStock = userStockRepository.findById(categoryCreationDTO.userStockId()).orElse(null);
        if (targetUserStock == null) {
            return ResponseEntity.badRequest().build();
        }
        //permission check
        if (!Objects.equals(targetUserStock.getUser().getId(), actingUser.getId())) {
            return ResponseEntity.badRequest().build();
        }

        UserCategory userCategory = new UserCategory();
        userCategory.setUserStock(targetUserStock);
        userCategory.setName(categoryCreationDTO.categoryName());
        userCategoryRepository.save(userCategory);

        return ResponseEntity.ok().body(userCategory.getName());
    }

    @DeleteMapping("/delete-category")
    public ResponseEntity<?> deleteCategory(Principal principal, @RequestParam Integer targetCategoryId) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserCategory targetCategory = userCategoryRepository.findById(targetCategoryId).orElse(null);
        if (targetCategory == null) {
            return ResponseEntity.badRequest().build();
        }
        //permission check
        if (!Objects.equals(targetCategory.getUserStock().getUser().getId(), actingUser.getId())) {
            return ResponseEntity.badRequest().build();
        }

        userCategoryRepository.deleteById(targetCategoryId);

        return ResponseEntity.ok().build();
    }

}
