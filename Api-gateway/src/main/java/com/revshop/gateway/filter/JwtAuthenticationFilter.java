package com.revshop.gateway.filter;

import com.revshop.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/forgot-password",
            "/api/auth/verify-otp",
            "/api/auth/reset-password",
            "/uploads",
            "/api/internal",
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs"
    );

    private static final List<String> BUYER_PATHS = List.of(
            "/api/buyer"
    );

    private static final List<String> SELLER_PATHS = List.of(
            "/api/seller"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);
        Long userId = jwtUtil.extractUserId(token);
        String userName = jwtUtil.extractUserName(token);

        if (isBuyerPath(path) && !"BUYER".equals(role)) {
            return onError(exchange, "Access denied: BUYER role required", HttpStatus.FORBIDDEN);
        }

        if (isSellerPath(path) && !"SELLER".equals(role)) {
            return onError(exchange, "Access denied: SELLER role required", HttpStatus.FORBIDDEN);
        }

        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Email", email)
                .header("X-User-Role", role)
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Name", userName)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean isBuyerPath(String path) {
        return BUYER_PATHS.stream().anyMatch(path::contains);
    }

    private boolean isSellerPath(String path) {
        return SELLER_PATHS.stream().anyMatch(path::contains);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        String body = "{\"error\":\"" + message + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
