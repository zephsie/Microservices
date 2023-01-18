package com.zephsie.report.services.entity;

import com.zephsie.report.models.entity.Report;
import com.zephsie.report.services.api.IReportProvider;
import com.zephsie.report.services.api.IReportProviderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportProviderFactory implements IReportProviderFactory {
    private final JournalReportProvider journalReportProvider;

    @Autowired
    public ReportProviderFactory(JournalReportProvider journalReportProvider) {
        this.journalReportProvider = journalReportProvider;
    }

    @Override
    public IReportProvider getProvider(Report report) {
        return switch (report.getReportType()) {
            case JOURNAL -> journalReportProvider;
        };
    }
}
