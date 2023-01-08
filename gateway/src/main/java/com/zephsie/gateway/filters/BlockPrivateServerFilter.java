package com.zephsie.gateway.filters;

import com.zephsie.gateway.dto.SingleErrorResponse;
import com.zephsie.gateway.http.CustomResponseSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BlockPrivateServerFilter implements GatewayFilter {
    private final CustomResponseSender customResponseSender;

    public BlockPrivateServerFilter(CustomResponseSender customResponseSender) {
        this.customResponseSender = customResponseSender;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return customResponseSender.send(exchange, new SingleErrorResponse("error", "Private server is not allowed"), HttpStatus.FORBIDDEN);
    }
}