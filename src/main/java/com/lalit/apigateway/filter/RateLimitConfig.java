////package com.lalit.apigateway.filter;
////
////import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.context.annotation.Primary;
////import reactor.core.publisher.Mono;
////
////import java.util.Optional;
////
////@Configuration
////public class RateLimitConfig {
////
////    @Primary
////    @Bean
////    public KeyResolver userKeyResolver() {
////        return exchange -> {
////            // Get the ID from headers (injected by your custom filter)
////            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
////
////            if (userId != null && !userId.isEmpty()) {
////                return Mono.just(userId);
////            }
////
////            // Fallback: If no UserID, use IP address so we still protect the API
////            return Mono.just(Optional.ofNullable(exchange.getRequest().getRemoteAddress())
////                    .map(address -> address.getAddress().getHostAddress())
////                    .orElse("anonymous"));
////        };
////    }
////
////
////    @Bean
////    public KeyResolver ipKeyResolver() {
////        return exchange -> Mono.just(
////                Optional.ofNullable(exchange.getRequest().getRemoteAddress())
////                        .map(address -> address.getAddress().getHostAddress())
////                        .orElse("unknown-ip")
////        );
////    }
////}
//
//
//package com.lalit.apigateway.filter;
//
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.util.Optional;
//
//@Configuration
//@Component
//public class RateLimitConfig {
//
//    @Primary
//    @Bean
//    public KeyResolver userKeyResolver() {
//        return exchange -> {
//            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
//            if (userId != null && !userId.isBlank()) {
//                return Mono.just("user:" + userId);
//            }
//            return Mono.just("ip:" +
//                    Optional.ofNullable(exchange.getRequest().getRemoteAddress())
//                            .map(addr -> addr.getAddress().getHostAddress())
//                            .orElse("anonymous"));
//        };
//    }
//}