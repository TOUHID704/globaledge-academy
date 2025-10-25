package com.globaledge.academy.lms.employee.imports.parser.impl;


import com.globaledge.academy.lms.employee.exception.InvalidFileFormatException;
import com.globaledge.academy.lms.employee.imports.constants.EmployeeImportExcelHeaders;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.parser.ExcelParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An implementation of the ExcelParser interface using the Apache POI library.
 * It is responsible for reading .xlsx files.
 */
@Slf4j
@Component
public class ApachePoiExcelParser implements ExcelParser {

    @Override
    public List<EmployeeImportRecord> parseFile(MultipartFile file) throws IOException {
        validateFile(file);
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new InvalidFileFormatException("Header row is missing from the Excel file.");
            }

            Map<String, Integer> headerMap = parseHeaders(headerRow);

            List<EmployeeImportRecord> records = StreamSupport.stream(sheet.spliterator(), false)
                    .skip(1) // Skip the header row
                    .filter(row -> row != null && !isRowEmpty(row))
                    .map(row -> parseRow(row, headerMap, row.getRowNum() + 1))
                    .collect(Collectors.toList());

            log.info("Successfully parsed {} records from file: {}", records.size(), file.getOriginalFilename());
            return records;
        }
    }

    private Map<String, Integer> parseHeaders(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            headerMap.put(getCellValueAsString(cell).trim(), cell.getColumnIndex());
        }
        validateMandatoryHeaders(headerMap);
        return headerMap;
    }

    private void validateMandatoryHeaders(Map<String, Integer> headerMap) {
        List<String> mandatoryHeaders = Arrays.asList(
                EmployeeImportExcelHeaders.IMPORT_TYPE,
                EmployeeImportExcelHeaders.EMPLOYEE_ID,
                EmployeeImportExcelHeaders.FIRST_NAME,
                EmployeeImportExcelHeaders.LAST_NAME,
                EmployeeImportExcelHeaders.EMAIL,
                EmployeeImportExcelHeaders.DEPARTMENT,
                EmployeeImportExcelHeaders.DESIGNATION,
                EmployeeImportExcelHeaders.DATE_OF_JOINING
        );
        String missingHeaders = mandatoryHeaders.stream()
                .filter(header -> !headerMap.containsKey(header))
                .collect(Collectors.joining(", "));

        if (!missingHeaders.isEmpty()) {
            throw new InvalidFileFormatException("Missing mandatory headers: " + missingHeaders);
        }
    }

    private EmployeeImportRecord parseRow(Row row, Map<String, Integer> headerMap, int rowNumber) {
        return EmployeeImportRecord.builder()
                .rowNumber(rowNumber)
                .importType(getCellValue(row, headerMap, EmployeeImportExcelHeaders.IMPORT_TYPE))
                .employeeId(getCellValue(row, headerMap, EmployeeImportExcelHeaders.EMPLOYEE_ID))
                .firstName(getCellValue(row, headerMap, EmployeeImportExcelHeaders.FIRST_NAME))
                .lastName(getCellValue(row, headerMap, EmployeeImportExcelHeaders.LAST_NAME))
                .email(getCellValue(row, headerMap, EmployeeImportExcelHeaders.EMAIL))
                .department(getCellValue(row, headerMap, EmployeeImportExcelHeaders.DEPARTMENT))
                .designation(getCellValue(row, headerMap, EmployeeImportExcelHeaders.DESIGNATION))
                .dateOfJoining(getCellValue(row, headerMap, EmployeeImportExcelHeaders.DATE_OF_JOINING))
                .phoneNumber(getCellValue(row, headerMap, EmployeeImportExcelHeaders.PHONE_NUMBER))
                .dateOfBirth(getCellValue(row, headerMap, EmployeeImportExcelHeaders.DATE_OF_BIRTH))
                .gender(getCellValue(row, headerMap, EmployeeImportExcelHeaders.GENDER))
                .domain(getCellValue(row, headerMap, EmployeeImportExcelHeaders.DOMAIN))
                .subDomain(getCellValue(row, headerMap, EmployeeImportExcelHeaders.SUB_DOMAIN))
                .employmentType(getCellValue(row, headerMap, EmployeeImportExcelHeaders.EMPLOYMENT_TYPE))
                .managerId(getCellValue(row, headerMap, EmployeeImportExcelHeaders.MANAGER_ID))
                .officeLocation(getCellValue(row, headerMap, EmployeeImportExcelHeaders.OFFICE_LOCATION))
                .workMode(getCellValue(row, headerMap, EmployeeImportExcelHeaders.WORK_MODE))
                .status(getCellValue(row, headerMap, EmployeeImportExcelHeaders.STATUS))
                .build();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("Uploaded file is empty.");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new InvalidFileFormatException("Invalid file format. Only .xlsx files are supported.");
        }
    }

    private String getCellValue(Row row, Map<String, Integer> headerMap, String headerName) {
        Integer columnIndex = headerMap.get(headerName);
        if (columnIndex == null) {
            return null;
        }
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return (cell == null) ? null : getCellValueAsString(cell).trim();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double numericValue = cell.getNumericCellValue();
                return (numericValue == (long) numericValue) ? String.valueOf((long) numericValue) : String.valueOf(numericValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Handle formula evaluation if needed, for now just return the formula string
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}