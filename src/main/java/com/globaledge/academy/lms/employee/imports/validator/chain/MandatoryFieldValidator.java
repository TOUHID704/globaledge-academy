package com.globaledge.academy.lms.employee.imports.validator.chain;


import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Order 2: Validates that all mandatory fields are present.
 *
 * Mandatory Fields:
 * - Employee ID
 * - First Name
 * - Last Name
 * - Email
 * - Department
 * - Designation
 * - Date of Joining
 *
 * Validation Rules:
 * - Missing -> ERROR
 */
@Component
@Order(2)
public class MandatoryFieldValidator implements ValidationChain {

    @Override
    public void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch) {
        var record = result.getRecord();

        // Use LinkedHashMap to maintain order
        Map<String, Function<EmployeeImportRecord, String>> mandatoryFields = new LinkedHashMap<>();
        mandatoryFields.put("Employee ID", EmployeeImportRecord::getEmployeeId);
        mandatoryFields.put("First Name", EmployeeImportRecord::getFirstName);
        mandatoryFields.put("Last Name", EmployeeImportRecord::getLastName);
        mandatoryFields.put("Email", EmployeeImportRecord::getEmail);
        mandatoryFields.put("Department", EmployeeImportRecord::getDepartment);
        mandatoryFields.put("Designation", EmployeeImportRecord::getDesignation);
        mandatoryFields.put("Date of Joining", EmployeeImportRecord::getDateOfJoining);

        mandatoryFields.forEach((fieldName, extractor) -> {
            String value = extractor.apply(record);
            if (value == null || value.trim().isEmpty()) {
                result.addError(createLog(record, "Mandatory field '" + fieldName + "' is missing or empty."));
            }
        });
    }
}