package com.zephsie.report.services.entity;

import com.zephsie.report.models.entity.Report;
import com.zephsie.report.services.api.IReportProvider;
import com.zephsie.report.services.api.IReportProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReportProviderFactory implements IReportProviderFactory {
    private final Map<String, IReportProvider> map;

    @Autowired
    public ReportProviderFactory(Map<String, IReportProvider> map) {
        this.map = map;
    }

    @Override
    public IReportProvider getProvider(Report report) {
        IReportProvider reportProvider = map.get(report.getReportType().toString());

        if (reportProvider == null) {
            throw new UnsupportedOperationException("Report type not supported");
        }

        return reportProvider;
    }
}