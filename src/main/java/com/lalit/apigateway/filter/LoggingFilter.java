package com.lalit.apigateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Log incoming request
        logger.info("Request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());

        // Continue with the filter chain
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Log response status
            logger.info("Response: {} - Status: {}",
                    exchange.getRequest().getURI(),
                    exchange.getResponse().getStatusCode());
        }));
    }

    @Override
    public int getOrder() {
        return -1; // Execute before other filters
    }
}

