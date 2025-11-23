package com.globaledge.academy.lms.employee.imports.dto;


import com.globaledge.academy.lms.employee.enums.ImportLogLevel;
import lombok.*;

/**
 * DTO representing a single line item (an error or warning) in an import log file.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeImportLogEntryDTO {

    private ImportLogLevel level;

    private String identifier; // e.g., "Row 5"

    private String message;
}