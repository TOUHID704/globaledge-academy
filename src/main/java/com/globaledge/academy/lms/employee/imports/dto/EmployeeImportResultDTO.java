package com.globaledge.academy.lms.employee.imports.dto;

import com.globaledge.academy.lms.employee.enums.ImportStatus;
import com.globaledge.academy.lms.user.dto.BulkUserCreationSummary;
import lombok.*;

import java.time.LocalDateTime;

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

    // User creation summary
    private BulkUserCreationSummary userCreationSummary;
}

