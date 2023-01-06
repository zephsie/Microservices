package com.zephsie.report.queue;

import com.zephsie.report.models.entity.Report;
import com.zephsie.report.models.entity.ReportType;
import com.zephsie.report.models.entity.Status;
import com.zephsie.report.services.api.IReportService;
import com.zephsie.report.services.entity.JournalReportProvider;
import com.zephsie.report.utils.exceptions.NotFoundException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class ReportConsumer {

    private final IReportService reportService;

    private final JournalReportProvider journalReportProvider;

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Autowired
    public ReportConsumer(IReportService reportService, JournalReportProvider journalReportProvider,  MinioClient minioClient) {
        this.reportService = reportService;
        this.journalReportProvider = journalReportProvider;
        this.minioClient = minioClient;
    }

    @RabbitListener(queues = "${rabbitmq.queue}", concurrency = "30")
    public void consume(UUID id) {
        try {
            log.info("Consuming report with id: {}", id);

            Report report = reportService.read(id).orElseThrow(() -> new NotFoundException("Report not found"));

            byte[] reportBytes;

            try {
                if (report.getReportType() == ReportType.JOURNAL) {
                    reportBytes = journalReportProvider.generateReport(report);
                } else {
                    throw new NotFoundException("Report type not supported");
                }

                InputStream inputStream = new ByteArrayInputStream(reportBytes);

                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(report.getId().toString())
                        .stream(inputStream, inputStream.available(), -1)
                        .build());

                reportService.setReportStatus(id, Status.DONE, report.getDtUpdate());
            } catch (Exception e) {
                reportService.setReportStatus(id, Status.ERROR, report.getDtUpdate());
                throw e;
            }
        } catch (Exception e) {
            log.error("Error while consuming report", e);
        }
    }
}