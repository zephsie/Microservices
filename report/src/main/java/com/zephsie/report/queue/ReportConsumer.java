package com.zephsie.report.queue;

import com.zephsie.report.models.entity.Report;
import com.zephsie.report.models.entity.ReportContent;
import com.zephsie.report.models.entity.ReportType;
import com.zephsie.report.models.entity.Status;
import com.zephsie.report.services.api.IReportService;
import com.zephsie.report.services.entity.JournalReportProvider;
import com.zephsie.report.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ReportConsumer {

    private final IReportService reportService;

    private final JournalReportProvider journalReportProvider;

    @Autowired
    public ReportConsumer(IReportService reportService, JournalReportProvider journalReportProvider) {
        this.reportService = reportService;
        this.journalReportProvider = journalReportProvider;
    }

    @RabbitListener(queues = "${rabbitmq.queue}", concurrency = "30")
    public void consume(UUID id) {
        try {
            log.info("Consuming report with id: {}", id);

            Report report = reportService.read(id).orElseThrow(() -> new NotFoundException("Report not found"));

            try {
                ReportContent reportContent;

                if (report.getReportType() == ReportType.JOURNAL) {
                    reportContent = journalReportProvider.generateReport(report);
                } else {
                    throw new NotFoundException("Report type not supported");
                }

                reportService.saveReportContent(id, reportContent, report.getDtUpdate());
            } catch (Exception e) {
                reportService.setReportStatus(id, Status.ERROR, report.getDtUpdate());
                throw e;
            }
        } catch (Exception e) {
            log.error("Error while consuming report", e);
        }
    }
}