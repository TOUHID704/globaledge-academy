package com.globaledge.academy.lms.employee.imports.validator.chain;


import com.globaledge.academy.lms.employee.enums.ImportStrategyType;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Order 1: Validates the Import Type field.
 * This must be validated first as it determines the import strategy.
 *
 * Validation Rules:
 * - Missing -> ERROR
 * - Invalid (not 101, 102, or 103) -> ERROR
 */
@Component
@Order(1)
public class StrategyCodeValidator implements ValidationChain {

    @Override
    public void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch) {
        String code = result.getRecord().getImportType();

        // Check if Import Type is missing
        if (code == null || code.trim().isEmpty()) {
            result.addError(createLog(result.getRecord(), "Mandatory field 'Import Type' is missing or empty."));
            return;
        }

        // Check if the code from the Excel file maps to a valid enum constant
        if (ImportStrategyType.fromCode(code) == null) {
            result.addError(createLog(result.getRecord(), "Invalid Import Type code: '" + code + "'. Must be 101, 102, or 103."));
        }
    }
}