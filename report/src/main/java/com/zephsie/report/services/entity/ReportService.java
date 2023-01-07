package com.zephsie.report.services.entity;

import com.zephsie.report.dtos.ReportDTO;
import com.zephsie.report.models.entity.Report;
import com.zephsie.report.models.entity.Status;
import com.zephsie.report.repositories.ReportRepository;
import com.zephsie.report.services.api.IReportService;
import com.zephsie.report.utils.exceptions.AccessDeniedException;
import com.zephsie.report.utils.exceptions.NotFoundException;
import com.zephsie.report.utils.exceptions.WrongVersionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ReportService implements IReportService {
    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    @Transactional
    public Report create(ReportDTO reportDTO, UUID userId) {
        Report report = new Report();
        report.setReportType(reportDTO.getReportType());
        report.setDtFrom(reportDTO.getDtFrom());
        report.setDtTo(reportDTO.getDtTo());
        report.setUserId(userId);
        report.setStatus(Status.PROCESSING);

        return reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Report read(UUID id, UUID userId) {
        Optional<Report> report = reportRepository.findById(id);

        if (report.isEmpty()) {
            throw new NotFoundException("Report not found");
        }

        if (!report.get().getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        return report.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Report> read(UUID id) {
        return reportRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Report> read(int page, int size, UUID userId) {
        return reportRepository.findAllByUserId(PageRequest.of(page, size), userId);
    }

    @Override
    @Transactional
    public Report setReportStatus(UUID id, Status status, LocalDateTime version) {
        Report report = reportRepository.findById(id).orElseThrow(() -> new NotFoundException("Report not found"));

        if (!report.getDtUpdate().equals(version)) {
            throw new WrongVersionException("Report version is wrong");
        }

        report.setStatus(status);

        return reportRepository.save(report);
    }
}
