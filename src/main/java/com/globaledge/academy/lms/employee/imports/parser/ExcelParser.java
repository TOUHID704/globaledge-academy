package com.globaledge.academy.lms.employee.imports.parser;


import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interface for a component that can parse an Excel file into a list of data records.
 */
public interface ExcelParser {

    /**
     * Parses a MultipartFile (Excel) into a list of intermediate EmployeeImportRecord objects.
     *
     * @param file The uploaded .xlsx file.
     * @return A list of EmployeeImportRecord, each representing a row in the Excel sheet.
     * @throws IOException if there is an error reading the file.
     */
    List<EmployeeImportRecord> parseFile(MultipartFile file) throws IOException;
}