package com.globaledge.academy.lms.employee.imports.model;


import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportLogEntryDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A container object that holds a single parsed record and the results of its validation.
 * It tracks validity and collects any errors or warnings generated for that record.
 */
@Getter
@Builder
public class EmployeeImportValidationResult {

    private final EmployeeImportRecord record;

    @Builder.Default
    private boolean valid = true;

    @Builder.Default
    private final List<EmployeeImportLogEntryDTO> errors = new ArrayList<>();

    @Builder.Default
    private final List<EmployeeImportLogEntryDTO> warnings = new ArrayList<>();

    public void addError(EmployeeImportLogEntryDTO error) {
        this.errors.add(error);
        this.valid = false;
    }

    public void addWarning(EmployeeImportLogEntryDTO warning) {
        this.warnings.add(warning);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !this.warnings.isEmpty();
    }
}