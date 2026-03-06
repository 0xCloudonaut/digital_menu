package com.digital.menu.controllers;

import com.digital.menu.model.Dish;
import com.digital.menu.security.TenantContext;
import com.digital.menu.service.DishService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dishes")
public class DishController {
    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public List<Dish> getAllDishesForAdmin() {
        return dishService.getAllDishes(TenantContext.getTenantIdOrThrow());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public Dish addDishForAdmin(@RequestBody Dish dish) {
        return dishService.addDish(TenantContext.getTenantIdOrThrow(), dish);
    }
}
