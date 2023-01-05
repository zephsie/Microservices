package com.zephsie.report.queue;

import com.zephsie.report.models.entity.Report;
import com.zephsie.report.models.entity.Status;
import com.zephsie.report.services.api.IReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReportProducer {

    private final RabbitTemplate rabbitTemplate;

    private final TopicExchange exchange;

    private final Binding binding;

    private final IReportService reportService;

    @Autowired
    public ReportProducer(RabbitTemplate rabbitTemplate, TopicExchange exchange, Binding binding, IReportService reportService) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.binding = binding;
        this.reportService = reportService;
    }

    public void produce(Report report) {
        log.info("UUID to send: " + report.getId());

        try {
            rabbitTemplate.convertAndSend(exchange.getName(), binding.getRoutingKey(), report.getId());
        } catch (Exception e) {
            reportService.setReportStatus(report.getId(), Status.ERROR, report.getDtUpdate());
            log.error("Error while sending message to queue: " + e.getMessage());
        }
    }
}
