//package com.lalit.apigateway.config;
//
//import com.lalit.apigateway.filter.InMemoryRateLimiterFilter;
//import com.lalit.apigateway.filter.JwtAuthenticationFilter;
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//
//
//@Configuration
//public class GatewayConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final InMemoryRateLimiterFilter rateLimiterFilter;
//
//    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter, RateLimiter<?> inMemoryRateLimiter, KeyResolver userKeyResolver, InMemoryRateLimiterFilter rateLimiterFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.rateLimiterFilter = rateLimiterFilter;
//    }
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        // Reuse one config instance across routes
//        InMemoryRateLimiterFilter.Config rlConfig = new InMemoryRateLimiterFilter.Config();
//        rlConfig.setReplenishRate(10);
//        rlConfig.setCapacity(20);
//
//
//        return builder.routes()
//
//                // ── AUTH SERVICE (public) ──────────────────────────────────
//                .route("auth-service-public", r -> r
//                        .path("/api/auth/**")
//                        .uri("lb://auth-service"))
//
//                // ── USER SERVICE ───────────────────────────────────────────
//                .route("user-service-create-profile", r -> r
//                        .path("/api/users/profile").and().method("POST")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig)))
//                                .uri("lb://user-service"))
//
//                .route("user-service-get-profile", r -> r
//                        .path("/api/users/profile/{userId}").and().method("GET")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://user-service"))
//
//                .route("user-service-update-profile", r -> r
//                        .path("/api/users/profile/{userId}").and().method("PUT")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://user-service"))
//
//                .route("user-service-by-country", r -> r
//                        .path("/api/users/by-country/{country}")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://user-service"))
//
//                .route("user-service-all", r -> r
//                        .path("/api/users/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://user-service"))
//
//                // ── JOB SERVICE ────────────────────────────────────────────
//                .route("job-service-companies", r -> r
//                        .path("/api/jobs/companies/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-search", r -> r
//                        .path("/api/jobs/search")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-jobs", r -> r
//                        .path("/api/jobs/{jobId}")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-create-job", r -> r
//                        .path("/api/jobs").and().method("POST")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-apply", r -> r
//                        .path("/api/jobs/{jobId}/apply")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-applications", r -> r
//                        .path("/api/jobs/applications/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-save-job", r -> r
//                        .path("/api/jobs/{jobId}/save")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-saved", r -> r
//                        .path("/api/jobs/saved/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                .route("job-service-all", r -> r
//                        .path("/api/jobs/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://job-service"))
//
//                // ── COMMUNITY SERVICE REST ─────────────────────────────────
//                .route("community-service-groups", r -> r
//                        .path("/api/community/groups/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://community-service"))
//
//                .route("community-service-posts", r -> r
//                        .path("/api/community/posts/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://community-service"))
//
//                .route("community-service-connections", r -> r
//                        .path("/api/community/connections/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://community-service"))
//
//                .route("community-service-chat", r -> r
//                        .path("/api/chat/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://community-service"))
//
//                // ── WEBSOCKET ──────────────────────────────────────────────
//                // .path("/ws/chat", "/ws/chat/**") matches BOTH:
//                //   - ws://host:8080/ws/chat          (exact path)
//                //   - ws://host:8080/ws/chat/anything (sub-paths)
//                // Without both patterns, /ws/chat/** misses the exact /ws/chat URL
//                // JwtAuthenticationFilter reads ?token= param and injects:
//                //   X-User-Id, X-User-Name, X-User-Role, X-Internal-Secret
//                // HttpHandshakeInterceptor in community-service reads them from headers
//                .route("community-service-websocket", r -> r
//                        .path("/ws/chat", "/ws/chat/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb:ws://community-service"))
//
//
//                .route("community-service-websocket", r -> r
//                        .path("/ws/chat-sockjs/info/**", "/ws/chat-sockjs/info")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb:ws://community-service"))
//
//
//
//
//                .route("community-service-all", r -> r
//                        .path("/api/community/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://community-service"))
//
//                // ── RELOCATION SERVICE ─────────────────────────────────────
//                .route("relocation-service-accommodations", r -> r
//                        .path("/api/relocation/accommodations/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://relocation-service"))
//
//                .route("relocation-service-requests", r -> r
//                        .path("/api/relocation/requests/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://relocation-service"))
//
//                .route("relocation-service-providers", r -> r
//                        .path("/api/relocation/services/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://relocation-service"))
//
//                .route("relocation-service-bookings", r -> r
//                        .path("/api/relocation/bookings/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//
//                        )                        .uri("lb://relocation-service"))
//
//                .route("relocation-service-all", r -> r
//                        .path("/api/relocation/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//                        )                        .uri("lb://relocation-service"))
//
//
//                .route("ai-service",r ->r
//                        .path("/api/ai/chat/**")
//                        .filters(f -> f
//                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
//                                .filter(rateLimiterFilter.apply(rlConfig))
//                        )                        .uri("lb://ai-service"))
//
//
//
//                .build();
//    }
//}



