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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * This class is responsible for reading and parsing Excel (.xlsx) files
 * using Apache POI library.
 *
 * It converts Excel rows into EmployeeImportRecord objects.
 */
@Slf4j
@Component
public class ApachePoiExcelParser implements ExcelParser {

    /**
     * Main method which gets called to parse the uploaded Excel file.
     */
    @Override
    public List<EmployeeImportRecord> parseFile(MultipartFile file) throws IOException {

        // Step 1: Validate file (not null, not empty, correct format)
        validateFile(file);

        // Step 2: Open Excel file using Apache POI
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Step 3: Read the first sheet of Excel
            Sheet sheet = workbook.getSheetAt(0);

            // Step 4: Read header row (first row)
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new InvalidFileFormatException("Header row is missing from the Excel file.");
            }

            // Step 5: Create a map of Header Name -> Column Index
            Map<String, Integer> headerMap = parseHeaders(headerRow);

            // Step 6: Read data rows (skip header row)
            List<EmployeeImportRecord> records =
                    StreamSupport.stream(sheet.spliterator(), false)
                            .skip(1) // Skip header row
                            .filter(row -> row != null && !isRowEmpty(row)) // Ignore empty rows
                            .map(row -> parseRow(row, headerMap, row.getRowNum() + 1))
                            .collect(Collectors.toList());

            log.info("Successfully parsed {} records from file: {}",
                    records.size(), file.getOriginalFilename());

            return records;
        }
    }

    /**
     * Reads header row and prepares a map of
     * headerName -> columnIndex
     */
    private Map<String, Integer> parseHeaders(Row headerRow) {
        Map<String, Integer> headerMap = new HashMap<>();

        // Loop through each cell in header row
        for (Cell cell : headerRow) {
            String headerName = getCellValueAsString(cell).trim();
            headerMap.put(headerName, cell.getColumnIndex());
        }

        // Validate mandatory headers are present
        validateMandatoryHeaders(headerMap);
        return headerMap;
    }

    /**
     * Checks whether all required headers are present in Excel.
     * If any mandatory header is missing, exception is thrown.
     */
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

        // Find missing headers
        String missingHeaders = mandatoryHeaders.stream()
                .filter(header -> !headerMap.containsKey(header))
                .collect(Collectors.joining(", "));

        if (!missingHeaders.isEmpty()) {
            throw new InvalidFileFormatException(
                    "Missing mandatory headers: " + missingHeaders
            );
        }
    }

    /**
     * Converts a single Excel row into EmployeeImportRecord object
     */
    private EmployeeImportRecord parseRow(Row row,
                                          Map<String, Integer> headerMap,
                                          int rowNumber) {

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

    /**
     * Validates uploaded file:
     * - not null
     * - not empty
     * - must be .xlsx file
     */
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("Uploaded file is empty.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new InvalidFileFormatException(
                    "Invalid file format. Only .xlsx files are supported."
            );
        }
    }

    /**
     * Gets value of a cell using header name
     */
    private String getCellValue(Row row,
                                Map<String, Integer> headerMap,
                                String headerName) {

        Integer columnIndex = headerMap.get(headerName);
        if (columnIndex == null) {
            return null;
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return (cell == null) ? null : getCellValueAsString(cell).trim();
    }

    /**
     * Converts any Excel cell value into String
     */
    private String getCellValueAsString(Cell cell) {

        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {

            case STRING:
                return cell.getStringCellValue();

            case NUMERIC:
                // Check if numeric value is a date
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue()
                            .toLocalDate()
                            .toString();
                }

                // Handle numbers like ID or phone number
                double numericValue = cell.getNumericCellValue();
                return (numericValue == (long) numericValue)
                        ? String.valueOf((long) numericValue)
                        : String.valueOf(numericValue);

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                // Currently returning formula itself
                return cell.getCellFormula();

            default:
                return "";
        }
    }

    /**
     * Checks whether an Excel row is completely empty
     */
    private boolean isRowEmpty(Row row) {

        for (Cell cell : row) {
            if (cell != null &&
                    cell.getCellType() != CellType.BLANK &&
                    !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
