package com.globaledge.academy.lms.employee.imports.validator.chain;


import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Order 3: Validates email format.
 *
 * Validation Rules:
 * - Invalid format -> ERROR (Email is a mandatory field)
 * - Missing -> Already caught by MandatoryFieldValidator
 */
@Component
@Order(3)
public class EmailFormatValidator implements ValidationChain {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch) {
        String email = result.getRecord().getEmail();

        // Email is mandatory, so we only validate format if it exists
        // Missing check is already done by MandatoryFieldValidator
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            result.addError(createLog(result.getRecord(), "Invalid email format: '" + email + "'."));
        }
    }
}