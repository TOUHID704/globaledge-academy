package com.globaledge.academy.lms.employee.imports.validator.chain;


import com.globaledge.academy.lms.employee.enums.ImportLogLevel;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Order 7: Validates optional text fields.
 *
 * Optional Text Fields:
 * - Phone Number
 * - Domain
 * - Sub Domain
 * - Office Location
 *
 * Validation Rules:
 * - Missing -> WARNING
 * - Invalid format/length -> WARNING
 *
 * Note: Date of Birth is validated in DateFormatValidator
 * Note: Enum fields are validated in EnumFieldValidator
 * Note: Manager ID is validated in ManagerReferenceValidator
 */
@Component
@Order(7)
public class OptionalFieldValidator implements ValidationChain {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    @Override
    public void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch) {
        EmployeeImportRecord record = result.getRecord();

        // Validate Phone Number
        validatePhoneNumber(result, record);

        // Validate Domain
        validateStringField(result, record.getDomain(), "Domain", 100);

        // Validate Sub Domain
        validateStringField(result, record.getSubDomain(), "Sub Domain", 100);

        // Validate Office Location
        validateStringField(result, record.getOfficeLocation(), "Office Location", 100);
    }

    /**
     * Validates phone number format.
     * Missing -> WARNING
     * Invalid format -> WARNING
     */
    private void validatePhoneNumber(EmployeeImportValidationResult result, EmployeeImportRecord record) {
        String phoneNumber = record.getPhoneNumber();

        // Check if missing
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            result.addWarning(createLog(record, "Optional field 'Phone Number' is missing or empty.", ImportLogLevel.WARNING));
            return;
        }

        // Validate format
        if (!PHONE_PATTERN.matcher(phoneNumber.trim()).matches()) {
            result.addWarning(createLog(record, "Invalid phone number format: '" + phoneNumber + "'. Expected 10-15 digits.", ImportLogLevel.WARNING));
        }
    }

    /**
     * Validates string fields for presence and length.
     * Missing -> WARNING
     * Exceeds max length -> WARNING
     */
    private void validateStringField(EmployeeImportValidationResult result, String value, String fieldName, int maxLength) {
        // Check if missing
        if (value == null || value.trim().isEmpty()) {
            result.addWarning(createLog(result.getRecord(), "Optional field '" + fieldName + "' is missing or empty.", ImportLogLevel.WARNING));
            return;
        }

        // Validate length
        if (value.length() > maxLength) {
            result.addWarning(createLog(result.getRecord(), "Field '" + fieldName + "' exceeds maximum length of " + maxLength + " characters.", ImportLogLevel.WARNING));
        }
    }
}