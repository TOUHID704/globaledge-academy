package com.globaledge.academy.lms.employee.imports.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A context object that holds metadata about the entire import job.
 * This information is consistent for all records within a single file upload.
 */
@Getter
@Setter
@Builder
public class EmployeeImportContext {

    private String fileName;

    private Long fileSize;

    private String importedBy;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}