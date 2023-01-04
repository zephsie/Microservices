package com.zephsie.report.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ReportProducer {

    private final RabbitTemplate rabbitTemplate;

    private final TopicExchange exchange;

    private final Binding binding;

    @Autowired
    public ReportProducer(RabbitTemplate rabbitTemplate, TopicExchange exchange, Binding binding) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.binding = binding;
    }

    public void produce(UUID id) {
        log.info("UUID to send: " + id);

        try {
            rabbitTemplate.convertAndSend(exchange.getName(), binding.getRoutingKey(), id);
        } catch (Exception e) {
            log.error("Error while sending message to queue: " + e.getMessage());
        }
    }
}
