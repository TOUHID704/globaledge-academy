package com.globaledge.academy.lms.employee.imports.writer.impl;



import com.globaledge.academy.lms.employee.imports.constants.EmployeeImportExcelHeaders;
import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportLogEntryDTO;
import com.globaledge.academy.lms.employee.imports.writer.ImportLogWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * An implementation of ImportLogWriter that creates log files in Excel (.xlsx) format
 * using the Apache POI library.
 */
@Slf4j
@Component
public class ExcelImportLogWriter implements ImportLogWriter {

    @Value("${employee.import.log.directory:./import-logs}")
    private String logDirectory;

    @Override
    public String writeLogFile(List<EmployeeImportLogEntryDTO> logEntries, String fileName) throws IOException {
        // 1. Ensure the log directory exists
        Path logDirPath = Paths.get(logDirectory);
        if (!Files.exists(logDirPath)) {
            Files.createDirectories(logDirPath);
        }

        // 2. Define the full path for the new log file
        String logFilePath = logDirPath.resolve(fileName).toString();

        // 3. Use try-with-resources to automatically close the workbook and file stream
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(logFilePath)) {
            Sheet sheet = workbook.createSheet("Import Log");
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 4. Create the header row
            Row headerRow = sheet.createRow(0);
            createHeaderCell(headerRow, 0, EmployeeImportExcelHeaders.LOG_LEVEL, headerStyle);
            createHeaderCell(headerRow, 1, EmployeeImportExcelHeaders.LOG_IDENTIFIER, headerStyle);
            createHeaderCell(headerRow, 2, EmployeeImportExcelHeaders.LOG_MESSAGE, headerStyle);

            // 5. Populate the sheet with log entry data
            int rowNum = 1;
            for (EmployeeImportLogEntryDTO entry : logEntries) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getLevel().toString());
                row.createCell(1).setCellValue(entry.getIdentifier());
                row.createCell(2).setCellValue(entry.getMessage());
            }

            // 6. Auto-size columns for better readability
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // 7. Write the output to the file
            workbook.write(fileOut);
            log.info("Successfully wrote {} log entries to: {}", logEntries.size(), logFilePath);
        }
        return logFilePath;
    }

    /**
     * Creates a bolded, gray-background cell style for the header row.
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * Helper method to create and style a single header cell.
     */
    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}