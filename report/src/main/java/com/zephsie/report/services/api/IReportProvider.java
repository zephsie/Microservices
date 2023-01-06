package com.zephsie.report.services.api;

import com.zephsie.report.models.entity.Report;

public interface IReportProvider {
    byte[] generateReport(Report report);
}
