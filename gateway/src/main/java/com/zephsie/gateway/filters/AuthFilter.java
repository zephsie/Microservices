package com.zephsie.gateway.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.gateway.dto.SingleErrorResponse;
import com.zephsie.gateway.dto.TokenDTO;
import com.zephsie.gateway.dto.UserIdDTO;
import com.zephsie.gateway.exceptions.InvalidCredentialException;
import com.zephsie.gateway.exceptions.ServerException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Component
@Slf4j
public class AuthFilter implements GlobalFilter {

    private final WebClient.Builder webClientBuilder;

    @Value("${paths.allowed}")
    private List<String> allowedPaths;

    @Value("${paths.forbidden}")
    private List<String> forbiddenPaths;

    @Value("${paths.auth}")
    private String authPath;

    private final ObjectMapper objectMapper;

    @Autowired
    public AuthFilter(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();

        log.info("URI: {}", uri);

        String path = uri.getPath();

        log.info("Path: {}", path);

        for (String allowedPath : allowedPaths) {
            if (path.startsWith(allowedPath)) {
                return chain.filter(exchange);
            }
        }

        for (String forbiddenPath : forbiddenPaths) {
            if (path.startsWith(forbiddenPath)) {
                return sendError(exchange, new SingleErrorResponse("Forbidden", "You are not allowed to access this resource"), HttpStatus.FORBIDDEN);
            }
        }

        if (!exchange.getRequest().getHeaders().containsKey("Authorization")) {
            return sendError(exchange, new SingleErrorResponse("error", "Authorization header is missing"), HttpStatus.UNAUTHORIZED);
        }

        List<String> authHeader = exchange.getRequest().getHeaders().get("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            return sendError(exchange, new SingleErrorResponse("error", "Authorization header is missing"), HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.get(0);

        String[] tokenParts = token.split(" ");

        if (tokenParts.length != 2 || !tokenParts[0].equals("Bearer")) {
            return sendError(exchange, new SingleErrorResponse("error", "Authorization header is invalid"), HttpStatus.UNAUTHORIZED);
        }

        String jwt = tokenParts[1];

        log.info("JWT: {}", jwt);

        return webClientBuilder.build()
                .post()
                .uri(authPath)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(new TokenDTO(jwt)), TokenDTO.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (response) -> response.bodyToMono(SingleErrorResponse.class).flatMap((error) -> Mono.error(new InvalidCredentialException(error.getMessage()))))
                .onStatus(HttpStatusCode::is5xxServerError,
                        (response) -> response.bodyToMono(SingleErrorResponse.class).flatMap((error) -> Mono.error(new ServerException(error.getMessage()))))
                .bodyToMono(UserIdDTO.class)
                .flatMap((dto) -> {
                    exchange.getRequest().mutate().header("USER_ID", dto.getId().toString()).build();
                    exchange.getRequest().mutate().headers((headers) -> headers.remove("Authorization")).build();

                    log.info("USER_ID: {}", dto.getId());

                    return chain.filter(exchange);
                })
                .onErrorResume(InvalidCredentialException.class, (e) -> sendError(exchange, new SingleErrorResponse("error", e.getMessage()), HttpStatus.UNAUTHORIZED))
                .onErrorResume(Exception.class, (e) -> sendError(exchange, new SingleErrorResponse("error", "Server error"), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @SneakyThrows
    private Mono<Void> sendError(ServerWebExchange exchange, SingleErrorResponse singleErrorResponse, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsString(singleErrorResponse).getBytes())));
    }
}