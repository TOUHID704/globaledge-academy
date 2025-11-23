package com.globaledge.academy.lms.employee.imports.strategy;


import com.globaledge.academy.lms.employee.entity.Employee;
import com.globaledge.academy.lms.employee.imports.model.EmployeeImportRecord;

/**
 * Defines the contract for different employee import strategies.
 * This allows the application to handle record processing (create, update, etc.) in a pluggable manner.
 */
public interface EmployeeImportStrategy {

    /**
     * Processes a single employee record according to the specific strategy's logic.
     *
     * @param employee The Employee entity mapped from the Excel row.
     * @param record The original raw record from the Excel file, useful for context like logging.
     * @return The saved Employee entity if the operation was successful, or null if the record was skipped.
     */
    Employee process(Employee employee, EmployeeImportRecord record);
}