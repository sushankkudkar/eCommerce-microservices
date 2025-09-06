package com.ecommerce.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthFilter implements WebFilter {


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);

            // Validate the token
            boolean isValid = validateToken(jwt);
            if (!isValid) {
                log.warn("Invalid JWT token received: {}", jwt);
                // Instead of rejecting, just continue
            }
        } else {
            // No token found → log only
            log.info("No Bearer token found in request to {}", request.getURI());
        }

        // Always continue the chain
        return chain.filter(exchange);
    }

    // Dummy JWT validation logic (replace with real validation)
    private boolean validateToken(String token) {
        // TODO: Decode, verify signature, check expiry etc.
        return !token.isBlank(); // For demo purposes
    }
}
