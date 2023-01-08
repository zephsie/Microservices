package com.zephsie.wellbeing.utils.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomResponseSender {

    @Value("${custom-response-sender.content.type}")
    private String contentType;

    @Value("${custom-response-sender.charset}")
    private String charset;

    private final ObjectMapper objectMapper;

    @Autowired
    public CustomResponseSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void send(HttpServletResponse response, int code, Object o) throws IOException {
        response.setContentType(contentType);
        response.setCharacterEncoding(charset);
        response.setStatus(code);
        response.getWriter().write(objectMapper.writeValueAsString(o));
    }
}