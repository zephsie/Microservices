package com.zephsie.report.services.api;

import com.zephsie.report.dtos.ReportDTO;
import com.zephsie.report.models.entity.Report;
import com.zephsie.report.models.entity.Status;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface IReportService {

    Report create(ReportDTO reportDTO, UUID userId);

    Optional<Report> read(UUID id);

    Optional<Report> read(UUID id, UUID userId);

    Page<Report> read(int page, int size, UUID userId);

    Report setReportStatus(UUID id, Status status, LocalDateTime version);
}
