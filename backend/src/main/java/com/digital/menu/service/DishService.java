package com.digital.menu.service;

import com.digital.menu.model.Dish;
import com.digital.menu.repository.DishRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public Dish addDish(String tenantId, Dish dish) {
        dish.setTenantId(tenantId);
        return dishRepository.save(dish);
    }

    public List<Dish> getAllDishes(String tenantId) {
        return dishRepository.findByTenantId(tenantId);
    }
}
