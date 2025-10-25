package com.globaledge.academy.lms.employee.imports.service;


import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportResultDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for the main employee import business logic.
 */
public interface EmployeeImportService {

    /**
     * Processes an uploaded Excel file to import employee data.
     *
     * @param file The Excel file containing employee records.
     * @param importedBy The identifier of the user performing the import.
     * @return A DTO summarizing the result of the import operation.
     */
    EmployeeImportResultDTO importEmployees(MultipartFile file, String importedBy);
}