package com.zephsie.gateway.filters;

import com.zephsie.gateway.dto.SingleErrorResponse;
import com.zephsie.gateway.dto.TokenDTO;
import com.zephsie.gateway.dto.UserIdDTO;
import com.zephsie.gateway.exceptions.InvalidCredentialException;
import com.zephsie.gateway.exceptions.ServerException;
import com.zephsie.gateway.http.CustomResponseSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter {

    private final WebClient.Builder webClientBuilder;

    @Value("${paths.auth}")
    private String authPath;

    @Value("${paths.allowed}")
    private List<String> allowedPaths;

    private final CustomResponseSender customResponseSender;

    @Autowired
    public AuthFilter(WebClient.Builder webClientBuilder, CustomResponseSender customResponseSender) {
        this.webClientBuilder = webClientBuilder;
        this.customResponseSender = customResponseSender;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();

        log.info("URI: {}", uri);

        for (String allowedPath : allowedPaths) {
            if (uri.getPath().startsWith(allowedPath)) {
                return chain.filter(exchange);
            }
        }

        if (!exchange.getRequest().getHeaders().containsKey("Authorization")) {
            return customResponseSender.send(exchange, new SingleErrorResponse("error", "Authorization header is missing"), HttpStatus.UNAUTHORIZED);
        }

        List<String> authHeader = exchange.getRequest().getHeaders().get("Authorization");

        if (authHeader == null || authHeader.isEmpty()) {
            return customResponseSender.send(exchange, new SingleErrorResponse("error", "Authorization header is missing"), HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.get(0);

        String[] tokenParts = token.split(" ");

        if (tokenParts.length != 2 || !tokenParts[0].equals("Bearer")) {
            return customResponseSender.send(exchange, new SingleErrorResponse("error", "Authorization header is invalid"), HttpStatus.UNAUTHORIZED);
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
                        (response) -> Mono.error(new ServerException()))
                .bodyToMono(UserIdDTO.class)
                .flatMap((dto) -> {
                    exchange.getRequest().mutate().header("USER_ID", dto.getId().toString()).build();
                    exchange.getRequest().mutate().headers((headers) -> headers.remove("Authorization")).build();

                    log.info("USER_ID: {}", dto.getId());

                    return chain.filter(exchange);
                })
                .onErrorResume(InvalidCredentialException.class, (e) -> customResponseSender.send(exchange, new SingleErrorResponse("error", e.getMessage()), HttpStatus.UNAUTHORIZED))
                .onErrorResume(Exception.class, (e) -> customResponseSender.send(exchange, new SingleErrorResponse("error", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR));
    }
}