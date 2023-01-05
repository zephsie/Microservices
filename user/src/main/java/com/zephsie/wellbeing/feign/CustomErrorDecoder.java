package com.zephsie.wellbeing.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zephsie.wellbeing.utils.exceptions.CustomException;
import com.zephsie.wellbeing.utils.exceptions.ServerException;
import com.zephsie.wellbeing.utils.responses.entity.SingleErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        Response.Body responseBody = response.body();
        HttpStatus responseStatus = HttpStatus.valueOf(response.status());

        if (responseStatus.is5xxServerError()) {
            return new ServerException();
        } else if (responseStatus.is4xxClientError()) {
            try {
                SingleErrorResponse errorResponse = objectMapper.readValue(responseBody.asInputStream(), SingleErrorResponse.class);
                return new CustomException(responseStatus.value(), errorResponse.getMessage());
            } catch (Exception e) {
                return new ServerException();
            }
        }

        return new ServerException();
    }
}