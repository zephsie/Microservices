package com.zephsie.report.queue;

import com.zephsie.report.models.entity.Report;
import com.zephsie.report.models.entity.Status;
import com.zephsie.report.services.api.IReportProviderFactory;
import com.zephsie.report.services.api.IReportService;
import com.zephsie.report.utils.exceptions.NotFoundException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class ReportConsumer {

    private final IReportService reportService;

    private final MinioClient minioClient;

    private final IReportProviderFactory reportProviderFactory;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Autowired
    public ReportConsumer(IReportService reportService,
                          @Qualifier("minioReportClient") MinioClient minioClient,
                          IReportProviderFactory reportProviderFactory) {

        this.reportService = reportService;
        this.minioClient = minioClient;
        this.reportProviderFactory = reportProviderFactory;
    }

    @RabbitListener(queues = "${rabbitmq.queue}", concurrency = "${rabbitmq.concurrency}")
    public void consume(UUID id) {
        try {
            log.info("Consuming report with id: {}", id);

            Report report = reportService.read(id).orElseThrow(() -> new NotFoundException("Report not found"));

            try {
                InputStream inputStream = new ByteArrayInputStream(reportProviderFactory.getProvider(report).generateReport(report));

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