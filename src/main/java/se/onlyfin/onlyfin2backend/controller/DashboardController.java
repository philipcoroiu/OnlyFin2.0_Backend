package se.onlyfin.onlyfin2backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import se.onlyfin.onlyfin2backend.DTO.incoming.CategoryCreationDTO;
import se.onlyfin.onlyfin2backend.DTO.incoming.CategoryUpdateDTO;
import se.onlyfin.onlyfin2backend.DTO.incoming.ModulePostDTO;
import se.onlyfin.onlyfin2backend.DTO.outgoing.*;
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
import java.util.stream.Collectors;

/**
 * This class is responsible for handling requests related to the dashboard.
 */
//TODO: Add module content validation?
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

    /**
     * Adds a user stock to the logged-in user's dashboard
     *
     * @param principal     The logged-in user
     * @param targetStockId The id of the stock to add
     * @return 200 OK if successful, 404 Not Found if the stock doesn't exist
     */
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

    /**
     * Deletes a user stock from the logged-in user's dashboard
     *
     * @param principal         The logged-in user
     * @param targetUserStockId The id of the user stock to delete
     * @return 200 OK if successful, 404 Not Found if the user stock doesn't exist,
     * 403 Forbidden if the user doesn't own the user stock
     */
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

    /**
     * Adds a category to the logged-in user's dashboard for the given user stock
     *
     * @param principal           The logged-in user
     * @param categoryCreationDTO The category creation DTO
     * @return 200 OK if successful, 404 Not Found if the user stock doesn't exist,
     * 403 Forbidden if the user doesn't own the user stock
     */
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

    /**
     * Updates the name of a specified category on the logged-in user's dashboard
     *
     * @param principal         The logged-in user
     * @param categoryUpdateDTO The category update DTO
     * @return 200 OK if successful, 404 Not Found if the category doesn't exist, 403 Forbidden if the user doesn't own the category
     */
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

    /**
     * Deletes a category from the logged-in user's dashboard
     *
     * @param principal        The logged-in user
     * @param targetCategoryId The id of the category to delete
     * @return 200 OK if successful, 404 Not Found if the category doesn't exist, 403 Forbidden if the user doesn't own the category
     */
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

    /**
     * Adds a module to the specified category on the logged-in user's dashboard
     *
     * @param principal     The logged-in user
     * @param modulePostDTO The module post DTO
     * @return 200 OK if successful, 404 Not Found if the category doesn't exist, 403 Forbidden if the user doesn't own the category
     */
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
        dashboardModule.setContent(modulePostDTO.content());

        dashboardModuleRepository.save(dashboardModule);

        return ResponseEntity.ok().build();
    }

    /**
     * Updates a module on the logged-in user's dashboard
     *
     * @param principal       The logged-in user
     * @param moduleId        The id of the module to update
     * @param moduleUpdateDTO The module update DTO
     * @return 200 OK if successful, 404 Not Found if the module doesn't exist, 403 Forbidden if the user doesn't own the module
     */
    @PutMapping("/update-module")
    public ResponseEntity<?> updateModule(Principal principal, @RequestParam Integer moduleId, @RequestBody ModulePostDTO moduleUpdateDTO) {
        User actingUser = userService.getUserOrException(principal.getName());

        DashboardModule targetModule = dashboardModuleRepository.findById(moduleId).orElse(null);
        if (targetModule == null) {
            return ResponseEntity.notFound().build();
        }
        //permission check
        if (!Objects.equals(actingUser.getId(), targetModule.getUserCategory().getUserStock().getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        targetModule.setHeight(moduleUpdateDTO.height());
        targetModule.setWidth(moduleUpdateDTO.width());
        targetModule.setX(moduleUpdateDTO.xAxis());
        targetModule.setY(moduleUpdateDTO.yAxis());
        targetModule.setModuleType(moduleUpdateDTO.type());
        targetModule.setContent(moduleUpdateDTO.content());
        dashboardModuleRepository.save(targetModule);

        return ResponseEntity.ok().build();
    }

    /**
     * Deletes a module from the logged-in user's dashboard
     *
     * @param principal The logged-in user
     * @param moduleId  The id of the module to delete
     * @return 200 OK if successful, 404 Not Found if the module doesn't exist, 403 Forbidden if the user doesn't own the module
     */
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

    /**
     * Fetches all stocks added by the target user
     *
     * @param targetUsername The username of the user to fetch stocks from
     * @return 200 OK if successful, 404 Not Found if the user doesn't exist
     */
    @GetMapping("/fetch-user-stocks")
    public ResponseEntity<?> fetchUserStocks(@RequestParam String targetUsername) {
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

    /**
     * Fetches all categories and modules under a target user stock
     *
     * @param userStockId The id of the user stock to fetch categories and modules from
     * @return 200 OK if successful, 404 Not Found if the user stock doesn't exist
     */
    @GetMapping("/fetch-categories-and-modules-under-user-stock")
    public ResponseEntity<UserStockTabDTO> fetchCategoriesAndModulesUnderUserStock(@RequestParam Integer userStockId) {
        UserStock userStock = userStockRepository.findById(userStockId).orElse(null);
        if (userStock == null) {
            return ResponseEntity.notFound().build();
        }

        List<UserCategoryTabDTO> categoryTabs = new ArrayList<>();

        List<UserCategory> userCategories = userCategoryRepository.findByUserStockIdIncludeModules(userStockId);
        for (UserCategory userCategory : userCategories) {
            int categoryId = userCategory.getId();

            List<ModuleDTO> categoryModules = userCategory.getModules()
                    .stream()
                    .map(module -> new ModuleDTO(
                            module.getId(),
                            categoryId,
                            module.getHeight(),
                            module.getWidth(),
                            module.getX(),
                            module.getY(),
                            module.getModuleType(),
                            module.getContent())
                    )
                    .collect(Collectors.toList());

            categoryTabs.add(new UserCategoryTabDTO(categoryId, userCategory.getName(), categoryModules));
        }

        UserStockTabDTO stockTab = new UserStockTabDTO(userStockId, categoryTabs);

        return ResponseEntity.ok().body(stockTab);
    }

}
