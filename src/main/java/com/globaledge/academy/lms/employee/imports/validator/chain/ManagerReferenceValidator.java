package com.globaledge.academy.lms.employee.imports.validator.chain;



import com.globaledge.academy.lms.employee.enums.ImportLogLevel;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportValidationResult;
import com.globaledge.academy.lms.employee.imports.validator.ValidationChain;
import com.globaledge.academy.lms.employee.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Order 6: Validates Manager ID references.
 *
 * Manager ID (Optional):
 * - Missing -> WARNING
 * - Not found in DB or batch -> WARNING
 */
@Component
@Order(6)
@RequiredArgsConstructor
public class ManagerReferenceValidator implements ValidationChain {

    private final EmployeeRepository employeeRepository;

    @Override
    public void validate(EmployeeImportValidationResult result, Set<String> employeeIdsInBatch) {
        String managerId = result.getRecord().getManagerId();

        // Check if Manager ID is missing
        if (managerId == null || managerId.trim().isEmpty()) {
            // Optional field missing -> WARNING
            result.addWarning(createLog(result.getRecord(), "Optional field 'Manager ID' is missing or empty.", ImportLogLevel.WARNING));
            return;
        }

        // If Manager ID is provided, validate it exists
        boolean existsInDb = employeeRepository.existsByEmployeeId(managerId);
        boolean existsInBatch = employeeIdsInBatch.contains(managerId);

        if (!existsInDb && !existsInBatch) {
            // Manager ID not found -> WARNING
            result.addWarning(createLog(result.getRecord(), "Manager ID '" + managerId + "' not found in the database or in the current import file. Linking will be skipped.", ImportLogLevel.WARNING));
        }
    }
}