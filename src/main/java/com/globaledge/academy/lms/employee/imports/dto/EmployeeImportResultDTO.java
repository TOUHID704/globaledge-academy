package com.globaledge.academy.lms.employee.imports.dto;


import com.globaledge.academy.lms.employee.enums.ImportStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for the immediate response after a file upload, summarizing the import result.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeImportResultDTO {

    private Long importId;

    private String fileName;

    private Long fileSize;

    private Integer totalRecords;

    private Integer successCount;

    private Integer errorCount;

    private Integer warningCount;

    private ImportStatus status;

    private String logFileName;

    private LocalDateTime importedAt;

    private String importedBy;

    private String message;
}