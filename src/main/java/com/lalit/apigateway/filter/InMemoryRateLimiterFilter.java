package com.lalit.apigateway.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class InMemoryRateLimiterFilter extends AbstractGatewayFilterFactory<InMemoryRateLimiterFilter.Config> {

    //Used concurrentMap because it thread safe
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public InMemoryRateLimiterFilter() {
        super(Config.class);
    }

    //creation of bucket with capacity and the refill
    private Bucket createNewBucket(Config config) {
        Bandwidth limit = Bandwidth.classic(
                config.getCapacity(),
                Refill.greedy(config.getReplenishRate(), Duration.ofSeconds(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

            //Creat  the key with Priority if the user logged then used id else used ip
            String key = (userId != null && !userId.isBlank())
                    ? "user:" + userId
                    : "ip:" + (exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown");

            //used the computerIdAbsent because it help to not create too much bucket
            //it will create one bucket for one person
            Bucket bucket = buckets.computeIfAbsent(key, k -> createNewBucket(config));

            //it will return the Token if available then true then request go ahead else false not
            if (bucket.tryConsume(1)) {
                log.debug("Rate limit passed for key={}", key);
                return chain.filter(exchange);
            }

            log.warn("Rate limit exceeded for key={}", key);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            response.getHeaders().add("Content-Type", "application/json");
            response.getHeaders().add("X-RateLimit-Retry-After", "1");
            String body = "{\"error\":\"Too many requests\",\"status\":429}";
            return response.writeWith(
                    Mono.just(response.bufferFactory().wrap(body.getBytes())));
        };
    }

    public static class Config {
        private int replenishRate = 10;  // tokens added per second
        private int capacity = 20;       // max burst capacity

        public int getReplenishRate() { return replenishRate; }
        public void setReplenishRate(int replenishRate) { this.replenishRate = replenishRate; }
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
    }
}