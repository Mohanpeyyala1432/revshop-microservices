package com.revshop.gateway.config;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(0))
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        
        // Register all services with lower limits for easy testing
        registry.rateLimiter("userService", RateLimiterConfig.custom()
                .limitForPeriod(3)  // Only 3 total requests
                .limitRefreshPeriod(Duration.ofSeconds(30))  // Resets every 30 seconds
                .timeoutDuration(Duration.ofMillis(0))
                .build());
        
        registry.rateLimiter("productService", RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .timeoutDuration(Duration.ofMillis(0))
                .build());
        
        registry.rateLimiter("cartService", RateLimiterConfig.custom()
                .limitForPeriod(15)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(0))
                .build());
        
        registry.rateLimiter("orderService", RateLimiterConfig.custom()
                .limitForPeriod(10)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(0))
                .build());
        
        registry.rateLimiter("paymentService", RateLimiterConfig.custom()
                .limitForPeriod(5)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(0))
                .build());
        
        return registry;
    }
}
