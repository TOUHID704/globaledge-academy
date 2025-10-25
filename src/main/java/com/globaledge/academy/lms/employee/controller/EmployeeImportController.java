package com.globaledge.academy.lms.employee.controller;


import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportHistoryDTO;
import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportResultDTO;
import com.globaledge.academy.lms.employee.imports.service.EmployeeImportHistoryService;
import com.globaledge.academy.lms.employee.imports.service.EmployeeImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller for handling all employee import functionalities.
 * Defines endpoints for uploading files, viewing import history, and downloading logs.
 */
@RestController
@RequestMapping("employees/import")
@RequiredArgsConstructor
@Tag(name = "Employee Import", description = "APIs for bulk importing employees and managing import history")
public class EmployeeImportController {

    private final EmployeeImportService employeeImportService;
    private final EmployeeImportHistoryService employeeImportHistoryService;

    /**
     * Endpoint for uploading an Excel file to import employees.
     * @param file The .xlsx file containing employee data.
     * @param importedBy The identifier (e.g., username) of the person performing the import.
     * @return A summary of the import result.
     */
    @Operation(summary = "Import Employees from Excel",
            description = "Upload an .xlsx file to bulk process employee records. Each row must contain an 'Import Type' column specifying the operation (101, 102, or 103).")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeImportResultDTO> importEmployees(
            @RequestParam("file") MultipartFile file,
            @RequestParam("importedBy") String importedBy) {

        EmployeeImportResultDTO result = employeeImportService.importEmployees(file, importedBy);
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint to retrieve a list of all past import jobs.
     * @return A list of import history records, sorted by most recent.
     */
    @Operation(summary = "Get All Import Histories",
            description = "Retrieves a list of all past employee import jobs, sorted by most recent.")
    @GetMapping("/history")
    public ResponseEntity<List<EmployeeImportHistoryDTO>> getAllImportHistory() {
        List<EmployeeImportHistoryDTO> history = employeeImportHistoryService.getAllImportHistory();
        return ResponseEntity.ok(history);
    }

    /**
     * Endpoint to retrieve details of a specific import job by its ID.
     * @param id The unique ID of the import job.
     * @return Detailed information and statistics for the specified import job.
     */
    @Operation(summary = "Get Import History by ID",
            description = "Retrieves detailed information and statistics for a specific import job by its ID.")
    @GetMapping("/history/{id}")
    public ResponseEntity<EmployeeImportHistoryDTO> getImportHistoryById(@PathVariable Long id) {
        EmployeeImportHistoryDTO history = employeeImportHistoryService.getImportHistoryById(id);
        return ResponseEntity.ok(history);
    }

    /**
     * Endpoint to download the log file for a specific import job.
     * @param id The unique ID of the import job.
     * @return The log file as a downloadable resource.
     */
    @Operation(summary = "Download Import Log File",
            description = "Downloads the validation log file (.xlsx) for a specific import job by its ID.")
    @GetMapping("/history/{id}/log")
    public ResponseEntity<Resource> downloadLogFile(@PathVariable Long id) {
        Resource resource = employeeImportHistoryService.downloadLogFile(id);

        // Set headers to prompt the browser to download the file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() {
        Resource resource = new ClassPathResource("templates/Employee Import Template.xlsx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Employee Import Template.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}