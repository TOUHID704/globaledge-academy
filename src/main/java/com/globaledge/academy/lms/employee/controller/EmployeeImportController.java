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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("employees/import")
@RequiredArgsConstructor
@Tag(name = "Employee Import", description = "APIs for bulk importing employees and managing import history")
public class EmployeeImportController {

    private final EmployeeImportService employeeImportService;
    private final EmployeeImportHistoryService employeeImportHistoryService;

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @Operation(summary = "Import Employees from Excel",
            description = "Upload an .xlsx file to bulk process employee records. Each row must contain an 'Import Type' column specifying the operation (101, 102, or 103).")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeImportResultDTO> importEmployees(
            @RequestParam("file") MultipartFile file,
            @RequestParam("importedBy") String importedBy) {

        EmployeeImportResultDTO result = employeeImportService.importEmployees(file, importedBy);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @Operation(summary = "Get All Import Histories",
            description = "Retrieves a list of all past employee import jobs, sorted by most recent.")
    @GetMapping("/history")
    public ResponseEntity<List<EmployeeImportHistoryDTO>> getAllImportHistory() {
        List<EmployeeImportHistoryDTO> history = employeeImportHistoryService.getAllImportHistory();
        return ResponseEntity.ok(history);
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @Operation(summary = "Get Import History by ID",
            description = "Retrieves detailed information and statistics for a specific import job by its ID.")
    @GetMapping("/history/{id}")
    public ResponseEntity<EmployeeImportHistoryDTO> getImportHistoryById(@PathVariable Long id) {
        EmployeeImportHistoryDTO history = employeeImportHistoryService.getImportHistoryById(id);
        return ResponseEntity.ok(history);
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @Operation(summary = "Download Import Log File",
            description = "Downloads the validation log file (.xlsx) for a specific import job by its ID.")
    @GetMapping("/history/{id}/log")
    public ResponseEntity<Resource> downloadLogFile(@PathVariable Long id) {
        Resource resource = employeeImportHistoryService.downloadLogFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate() {
        Resource resource = new ClassPathResource("templates/Employee Import Template.xlsx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Employee Import Template.xlsx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}