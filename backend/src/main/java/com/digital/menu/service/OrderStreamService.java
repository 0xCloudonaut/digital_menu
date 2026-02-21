package com.digital.menu.service;

import com.digital.menu.model.RestaurantOrder;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class OrderStreamService {
    private final Map<String, Set<SseEmitter>> tenantEmitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String tenantId) {
        SseEmitter emitter = new SseEmitter(0L);
        tenantEmitters.computeIfAbsent(tenantId, key -> ConcurrentHashMap.newKeySet()).add(emitter);

        emitter.onCompletion(() -> remove(tenantId, emitter));
        emitter.onTimeout(() -> remove(tenantId, emitter));
        emitter.onError(ex -> remove(tenantId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("stream-open"));
        } catch (IOException ex) {
            remove(tenantId, emitter);
        }
        return emitter;
    }

    public void publishOrderCreated(String tenantId, RestaurantOrder order) {
        publish(tenantId, "order-created", order);
    }

    public void publishOrderUpdated(String tenantId, RestaurantOrder order) {
        publish(tenantId, "order-updated", order);
    }

    private void publish(String tenantId, String eventName, RestaurantOrder payload) {
        Set<SseEmitter> emitters = tenantEmitters.get(tenantId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        emitters.removeIf(emitter -> {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(payload));
                return false;
            } catch (IOException ex) {
                return true;
            }
        });
    }

    private void remove(String tenantId, SseEmitter emitter) {
        Set<SseEmitter> emitters = tenantEmitters.get(tenantId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                tenantEmitters.remove(tenantId);
            }
        }
    }
}
