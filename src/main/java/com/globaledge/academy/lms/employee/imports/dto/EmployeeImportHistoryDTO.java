package com.globaledge.academy.lms.employee.imports.dto;


import com.globaledge.academy.lms.employee.enums.ImportStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for representing a single record of a past import job.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeImportHistoryDTO {

    private Long id;

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
}