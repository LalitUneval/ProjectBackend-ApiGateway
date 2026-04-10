package com.lalit.apigateway.filter;

import com.lalit.apigateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            //We used token in two way one by simple call http(In header) and other by websocket(Query param)
            String token = null;

            // Authorization header first
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            //If token null then may possible request came form WebSocket clients
            if (token == null) {
                token = request.getQueryParams().getFirst("token");
            }

            //If all possible cash done then token was missing
            if (token == null) {
                return onError(exchange, "Missing Authorization token", HttpStatus.UNAUTHORIZED);
            }

            //If token present then validate it
            try {
                if (!jwtUtil.validateToken(token)) {
                    return onError(exchange, "Invalid or expired JWT token", HttpStatus.UNAUTHORIZED);
                }

                // Extract all user info from token
                String userId   = jwtUtil.extractUserId(token);
                String userRole = jwtUtil.extractUserRole(token);
                String userName = jwtUtil.extractUsername(token); // email/username from subject

                // Inject into request headers — available to all downstream services
                // For WebSocket captured by HttpHandshakeInterceptor during upgrade
                // For REST read via RequestHeader in controllers
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id",        userId)
                        .header("X-User-Role",       userRole != null ? userRole : "USER")
                        .header("X-User-Name",       userName != null ? userName : "Unknown")
                        .header("X-Internal-Secret", "Lalit-Super-Secret-Key-2026")
                        .build();

                log.debug("JWT validated: userId={}, role={}, name={}", userId, userRole, userName);
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                return onError(exchange, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"error\":\"%s\",\"status\":%d}", message, status.value());
        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    public static class Config {
    }
}