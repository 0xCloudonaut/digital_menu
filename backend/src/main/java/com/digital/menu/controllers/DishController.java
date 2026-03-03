package com.digital.menu.controllers;

import com.digital.menu.dto.MediaUploadResponse;
import com.digital.menu.model.Dish;
import com.digital.menu.security.TenantContext;
import com.digital.menu.service.DishService;
import com.digital.menu.service.S3MediaStorageService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/dishes")
public class DishController {
    private final DishService dishService;
    private final S3MediaStorageService s3MediaStorageService;

    public DishController(DishService dishService, S3MediaStorageService s3MediaStorageService) {
        this.dishService = dishService;
        this.s3MediaStorageService = s3MediaStorageService;
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

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    public MediaUploadResponse uploadMedia(
        @RequestParam("mediaType") String mediaType,
        @RequestPart("file") MultipartFile file
    ) {
        return s3MediaStorageService.upload(TenantContext.getTenantIdOrThrow(), mediaType, file);
    }
}
