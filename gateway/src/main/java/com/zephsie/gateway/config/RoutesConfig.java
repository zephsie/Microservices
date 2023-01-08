package com.zephsie.gateway.config;

import com.zephsie.gateway.filters.BlockPrivateServerFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    private final BlockPrivateServerFilter blockPrivateServerFilter;

    @Autowired
    public RoutesConfig(BlockPrivateServerFilter blockPrivateServerFilter) {
        this.blockPrivateServerFilter = blockPrivateServerFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/audit-server/**")
                        .filters(f -> f.filter(blockPrivateServerFilter))
                        .uri("lb://audit-server"))
                .build();
    }
}