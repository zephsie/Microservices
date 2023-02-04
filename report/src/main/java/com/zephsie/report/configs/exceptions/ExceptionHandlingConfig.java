package com.zephsie.report.configs.exceptions;

import com.zephsie.report.utils.exceptions.AccessDeniedException;
import com.zephsie.report.utils.exceptions.IllegalParamValuesException;
import com.zephsie.report.utils.exceptions.IllegalStateException;
import com.zephsie.report.utils.exceptions.NotFoundException;
import com.zephsie.report.utils.responses.api.ErrorResponse;
import com.zephsie.report.utils.responses.entity.SingleErrorResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.function.Function;

@Configuration
public class ExceptionHandlingConfig {

    @Bean
    public Map<Class<? extends Exception>, Function<Exception, ResponseEntity<ErrorResponse>>> exceptionHandlers() {
        return Map.ofEntries(
                Map.entry(HttpMessageNotReadableException.class, e ->
                        ResponseEntity.badRequest().body(new SingleErrorResponse("error", "Invalid request"))),
                Map.entry(NotFoundException.class, e ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(IllegalParamValuesException.class, e ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(MethodArgumentTypeMismatchException.class, e ->
                        ResponseEntity.badRequest().body(new SingleErrorResponse("error", "Invalid values passed"))),
                Map.entry(HttpRequestMethodNotSupportedException.class, e ->
                        ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new SingleErrorResponse("error", "Method not allowed"))),
                Map.entry(HttpMediaTypeNotSupportedException.class, e ->
                        ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new SingleErrorResponse("error", "Unsupported media type"))),
                Map.entry(IllegalStateException.class, e ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SingleErrorResponse("error", e.getMessage()))),
                Map.entry(MissingRequestHeaderException.class, e ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SingleErrorResponse("error", "Missing request header"))),
                Map.entry(MissingServletRequestParameterException.class, e ->
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SingleErrorResponse("error", "Missing request parameter"))),
                Map.entry(AccessDeniedException.class, e ->
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(new SingleErrorResponse("error", e.getMessage())))
        );
    }

    @Bean
    public Function<Exception, ResponseEntity<ErrorResponse>> defaultHandler() {
        return e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new SingleErrorResponse("error", "Internal server error"));
    }
}