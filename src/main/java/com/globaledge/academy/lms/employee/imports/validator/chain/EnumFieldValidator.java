package com.globaledge.academy.lms.employee.imports.validator.chain;


import com.globaledge.academy.lms.employee.enums.*;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Order 5: Validates enum fields.
 *
 * Enum Fields (All Optional):
 * - Gender (MALE, FEMALE, OTHER)
 * - Employment Type (FULL_TIME, PART_TIME, CONTRACT, INTERN)
 * - Work Mode (ONSITE, REMOTE, HYBRID)
 * - Status (ACTIVE, INACTIVE, ON_LEAVE, TERMINATED)
 *
 * Validation Rules:
 * - Missing -> WARNING
 * - Invalid -> WARNING
 */
@Component
@Order(5)
public class EnumFieldValidator implements ValidationChain {

    @Override
    public void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch) {
        EmployeeImportRecord record = result.getRecord();

        // Validate optional enum fields
        validateEnum(result, record.getGender(), "Gender", Gender.class, false);
        validateEnum(result, record.getEmploymentType(), "Employment Type", EmploymentType.class, false);
        validateEnum(result, record.getWorkMode(), "Work Mode", WorkMode.class, false);
        validateEnum(result, record.getStatus(), "Status", EmployeeStatus.class, false);
    }

    /**
     * Validates enum fields with distinction between mandatory and optional.
     *
     * @param result Validation result container
     * @param value The field value to validate
     * @param fieldName The name of the field for logging
     * @param enumClass The enum class to validate against
     * @param isMandatory Whether the field is mandatory or optional
     */
    private <E extends Enum<E>> void validateEnum(
            EmployeeImportValidationResult result,
            String value,
            String fieldName,
            Class<E> enumClass,
            boolean isMandatory) {

        // If field is empty
        if (value == null || value.trim().isEmpty()) {
            if (isMandatory) {
                // Mandatory field missing -> ERROR
                result.addError(createLog(result.getRecord(), "Mandatory field '" + fieldName + "' is missing or empty."));
            } else {
                // Optional field missing -> WARNING
                result.addWarning(createLog(result.getRecord(), "Optional field '" + fieldName + "' is missing or empty.", ImportLogLevel.WARNING));
            }
            return;
        }

        // If field has a value, validate it
        try {
            Enum.valueOf(enumClass, value.trim().toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            if (isMandatory) {
                // Mandatory field invalid -> ERROR
                result.addError(createLog(result.getRecord(), "Invalid value '" + value + "' for mandatory field '" + fieldName + "'."));
            } else {
                // Optional field invalid -> WARNING
                result.addWarning(createLog(result.getRecord(), "Invalid value '" + value + "' for field '" + fieldName + "'. Expected values: " + getEnumValues(enumClass) + ".", ImportLogLevel.WARNING));
            }
        }
    }

    /**
     * Gets all valid enum values as a comma-separated string for error messages.
     */
    private <E extends Enum<E>> String getEnumValues(Class<E> enumClass) {
        E[] constants = enumClass.getEnumConstants();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < constants.length; i++) {
            sb.append(constants[i].name());
            if (i < constants.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}