package com.lalit.apigateway.config;

import com.lalit.apigateway.filter.InMemoryRateLimiterFilter;
import com.lalit.apigateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InMemoryRateLimiterFilter rateLimiterFilter;  // ← only this, no RateLimiter<?>

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         InMemoryRateLimiterFilter rateLimiterFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimiterFilter = rateLimiterFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        InMemoryRateLimiterFilter.Config rlConfig = new InMemoryRateLimiterFilter.Config();
        rlConfig.setReplenishRate(2);
        rlConfig.setCapacity(5);

        return builder.routes()

                // Auth Service
                .route("auth-service-public", r -> r
                        .path("/api/auth/**")
                        .uri("lb://auth-service"))

                // User Service
                .route("user-service-create-profile", r -> r
                        .path("/api/users/profile").and().method("POST")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://user-service"))

                .route("user-service-get-profile", r -> r
                        .path("/api/users/profile/{userId}").and().method("GET")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://user-service"))

                .route("user-service-update-profile", r -> r
                        .path("/api/users/profile/{userId}").and().method("PUT")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://user-service"))

                .route("user-service-by-country", r -> r
                        .path("/api/users/by-country/{country}")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://user-service"))

                .route("user-service-all", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://user-service"))

                // Job Service
                .route("job-service-companies", r -> r
                        .path("/api/jobs/companies/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-search", r -> r
                        .path("/api/jobs/search")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-jobs", r -> r
                        .path("/api/jobs/{jobId}")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-create-job", r -> r
                        .path("/api/jobs").and().method("POST")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-apply", r -> r
                        .path("/api/jobs/{jobId}/apply")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-applications", r -> r
                        .path("/api/jobs/applications/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-save-job", r -> r
                        .path("/api/jobs/{jobId}/save")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-saved", r -> r
                        .path("/api/jobs/saved/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                .route("job-service-all", r -> r
                        .path("/api/jobs/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://job-service"))

                // Community Service
                .route("community-service-groups", r -> r
                        .path("/api/community/groups/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://community-service"))

                .route("community-service-posts", r -> r
                        .path("/api/community/posts/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://community-service"))

                .route("community-service-connections", r -> r
                        .path("/api/community/connections/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://community-service"))

                .route("community-service-chat", r -> r
                        .path("/api/chat/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://community-service"))

                .route("community-service-websocket", r -> r
                        .path("/ws/chat", "/ws/chat/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb:ws://community-service"))

                .route("community-service-all", r -> r
                        .path("/api/community/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://community-service"))

                // Relocation Service
                .route("relocation-service-accommodations", r -> r
                        .path("/api/relocation/accommodations/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://relocation-service"))

                .route("relocation-service-requests", r -> r
                        .path("/api/relocation/requests/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://relocation-service"))

                .route("relocation-service-providers", r -> r
                        .path("/api/relocation/services/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://relocation-service"))

                .route("relocation-service-bookings", r -> r
                        .path("/api/relocation/bookings/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://relocation-service"))

                .route("relocation-service-all", r -> r
                        .path("/api/relocation/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://relocation-service"))


                // AI - Service
                .route("ai-service-all", r -> r
                        .path("/api/ai/chat")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config()))
                                .filter(rateLimiterFilter.apply(rlConfig)))
                        .uri("lb://ai-service")
                )

                .build();
    }
}