package com.zephsie.report.services.api;

import com.zephsie.report.models.entity.Report;

public interface IReportProviderFactory {
    IReportProvider getProvider(Report report);
}
