package com.zephsie.report.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.report.dtos.ReportDTO;
import com.zephsie.report.logging.Logging;
import com.zephsie.report.models.entity.Report;
import com.zephsie.report.models.entity.ReportType;
import com.zephsie.report.models.entity.Status;
import com.zephsie.report.queue.ReportProducer;
import com.zephsie.report.services.api.IReportService;
import com.zephsie.report.utils.converters.UnixTimeToLocalDateTimeConverter;
import com.zephsie.report.utils.exceptions.IllegalParamValuesException;
import com.zephsie.report.utils.exceptions.IllegalStateException;
import com.zephsie.report.utils.views.EntityView;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/report")
@Slf4j
public class ReportController {
    private final IReportService reportService;

    private final UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter;

    private final ReportProducer reportProducer;

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Autowired
    public ReportController(IReportService reportService,
                            UnixTimeToLocalDateTimeConverter unixTimeToLocalDateTimeConverter,
                            ReportProducer reportProducer,
                            @Qualifier("minioReportClient") MinioClient minioClient) {

        this.reportService = reportService;
        this.unixTimeToLocalDateTimeConverter = unixTimeToLocalDateTimeConverter;
        this.reportProducer = reportProducer;
        this.minioClient = minioClient;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @JsonView(EntityView.System.class)
    public ResponseEntity<Report> read(@PathVariable("id") UUID id,
                                       @RequestHeader("USER_ID") UUID userId) {

        return ResponseEntity.ok(reportService.read(id, userId));
    }

    @GetMapping(value = "/{id}/content", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> readContent(@PathVariable("id") UUID id,
                                              @RequestHeader("USER_ID") UUID userId) {

        Report report = reportService.read(id, userId);

        if (report.getStatus() != Status.DONE) {
            throw new IllegalStateException("Report is not ready");
        }

        try {
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + report.getId())
                    .body((minioClient.getObject(GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(report.getId().toString())
                            .build()))
                            .readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/{type}", produces = "application/json")
    @JsonView(EntityView.System.class)
    @Logging(type = "REPORT", description = "Create report", userIdPosition = 3)
    public ResponseEntity<Report> create(@PathVariable("type") ReportType type,
                                         @RequestParam(value = "from") long from,
                                         @RequestParam(value = "to") long to,
                                         @RequestHeader("USER_ID") UUID userId) {

        if (from > to) {
            throw new IllegalParamValuesException("From date must be less than to date");
        }

        Report report = reportService.create(
                new ReportDTO(
                        unixTimeToLocalDateTimeConverter.convert(from),
                        unixTimeToLocalDateTimeConverter.convert(to),
                        type),
                userId);

        log.info("Report created: {}", report.getId());

        reportProducer.produce(report);

        return ResponseEntity.ok(report);
    }

    @GetMapping(produces = "application/json")
    @JsonView(EntityView.System.class)
    public ResponseEntity<Page<Report>> read(@RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size,
                                             @RequestHeader("USER_ID") UUID userId) {

        if (page < 0 || size <= 0) {
            throw new IllegalParamValuesException("Pagination values are not correct");
        }

        return ResponseEntity.ok(reportService.read(page, size, userId));
    }
}