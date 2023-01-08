package com.zephsie.gateway.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomResponseSender {
    @Value("${custom-response-sender.content.type}")
    private String contentType;

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomResponseSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public Mono<Void> send(ServerWebExchange exchange, Object object, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.valueOf(contentType));

        return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsString(object).getBytes())));
    }
}
