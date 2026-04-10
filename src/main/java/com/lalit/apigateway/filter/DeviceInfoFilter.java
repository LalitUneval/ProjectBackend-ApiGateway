package com.lalit.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class DeviceInfoFilter implements GlobalFilter, Ordered {

    //Highest Priority because if ip not correct then no need to pass from the jwtAuthenticationFilter
    @Override
    public int getOrder() {
        return -2;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        //It will have more then one ip
        // Format: "client, proxy1, proxy2" → we want the first one
        String ip = request.getHeaders().getFirst("X-Forwarded-For");

        //try extract the really ip if have
        if (ip == null || ip.isBlank()) {
            ip = request.getHeaders().getFirst("X-Real-IP");
        }

        //If the request passed through the load-balancer then it will this ip
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddress() != null
                    ? request.getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
        }


        // X-Forwarded-For can be "1.2.3.4, 5.6.7.8" — take first (original client)
        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // Extract User-Agent
        String userAgent = request.getHeaders().getFirst("User-Agent");
        if (userAgent == null || userAgent.isBlank()) {
            userAgent = "Unknown";
        }

        log.debug("DeviceInfoFilter → path={}, ip={}, userAgent={}",
                request.getPath(), ip, userAgent);

        //  Inject as headers downstream
        // Auth-service will read these in AuthController
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Client-IP", ip)
                .header("X-Device-Agent", userAgent)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
}