package com.globaledge.academy.lms.employee.imports.validator;



import com.globaledge.academy.lms.employee.enums.ImportLogLevel;
import com.globaledge.academy.lms.employee.imports.dto.EmployeeImportLogEntryDTO;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;

import java.util.Set;

/**
 * The base interface for a single link in the validation chain.
 * Each validator performs a specific validation check on employee import records.
 *
 * Validators are executed in order based on their @Order annotation:
 * 1. StrategyCodeValidator - Validates Import Type
 * 2. MandatoryFieldValidator - Validates mandatory fields presence
 * 3. EmailFormatValidator - Validates email format
 * 4. DateFormatValidator - Validates date formats and business rules
 * 5. EnumFieldValidator - Validates enum field values
 * 6. ManagerReferenceValidator - Validates manager references
 * 7. OptionalFieldValidator - Validates optional field formats
 *
 * Validation Logic:
 * - Mandatory fields missing -> ERROR
 * - Mandatory fields invalid -> ERROR
 * - Optional fields missing -> WARNING
 * - Optional fields invalid -> WARNING
 */
public interface ValidationChain {

    /**
     * Performs a specific validation check on the given record.
     *
     * @param result The container holding the record and its validation status.
     * @param employeeIdsInBatch A set of all employee IDs from the current import file for cross-referencing.
     */
    void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch);

    /**
     * Creates a log entry with a custom log level.
     *
     * @param rec The employee import record being validated.
     * @param msg The validation message.
     * @param lvl The log level (ERROR or WARNING).
     * @return EmployeeImportLogEntryDTO with the specified details.
     */
    default EmployeeImportLogEntryDTO createLog(EmployeeImportRecord rec, String msg, ImportLogLevel lvl) {
        return EmployeeImportLogEntryDTO.builder()
                .level(lvl)
                .identifier("Row " + rec.getRowNumber())
                .message(msg)
                .build();
    }

    /**
     * Creates an error log entry (default log level is ERROR).
     *
     * @param rec The employee import record being validated.
     * @param msg The validation error message.
     * @return EmployeeImportLogEntryDTO with ERROR level.
     */
    default EmployeeImportLogEntryDTO createLog(EmployeeImportRecord rec, String msg) {
        return createLog(rec, msg, ImportLogLevel.ERROR);
    }
}