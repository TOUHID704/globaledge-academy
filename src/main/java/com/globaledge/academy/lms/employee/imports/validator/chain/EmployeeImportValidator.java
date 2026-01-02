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
 * MAIN VALIDATOR ORCHESTRATOR
 *
 * This class does NOT perform validation itself.
 * Instead, it:
 *  - Collects all ValidationChain implementations
 *  - Executes them one by one for each record
 *
 * Think of this class as:
 * 👉 "Validation Manager / Coordinator"
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeImportValidator {

    /**
     * List of all validation chain implementations.
     *
     * Spring automatically injects:
     *  - StrategyCodeValidator
     *  - MandatoryFieldValidator
     *  - EmailFormatValidator
     *  - DateFormatValidator
     *  - EnumFieldValidator
     *  - ManagerReferenceValidator
     *  - OptionalFieldValidator
     *
     * Order is controlled by @Order annotation on each validator.
     */
    private final List<ValidationChain> validators;

    /**
     * ENTRY POINT for validation.
     *
     * This method is called from EmployeeImportService.
     *
     * @param records
     *  - List of parsed Excel records
     *  - Each record represents ONE Excel row
     *
     * @param employeeIdsInBatch
     *  - Set of all employee IDs in the uploaded file
     *  - Used for cross-record validations (e.g., manager reference)
     *
     * @return
     *  - List of validation results
     *  - Each result contains:
     *      - original record
     *      - list of errors
     *      - list of warnings
     */
    public List<EmployeeImportValidationResult> validateRecords(
            List<EmployeeImportRecord> records,
            Set<String> employeeIdsInBatch) {

        log.info("Starting validation for {} records.", records.size());

        // STEP 1:
        // Convert each EmployeeImportRecord into a ValidationResult
        List<EmployeeImportValidationResult> results = records.stream()

                .map(record -> {

                    // STEP 2:
                    // Create validation result object for THIS record
                    EmployeeImportValidationResult result =
                            EmployeeImportValidationResult.builder()
                                    .record(record)
                                    .build();

                    // STEP 3:
                    // Execute ALL validators one by one
                    //
                    // Flow:
                    //  Validator 1 → Validator 2 → Validator 3 → ...
                    //
                    // Each validator:
                    //  - reads record data
                    //  - adds errors/warnings to result if needed
                    validators.forEach(validator ->
                            validator.validate(result, employeeIdsInBatch)
                    );

                    // STEP 4:
                    // Return result after all validations are done
                    return result;
                })

                // STEP 5:
                // Collect all validation results into a list
                .collect(Collectors.toList());

        // STEP 6:
        // Count how many records have errors
        long errorCount = results.stream()
                .filter(EmployeeImportValidationResult::hasErrors)
                .count();

        // STEP 7:
        // Count how many records have warnings
        long warningCount = results.stream()
                .filter(EmployeeImportValidationResult::hasWarnings)
                .count();

        log.info("Validation complete. Found {} records with errors and {} records with warnings.",
                errorCount, warningCount);

        // STEP 8:
        // Return validation results back to Service layer
        return results;
    }

    /**
     * Helper method to create an ERROR log entry.
     *
     * NOTE:
     * This method is kept only for backward compatibility.
     * New validators should use ValidationChain.createLog().
     *
     * @param record
     *  - The Excel record that caused the error
     *
     * @param message
     *  - Error message to show in import log
     *
     * @return
     *  - Log entry with ERROR level
     */
    public EmployeeImportLogEntryDTO createLog(
            EmployeeImportRecord record,
            String message) {

        return EmployeeImportLogEntryDTO.builder()

                // Log level = ERROR
                .level(ImportLogLevel.ERROR)

                // Identifier shown in log (e.g. "Row 5")
                .identifier("Row " + record.getRowNumber())

                // Actual error message
                .message(message)

                .build();
    }
}
