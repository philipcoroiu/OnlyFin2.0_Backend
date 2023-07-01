package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.incoming.CategoryCreationDTO;
import se.onlyfin.onlyfin2backend.DTO.incoming.CategoryUpdateDTO;
import se.onlyfin.onlyfin2backend.DTO.incoming.ModulePostDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.ModuleDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.UserCategoryTabDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.UserStockDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.UserStockTabDTO;
import se.onlyfin.onlyfin2backend.model.*;
import se.onlyfin.onlyfin2backend.repository.DashboardModuleRepository;
import se.onlyfin.onlyfin2backend.repository.StockRepository;
import se.onlyfin.onlyfin2backend.repository.UserCategoryRepository;
import se.onlyfin.onlyfin2backend.repository.UserStockRepository;
import se.onlyfin.onlyfin2backend.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
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
    public ResponseEntity<String> addStock(Principal principal, @RequestParam Integer targetStockId) {
        User actingUser = userService.getUserOrException(principal.getName());

        Stock targetStock = stockRepository.findById(targetStockId).orElse(null);
        if (targetStock == null) {
            return ResponseEntity.notFound().build();
        }

        UserStock userStock = new UserStock();
        userStock.setUser(actingUser);
        userStock.setStock(targetStock);
        userStockRepository.save(userStock);

        return ResponseEntity.ok().body(targetStock.getName());
    }

    @DeleteMapping("/delete-stock")
    public ResponseEntity<?> deleteStock(Principal principal, @RequestParam Integer targetUserStockId) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserStock targetUserStock = userStockRepository.findById(targetUserStockId).orElse(null);
        if (targetUserStock == null) {
            return ResponseEntity.notFound().build();
        }
        //permission check
        if (!Objects.equals(actingUser.getId(), targetUserStock.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userStockRepository.deleteById(targetUserStockId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-category")
    public ResponseEntity<String> addCategory(Principal principal, @RequestBody CategoryCreationDTO categoryCreationDTO) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserStock targetUserStock = userStockRepository.findById(categoryCreationDTO.userStockId()).orElse(null);
        if (targetUserStock == null) {
            return ResponseEntity.notFound().build();
        }
        //permission check
        if (!Objects.equals(targetUserStock.getUser().getId(), actingUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserCategory userCategory = new UserCategory();
        userCategory.setUserStock(targetUserStock);
        userCategory.setName(Objects.requireNonNullElse(categoryCreationDTO.categoryName(), "unnamed"));
        userCategoryRepository.save(userCategory);

        return ResponseEntity.ok().body(userCategory.getName());
    }

    @PutMapping("/update-category")
    public ResponseEntity<String> updateCategoryName(Principal principal, @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserCategory targetCategory = userCategoryRepository.findById(categoryUpdateDTO.targetCategoryId()).orElse(null);
        if (targetCategory == null) {
            return ResponseEntity.notFound().build();
        }
        //permission check
        if (!Objects.equals(targetCategory.getUserStock().getUser().getId(), actingUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        targetCategory.setName(categoryUpdateDTO.newCategoryName());
        userCategoryRepository.save(targetCategory);

        return ResponseEntity.ok().body(categoryUpdateDTO.newCategoryName());
    }

    @DeleteMapping("/delete-category")
    public ResponseEntity<?> deleteCategory(Principal principal, @RequestParam Integer targetCategoryId) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserCategory targetCategory = userCategoryRepository.findById(targetCategoryId).orElse(null);
        if (targetCategory == null) {
            return ResponseEntity.notFound().build();
        }
        //permission check
        if (!Objects.equals(targetCategory.getUserStock().getUser().getId(), actingUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userCategoryRepository.deleteById(targetCategoryId);

        return ResponseEntity.ok().build();
    }

    //TODO: Add validation of contents of post
    @PostMapping("/add-module")
    public ResponseEntity<?> addModule(Principal principal, @RequestBody ModulePostDTO modulePostDTO) {
        User actingUser = userService.getUserOrException(principal.getName());

        UserCategory userCategory = userCategoryRepository.findById(modulePostDTO.targetCategoryId()).orElse(null);
        if (userCategory == null) {
            return ResponseEntity.notFound().build();

        }
        //permission check
        if (!Objects.equals(userCategory.getUserStock().getUser().getId(), actingUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DashboardModule dashboardModule = new DashboardModule();
        dashboardModule.setUserCategory(userCategory);
        dashboardModule.setHeight(modulePostDTO.height());
        dashboardModule.setWidth(modulePostDTO.width());
        dashboardModule.setX(modulePostDTO.xAxis());
        dashboardModule.setY(modulePostDTO.yAxis());
        dashboardModule.setModuleType(modulePostDTO.type());
        dashboardModule.setContent(modulePostDTO.content().asText());

        dashboardModuleRepository.save(dashboardModule);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-module")
    public ResponseEntity<?> deleteModule(Principal principal, @RequestParam Integer moduleId) {
        User actingUser = userService.getUserOrException(principal.getName());

        DashboardModule dashboardModule = dashboardModuleRepository.findById(moduleId).orElse(null);
        if (dashboardModule == null) {
            return ResponseEntity.notFound().build();
        }
        //permission check
        if (!Objects.equals(actingUser.getId(), dashboardModule.getUserCategory().getUserStock().getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        dashboardModuleRepository.delete(dashboardModule);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/fetch-user-stocks")
    public ResponseEntity<?> fetchStocks(@RequestParam String targetUsername) {
        User targetUser = userService.getUserOrNull(targetUsername);
        if (targetUser == null) {
            return ResponseEntity.notFound().build();
        }

        List<UserStock> userStocks = userStockRepository.findByUserId(targetUser.getId());

        List<UserStockDTO> userStockDTOS = new ArrayList<>();
        for (UserStock userStock : userStocks) {
            userStockDTOS.add(new UserStockDTO(userStock.getId(), userStock.getStock()));
        }

        return ResponseEntity.ok().body(userStockDTOS);
    }

    //TODO: Make this less shit
    @GetMapping("/fetch-categories-and-modules-under-user-stock")
    public ResponseEntity<UserStockTabDTO> fetchCategoriesAndModulesUnderUserStock(@RequestParam Integer userStockId) {
        UserStock userStock = userStockRepository.findById(userStockId).orElse(null);
        if (userStock == null) {
            return ResponseEntity.notFound().build();
        }

        List<UserCategory> userCategories = userCategoryRepository.findByUserStockId(userStockId);
        List<UserCategoryTabDTO> categoryTabs = new ArrayList<>();
        for (UserCategory currentUserCategory : userCategories) {
            Integer currentUserCategoryId = currentUserCategory.getId();
            List<DashboardModule> fetchedModules = dashboardModuleRepository.findByUserCategoryId(currentUserCategoryId);
            List<ModuleDTO> categoryModules = new ArrayList<>();
            for (DashboardModule currentModule : fetchedModules) {
                categoryModules.add(new ModuleDTO(currentUserCategoryId, currentModule.getHeight(), currentModule.getWidth(), currentModule.getX(), currentModule.getY(), currentModule.getModuleType(), currentModule.getContent()));
            }

            categoryTabs.add(new UserCategoryTabDTO(currentUserCategoryId, categoryModules));
        }

        UserStockTabDTO userStockTabDTO = new UserStockTabDTO(userStockId, categoryTabs);

        return ResponseEntity.ok().body(userStockTabDTO);
    }

}
