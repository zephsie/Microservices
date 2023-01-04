package com.zephsie.report.dtos;

import com.zephsie.report.models.entity.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private LocalDateTime dtFrom;

    private LocalDateTime dtTo;

    private ReportType reportType;
}
