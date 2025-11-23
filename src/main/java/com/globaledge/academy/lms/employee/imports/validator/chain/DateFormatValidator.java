package com.globaledge.academy.lms.employee.imports.validator.chain;


import com.globaledge.academy.lms.employee.enums.ImportLogLevel;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Order 4: Validates date fields and applies business rules.
 *
 * Date Fields:
 * - Date of Joining (Mandatory)
 * - Date of Birth (Optional)
 *
 * Validation Rules:
 * Date of Joining:
 * - Missing -> Already caught by MandatoryFieldValidator
 * - Invalid format -> ERROR
 * - Future date -> ERROR
 *
 * Date of Birth:
 * - Missing -> WARNING
 * - Invalid format -> WARNING
 * - Employee < 18 years at joining -> WARNING
 */
@Component
@Order(4)
public class DateFormatValidator implements ValidationChain {
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );

    @Override
    public void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch) {
        EmployeeImportRecord record = result.getRecord();

        // Validate Date of Joining (Mandatory)
        LocalDate joiningDate = validateDate(result, record.getDateOfJoining(), "Date of Joining", true);

        // Validate Date of Birth (Optional)
        LocalDate birthDate = validateDate(result, record.getDateOfBirth(), "Date of Birth", false);

        // Business rule: Date of Joining cannot be in the future
        if (joiningDate != null && joiningDate.isAfter(LocalDate.now())) {
            result.addError(createLog(record, "Date of Joining cannot be in the future."));
        }

        // Business rule: Employee should be at least 18 years old at joining
        if (birthDate != null && joiningDate != null && birthDate.plusYears(18).isAfter(joiningDate)) {
            result.addWarning(createLog(record, "Employee appears to be less than 18 years old at the date of joining.", ImportLogLevel.WARNING));
        }
    }

    /**
     * Validates a date field with support for multiple formats.
     *
     * @param result Validation result container
     * @param dateStr The date string to validate
     * @param fieldName The name of the field for logging
     * @param isMandatory Whether the field is mandatory
     * @return Parsed LocalDate or null if invalid/missing
     */
    private LocalDate validateDate(EmployeeImportValidationResult result, String dateStr, String fieldName, boolean isMandatory) {
        // Check if date is missing
        if (dateStr == null || dateStr.trim().isEmpty()) {
            if (isMandatory) {
                // Missing check already done by MandatoryFieldValidator, so we don't duplicate here
                return null;
            } else {
                // Optional field missing -> WARNING
                result.addWarning(createLog(result.getRecord(), "Optional field '" + fieldName + "' is missing or empty.", ImportLogLevel.WARNING));
                return null;
            }
        }

        // Try to parse the date with multiple formatters
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr.trim(), formatter);
            } catch (DateTimeParseException e) {
                // Try the next format
            }
        }

        // If no format matches
        if (isMandatory) {
            result.addError(createLog(result.getRecord(), "Invalid date format for " + fieldName + ": '" + dateStr + "'. Expected formats: yyyy-MM-dd, dd/MM/yyyy, MM/dd/yyyy, dd-MM-yyyy, yyyy/MM/dd."));
        } else {
            result.addWarning(createLog(result.getRecord(), "Invalid date format for " + fieldName + ": '" + dateStr + "'. Expected formats: yyyy-MM-dd, dd/MM/yyyy, MM/dd/yyyy, dd-MM-yyyy, yyyy/MM/dd.", ImportLogLevel.WARNING));
        }
        return null;
    }
}