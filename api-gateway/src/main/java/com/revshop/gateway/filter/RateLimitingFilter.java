package com.revshop.gateway.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitingFilter implements GlobalFilter, Ordered {

    private final RateLimiterRegistry rateLimiterRegistry;

    public RateLimitingFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String serviceName = getServiceName(path);
        
        if (serviceName == null) {
            return chain.filter(exchange);
        }

        try {
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(serviceName);
            
            // Check available permissions BEFORE acquiring
            int availableBefore = rateLimiter.getMetrics().getAvailablePermissions();
            boolean permitted = rateLimiter.acquirePermission();
            int availableAfter = rateLimiter.getMetrics().getAvailablePermissions();
            
            System.out.println("[RATE LIMIT] Service: " + serviceName + ", Before: " + availableBefore + ", After: " + availableAfter + ", Permitted: " + permitted);
            
            // If no permissions available, block the request
            if (availableBefore <= 0 || !permitted) {
                System.out.println("[RATE LIMIT] ❌ BLOCKED - Too many requests for " + serviceName);
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                exchange.getResponse().getHeaders().add("X-Rate-Limit-Retry-After-Seconds", "30");
                
                String errorMessage = "{\"error\":\"Too many requests. Please try again after 30 seconds.\",\"status\":429}";
                byte[] bytes = errorMessage.getBytes();
                
                return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(bytes))
                );
            }
            
            System.out.println("[RATE LIMIT] ✅ ALLOWED - Request permitted for " + serviceName);
            return chain.filter(exchange);
        } catch (Exception e) {
            System.out.println("[RATE LIMIT] ERROR: " + e.getMessage());
            e.printStackTrace();
            // If rate limiter fails, allow request through
            return chain.filter(exchange);
        }
    }

    private String getServiceName(String path) {
        // Skip rate limiting for image uploads
        if (path.startsWith("/uploads")) {
            return null;
        }
        
        if (path.startsWith("/api/auth") || path.startsWith("/api/users") || 
            path.startsWith("/api/sellers") || path.startsWith("/api/buyers")) {
            return "userService";
        } else if (path.startsWith("/api/products") || path.startsWith("/api/categories") || 
                   path.startsWith("/api/reviews") || path.startsWith("/api/seller/products") ||
                   path.startsWith("/api/seller/categories") || path.startsWith("/api/buyer/products") ||
                   path.startsWith("/api/buyer/reviews")) {
            return "productService";
        } else if (path.startsWith("/api/cart") || path.startsWith("/api/wishlist") ||
                   path.startsWith("/api/buyer/cart") || path.startsWith("/api/buyer/wishlist")) {
            return "cartService";
        } else if (path.startsWith("/api/orders") || path.startsWith("/api/seller/orders") ||
                   path.startsWith("/api/buyer/order")) {
            return "orderService";
        } else if (path.startsWith("/api/payments") || path.startsWith("/api/buyer/payment")) {
            return "paymentService";
        }
        return null;
    }

    @Override
    public int getOrder() {
        return -2;
    }
}
