package com.globaledge.academy.lms.employee.imports.validator.chain;


import com.globaledge.academy.lms.employee.enums.ImportLogLevel;
import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportLogEntryDTO;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Main validator that orchestrates all validation chains.
 *
 * This class automatically picks up all implementations of ValidationChain
 * through Spring's dependency injection and executes them in order based
 * on their @Order annotation.
 *
 * Validation Chain Execution Order:
 * 1. StrategyCodeValidator - Import Type validation
 * 2. MandatoryFieldValidator - Mandatory fields presence
 * 3. EmailFormatValidator - Email format validation
 * 4. DateFormatValidator - Date format and business rules
 * 5. EnumFieldValidator - Enum field validation
 * 6. ManagerReferenceValidator - Manager reference validation
 * 7. OptionalFieldValidator - Optional field validation
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeImportValidator {

    private final List<ValidationChain> validators;

    /**
     * Validates a list of employee import records.
     *
     * @param records List of records to validate
     * @param employeeIdsInBatch Set of all employee IDs in the current batch for cross-referencing
     * @return List of validation results with errors and warnings
     */
    public List<EmployeeImportValidationResult> validateRecords(List<EmployeeImportRecord> records, Set<String> employeeIdsInBatch) {
        log.info("Starting validation for {} records.", records.size());

        List<EmployeeImportValidationResult> results = records.stream().map(record -> {
            EmployeeImportValidationResult result = EmployeeImportValidationResult.builder()
                    .record(record)
                    .build();

            // Execute all validators in order
            validators.forEach(validator -> validator.validate(result, employeeIdsInBatch));

            return result;
        }).collect(Collectors.toList());

        long errorCount = results.stream().filter(EmployeeImportValidationResult::hasErrors).count();
        long warningCount = results.stream().filter(EmployeeImportValidationResult::hasWarnings).count();

        log.info("Validation complete. Found {} records with errors and {} records with warnings.", errorCount, warningCount);

        return results;
    }

    /**
     * Creates a log entry with ERROR level.
     * This method is kept for backward compatibility.
     *
     * @param record The employee import record
     * @param message The error message
     * @return EmployeeImportLogEntryDTO with ERROR level
     * @deprecated Use ValidationChain.createLog() instead
     */
    public EmployeeImportLogEntryDTO createLog(EmployeeImportRecord record, String message) {
        return EmployeeImportLogEntryDTO.builder()
                .level(ImportLogLevel.ERROR)
                .identifier("Row " + record.getRowNumber())
                .message(message)
                .build();
    }
}