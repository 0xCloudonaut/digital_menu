package com.digital.menu.repository;

import com.digital.menu.model.RestaurantOrder;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RestaurantOrderRepository extends MongoRepository<RestaurantOrder, String> {
    List<RestaurantOrder> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    List<RestaurantOrder> findByTenantIdAndTableNumberOrderByCreatedAtDesc(String tenantId, Integer tableNumber);
    List<RestaurantOrder> findByTenantIdAndStatusInOrderByCreatedAtAsc(String tenantId, List<String> statuses);
    List<RestaurantOrder> findByTenantIdAndCreatedAtBetweenOrderByCreatedAtAsc(
        String tenantId,
        java.time.Instant from,
        java.time.Instant to
    );
    java.util.Optional<RestaurantOrder> findFirstByTenantIdAndTableNumberAndIdempotencyKeyOrderByCreatedAtDesc(
        String tenantId,
        Integer tableNumber,
        String idempotencyKey
    );
}
