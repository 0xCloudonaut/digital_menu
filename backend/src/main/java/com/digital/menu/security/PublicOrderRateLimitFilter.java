package com.digital.menu.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class PublicOrderRateLimitFilter extends OncePerRequestFilter {
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String path = request.getRequestURI();
        if (!"POST".equalsIgnoreCase(request.getMethod()) || !path.startsWith("/api/public/orders")) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = request.getRemoteAddr();
        Instant now = Instant.now();
        Counter counter = counters.compute(key, (k, current) -> {
            if (current == null || now.isAfter(current.windowStart.plusSeconds(60))) {
                return new Counter(now, 1);
            }
            current.count += 1;
            return current;
        });

        if (counter.count > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Too many requests. Please try again shortly.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private static final class Counter {
        private final Instant windowStart;
        private int count;

        private Counter(Instant windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
