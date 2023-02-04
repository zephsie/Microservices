package com.zephsie.report.services.entity;

import com.zephsie.report.dtos.JournalDTO;
import com.zephsie.report.feign.JournalService;
import com.zephsie.report.models.entity.Report;
import com.zephsie.report.services.api.IReportProvider;
import com.zephsie.report.utils.converters.LocalDateTimeToUnixTimeConverter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Collection;

@Component("JOURNAL")
@Slf4j
public class JournalReportProvider implements IReportProvider {
    private final JournalService journalService;

    private final LocalDateTimeToUnixTimeConverter localDateTimeToUnixTimeConverter;

    @Autowired
    public JournalReportProvider(JournalService journalService, LocalDateTimeToUnixTimeConverter localDateTimeToUnixTimeConverter) {
        this.journalService = journalService;
        this.localDateTimeToUnixTimeConverter = localDateTimeToUnixTimeConverter;
    }

    @SneakyThrows
    public byte[] generateReport(Report report) {
        Collection<JournalDTO> collection = journalService.read(
                localDateTimeToUnixTimeConverter.convert(report.getDtFrom()),
                localDateTimeToUnixTimeConverter.convert(report.getDtTo()),
                report.getUserId());

        //////////////////////////////////////////////////////////////////////////////////////////
        // validation
        //////////////////////////////////////////////////////////////////////////////////////////

        try (Workbook workbook = new XSSFWorkbook()) {
            //////////////////////////////////////////////////////////////////////////////////////////
            // logic

            Sheet sheet = workbook.createSheet("Journal");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Id");
            headerRow.createCell(1).setCellValue("Date");

            int rowNum = 1;
            for (JournalDTO journalDTO : collection) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(journalDTO.getId().toString());
                row.createCell(1).setCellValue(journalDTO.getDtSupply().toString());
            }

            //////////////////////////////////////////////////////////////////////////////////////////

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            workbook.write(byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        }
    }
}